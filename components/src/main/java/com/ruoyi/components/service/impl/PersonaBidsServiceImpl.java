package com.ruoyi.components.service.impl;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.*;
import com.ruoyi.components.domain.IndustryType;
import com.ruoyi.components.domain.PersonaBids;
import com.ruoyi.components.domain.bo.PublicBiddingURL;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import com.ruoyi.components.mapper.CustomersVoMapper;
import com.ruoyi.components.mapper.IndustryTypeMapper;
import com.ruoyi.components.mapper.PersonaBidsMapper;
import com.ruoyi.components.service.IPersonaBidsService;
import com.ruoyi.components.utils.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ruoyi.common.enums.UrlAddressEnum.TOKEN_API;

/**
 * 招投标数据Service业务层处理
 *
 * @author Leeyq
 * @date 2022-08-25
 */
@Service
public class PersonaBidsServiceImpl implements IPersonaBidsService
{
    private static final Logger logger = LoggerFactory.getLogger(PersonaBidsServiceImpl.class);
    private static final String COMPANY_LISTS = "www.ccgp.gov.cn";
    private static final String INDUSTRY_TYPE = "cus.persona.industry.type";

    //招投标网站详情地址（需爬取数据的地址）
    private static final String DETAIL_URL = "DOCPUBURL";

    @Autowired
    private PersonaBidsMapper personaBidsMapper;
    @Autowired
    private CustomersVoMapper customerVoMapper;
    @Autowired
    private MybatisBatchUtils mybatisBatchUtils;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private IndustryTypeMapper industryTypeMapper;

    //查询行业类型
    public List<IndustryType> getIndustryType(){
        //添加缓存--缓存判定行業類型
        List<IndustryType> industrys = new ArrayList<>();
        String typekey = INDUSTRY_TYPE;
        List<Object> cacheType = redisCache.getCacheList(typekey);
        if (!cacheType.isEmpty()){
            for (Object obj : cacheType){
                industrys.add((IndustryType) obj);
            }
        }else{
            //查询行業類型
            industrys =industryTypeMapper.selectIndustryTypeList(new IndustryType());
            if (!industrys.isEmpty()){
                redisCache.setCacheList(typekey, industrys);
                redisCache.expire(typekey, 86400, TimeUnit.SECONDS);
            }
        }

        return industrys;
    }

    //判断行业
    public String analyseIndustryType(List<IndustryType> industrys, String title, String purchaser, String content){
        //判断行业类型
        String industry ="";
        //招投标匹配行业类型关键字（bidKeywordFirst优先级：1）
        if (StringUtils.isNotEmpty(title)) {
            industry = IndustryTypeUtils.decideIndustryType(industrys, title);
        }
        if(StringUtils.isNotEmpty(purchaser) && StringUtils.isEmpty(industry)) {
            industry = IndustryTypeUtils.decideIndustryType(industrys, purchaser);
        }
        if(StringUtils.isNotEmpty(content) && StringUtils.isEmpty(industry)) {
            industry = IndustryTypeUtils.decideIndustryType(industrys, content);
        }
        //招投标匹配行业类型关键字（bidKeywordSecond优先级：2）
        if(StringUtils.isNotEmpty(title) && StringUtils.isEmpty(industry)) {
            industry = IndustryTypeUtils.judgeIndustryType(industrys, title);
        }
        if(StringUtils.isNotEmpty(purchaser) && StringUtils.isEmpty(industry)) {
            industry = IndustryTypeUtils.judgeIndustryType(industrys, purchaser);
        }
        if(StringUtils.isNotEmpty(content) && StringUtils.isEmpty(industry)){
            industry = IndustryTypeUtils.judgeIndustryType(industrys, content);
        }
        return industry;
    }

    /**
     * 新增招投标数据
     *
     * @param personaBids 招投标数据
     * @return 结果
     */
    @Override
    public AjaxResult insertPersonaBids(PersonaBids personaBids) {
        //调用天眼查-新增入库
        String keyword =personaBids.getKeyword();
        //获取近一年的时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -3);
        Date y = calendar.getTime();
        String publishStartTime = format.format(y);
        //请求页码
        int pageNum = 0;
        //查询企业列表
        List<PersonaBids> bids =new ArrayList<PersonaBids>();
        //是否终止循环变量
        boolean endLoop = false;

        //循环的次数
        int loopNum = 0;
        while (!endLoop){
            pageNum++;
            String token = "7b1f73a2-3709-4be1-ae59-6536d47aae1b";
            String url = "https://open.api.tianyancha.com/services/open/m/bids/search?pageSize=20&pageNum=" + pageNum + "&keyword=" + keyword+"&publishStartTime=" + publishStartTime + "&type=1,2,4";
            //System.out.println(executeGet(url, token));
            //获取查询数据
            String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());
            if (StringUtils.isEmpty(result)){
                return AjaxResult.error(500,"请求用户信息接口为空，请联系系统管理员进行处理。");
            }
//        logger.info("接口请求返回参数：{}",result);
            //处理返回参数
            JSONObject resultObj = JSONObject.parseObject(result);
            //判断异常信息
//            TycApiUtils.checkCodeMsg(code);
            int total = Integer.parseInt(resultObj.getJSONObject("result").getString("total"));
            //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
            loopNum = total/20 + 1;

            //设置最多循环次数，避免出现过多请求接口导致收费过多
            if (loopNum > 10){
                endLoop = true;
            }
            else if (pageNum <= loopNum){
                //获取最后一页，获取完后结循环
                if (pageNum == loopNum){
                    endLoop = true;
                }
                JSONArray lists=resultObj.getJSONObject("result").getJSONArray("items");

                JSONObject temp = new JSONObject();
                if (lists.size()>0) {
                    for (Object object : lists) {
                        temp = JSONObject.parseObject(object.toString());
                        PersonaBids bid =new PersonaBids();
                        //cy.setCompanytype(Long.valueOf(temp.getString("")));
                        bid.setPublishTime(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishTime"))));
                        bid.setBidUrl(temp.getString("bidUrl"));
                        //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                        bid.setPurchaser(StringUtils.subStringAfter(temp.getString("purchaser"),"："));
                        bid.setLink(temp.getString("link"));
                        bid.setTitle(temp.getString("title"));
                        bid.setUuid(temp.getString("uuid"));
                        bid.setContent(temp.getString("content"));
                        //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                        bid.setProxy(StringUtils.subStringAfter(temp.getString("proxy"),"："));
                        bid.setAbs(temp.getString("abs"));
                        bid.setIntro(temp.getString("intro"));
                        bid.setType(temp.getString("type"));
                        bid.setProvince(temp.getString("province"));
                        //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                        bid.setBidWinner(StringUtils.subStringAfter(temp.getString("bidWinner"),"："));
                        bid.setKeyword(keyword);
                        bid.setSource("https://open.tianyancha.com/open/1063");
                        //截取招投标中的金额（只包含数字和小数点）
                        bid.setBidAmount(StringUtils.subStringNumber(temp.getString("bidAmount")));
                        JSONArray jsonArray = JSONArray.parseArray(temp.getString("companies"));
                        if (jsonArray != null){
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < jsonArray.size(); i++){
                                JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
                                stringBuilder.append(jsonObject.getString("cname"));
                                stringBuilder.append(",");
                            }
                            //去除最后面的逗号
                            stringBuilder.deleteCharAt(stringBuilder.length() -1);
                            bid.setCompetitiveCompany(stringBuilder.toString());
                        }
                        bids.add(bid);
                    }
                }
            }
            else {
                endLoop = true;
            }
        }

        int num=0;
        if (CollectionUtils.isNotEmpty(bids)){
            num = mybatisBatchUtils.batchUpdateOrInsert(bids, PersonaBidsMapper.class,
                    (item, personaBidsMapper) -> personaBidsMapper.insertPersonaBids(item));
        }
        return AjaxResult.success(num);
    }

    @Override
    public int insertCustomerPersonaBids(List<String> customersNameList) {
        //新增数量
        int num = 0;
        for (String customerName : customersNameList) {
            //调用天眼查-新增入库
            String keyword = customerName;
            //获取近一年的时间
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, -1);
            Date y = calendar.getTime();
            String publishStartTime = format.format(y);
            //请求页码
            int pageNum = 0;
            //查询企业列表
            List<PersonaBids> bids = new ArrayList<PersonaBids>();
            //是否终止循环变量
            boolean endLoop = false;

            //循环的次数
            int loopNum = 0;

            //重连次数
            int reconnect = 0;
            while (!endLoop) {
                pageNum++;
                String url = "https://open.api.tianyancha.com/services/open/m/bids/search?pageSize=20&pageNum=" + pageNum + "&keyword=" + keyword + "&publishStartTime=" + publishStartTime + "&type=1,2,4";
                //获取查询数据
                try {
                    String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());
                    JSONObject resultObj = JSONObject.parseObject(result);
                    String code = resultObj.getString("error_code");
                    //判断异常信息
                    if (!"0".equals(code)){
                        break;
                    }
                    //获取不到数据，退出本次循环
                    String totalString = resultObj.getJSONObject("result").getString("total");
                    if (totalString == null){
                        continue;
                    }
                    int total = Integer.parseInt(totalString);
                    //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                    loopNum = total / 20 + 1;
                    //设置最多循环次数，避免出现过多请求接口导致收费过多
                    if (pageNum > 50) {
                        endLoop = true;
                    } else if (pageNum <= loopNum) {
                        //获取最后一页，获取完后结循环
                        if (pageNum == loopNum) {
                            endLoop = true;
                        }
                        JSONArray lists = resultObj.getJSONObject("result").getJSONArray("items");

                        JSONObject temp = new JSONObject();
                        if (lists.size() > 0) {
                            for (Object object : lists) {
                                temp = JSONObject.parseObject(object.toString());
                                PersonaBids bid = new PersonaBids();
                                //cy.setCompanytype(Long.valueOf(temp.getString("")));
                                bid.setPublishTime(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishTime"))));
                                bid.setBidUrl(temp.getString("bidUrl"));
                                //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                                bid.setPurchaser(StringUtils.subStringAfter(temp.getString("purchaser"), "："));
                                bid.setLink(temp.getString("link"));
                                bid.setTitle(temp.getString("title"));
                                bid.setUuid(temp.getString("uuid"));
                                bid.setContent(temp.getString("content"));
                                //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                                bid.setProxy(StringUtils.subStringAfter(temp.getString("proxy"), "："));
                                bid.setAbs(temp.getString("abs"));
                                bid.setIntro(temp.getString("intro"));
                                bid.setType(temp.getString("type"));
                                bid.setProvince(temp.getString("province"));
                                //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                                bid.setBidWinner(StringUtils.subStringAfter(temp.getString("bidWinner"), "："));
                                bid.setKeyword(keyword);
                                bid.setSource("https://open.tianyancha.com/open/1063");
                                //截取招投标中的金额（只包含数字和小数点）
                                bid.setBidAmount(StringUtils.subStringNumber(temp.getString("bidAmount")));
                                JSONArray jsonArray = JSONArray.parseArray(temp.getString("companies"));
                                if (jsonArray != null) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
                                        stringBuilder.append(jsonObject.getString("cname"));
                                        stringBuilder.append(",");
                                    }
                                    //去除最后面的逗号
                                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                                    bid.setCompetitiveCompany(stringBuilder.toString());
                                }
                                //System.out.println(temp.getString("bidAmount"));
                                bids.add(bid);
                                num = num + personaBidsMapper.insertPersonaBids(bid);
                            }
                        }
                    }
                    else {
                        endLoop = true;
                    }
                }catch (Exception e){
//                    将pageNum 减一，继续访问该页面
                    if (reconnect < 5){
                        pageNum = pageNum - 1;
                        endLoop = false;
                        reconnect++;
                    }else {
                        endLoop = true;
                        reconnect = 0;
                    }
                }
            }
        }
        return num;
    }

    /**
     * 采购单位招标数据
     */
    @Override
    public int savePurchaserPersonaBids(List<String> customersNameList) {
        //新增数量
        int num = 0;
        for (String customerName : customersNameList) {
            //调用天眼查-新增入库
            String keyword = customerName;
            //获取近一年的时间
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, -3);
            Date y = calendar.getTime();
            String publishStartTime = format.format(y);
            //请求页码
            int pageNum = 0;
            //查询企业列表
            List<PersonaBids> bids = new ArrayList<PersonaBids>();
            //是否终止循环变量
            boolean endLoop = false;

            //循环的次数
            int loopNum = 0;

            //重连次数
            int reconnect = 0;
            while (!endLoop) {
                pageNum++;
                //查询类别 1-标题 2-采购人 3-供应商
                String url ="http://open.api.tianyancha.com/services/open/m/bids/search?pageSize=20&pageNum=" + pageNum + "&keyword=" + keyword + "&searchType=2,3&publishEndTime="+DateUtils.getDate()+"&publishStartTime="+publishStartTime+ "&type=1,2,4";
                //获取查询数据
                try {
                    String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());
                    JSONObject resultObj = JSONObject.parseObject(result);
                    String code = resultObj.getString("error_code");
                    //判断异常信息
                    if (!"0".equals(code)){
                        break;
                    }
                    //获取不到数据，退出本次循环
                    String totalString = resultObj.getJSONObject("result").getString("total");
                    if (totalString == null){
                        continue;
                    }
                    int total = Integer.parseInt(totalString);
                    //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                    loopNum = total / 20 + 1;
                    //设置最多循环次数，避免出现过多请求接口导致收费过多
                    if (pageNum > 50) {
                        endLoop = true;
                    } else if (pageNum <= loopNum) {
                        //获取最后一页，获取完后结循环
                        if (pageNum == loopNum) {
                            endLoop = true;
                        }
                        JSONArray lists = resultObj.getJSONObject("result").getJSONArray("items");

                        JSONObject temp = new JSONObject();
                        if (lists.size() > 0) {
                            for (Object object : lists) {
                                temp = JSONObject.parseObject(object.toString());
                                PersonaBids bid = new PersonaBids();
                                //cy.setCompanytype(Long.valueOf(temp.getString("")));
                                bid.setPublishTime(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishTime"))));
                                bid.setBidUrl(temp.getString("bidUrl"));
                                //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                                bid.setPurchaser(StringUtils.subStringAfter(temp.getString("purchaser"), "："));
                                bid.setLink(temp.getString("link"));
                                bid.setTitle(temp.getString("title"));
                                bid.setUuid(temp.getString("uuid"));
                                bid.setContent(temp.getString("content"));
                                //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                                bid.setProxy(StringUtils.subStringAfter(temp.getString("proxy"), "："));
                                bid.setAbs(temp.getString("abs"));
                                bid.setIntro(temp.getString("intro"));
                                bid.setType(temp.getString("type"));
                                bid.setProvince(temp.getString("province"));
                                //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                                bid.setBidWinner(StringUtils.subStringAfter(temp.getString("bidWinner"), "："));
                                bid.setKeyword(keyword);
                                bid.setSource("https://open.tianyancha.com/open/1063");
                                //截取招投标中的金额（只包含数字和小数点）
                                bid.setBidAmount(StringUtils.subStringNumber(temp.getString("bidAmount")));
                                JSONArray jsonArray = JSONArray.parseArray(temp.getString("companies"));
                                if (jsonArray != null) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
                                        stringBuilder.append(jsonObject.getString("cname"));
                                        stringBuilder.append(",");
                                    }
                                    //去除最后面的逗号
                                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                                    bid.setCompetitiveCompany(stringBuilder.toString());
                                }
                                //System.out.println(temp.getString("bidAmount"));
                                bids.add(bid);
                                num = num + personaBidsMapper.insertPersonaBids(bid);
                            }
                        }
                    }
                    else {
                        endLoop = true;
                    }
                }catch (Exception e){
//                    将pageNum 减一，继续访问该页面
                    if (reconnect < 5){
                        pageNum = pageNum - 1;
                        endLoop = false;
                        reconnect++;
                    }else {
                        endLoop = true;
                        reconnect = 0;
                    }
                }
            }
        }
        return num;
    }

    @Override
    public int savePurchaserPersonaBidsByCompany(String keyword) {
        //获取近一年的时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -3);
        Date y = calendar.getTime();
        String publishStartTime = format.format(y);
        //请求页码
        int pageNum = 0;
        //查询企业列表
        List<PersonaBids> bids = new ArrayList<>();
        //是否终止循环变量
        boolean endLoop = false;

        //循环的次数
        int loopNum = 0;
        int num = 0;
        //重连次数
        int reconnect = 0;
        while (!endLoop) {
            pageNum++;
            //查询类别 1-标题 2-采购人 3-供应商
            String url ="http://open.api.tianyancha.com/services/open/m/bids/search?pageSize=20&pageNum=" + pageNum + "&keyword=" + keyword + "&searchType=2,3&publishEndTime="+DateUtils.getDate()+"&publishStartTime="+publishStartTime+ "&type=1,2,4";
            //获取查询数据
            try {
                String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());
                JSONObject resultObj = JSONObject.parseObject(result);
                String code = resultObj.getString("error_code");
                //判断异常信息
                if (!"0".equals(code)){
                    break;
                }
                //获取不到数据，退出本次循环
                String totalString = resultObj.getJSONObject("result").getString("total");
                if (totalString == null){
                    continue;
                }
                int total = Integer.parseInt(totalString);
                //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                loopNum = total / 20 + 1;
                //设置最多循环次数，避免出现过多请求接口导致收费过多
                if (pageNum > 50) {
                    endLoop = true;
                } else if (pageNum <= loopNum) {
                    //获取最后一页，获取完后结循环
                    if (pageNum == loopNum) {
                        endLoop = true;
                    }
                    JSONArray lists = resultObj.getJSONObject("result").getJSONArray("items");

                    if (lists.size() > 0) {
                        for (Object object : lists) {
                            JSONObject temp = JSONObject.parseObject(object.toString());
                            PersonaBids bid = new PersonaBids();
                            //cy.setCompanytype(Long.valueOf(temp.getString("")));
                            bid.setPublishTime(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishTime"))));
                            bid.setBidUrl(temp.getString("bidUrl"));
                            //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                            bid.setPurchaser(StringUtils.subStringAfter(temp.getString("purchaser"), "："));
                            bid.setLink(temp.getString("link"));
                            bid.setTitle(temp.getString("title"));
                            bid.setUuid(temp.getString("uuid"));
                            bid.setContent(temp.getString("content"));
                            //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                            bid.setProxy(StringUtils.subStringAfter(temp.getString("proxy"), "："));
                            bid.setAbs(temp.getString("abs"));
                            bid.setIntro(temp.getString("intro"));
                            bid.setType(temp.getString("type"));
                            bid.setProvince(temp.getString("province"));
                            //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                            bid.setBidWinner(StringUtils.subStringAfter(temp.getString("bidWinner"), "："));
                            bid.setKeyword(keyword);
                            bid.setSource("https://open.tianyancha.com");
                            bid.setBidAmount(StringUtils.subStringNumber(temp.getString("bidAmount")));
                            bid.setCreateTime(new Date());
                            JSONArray jsonArray = JSONArray.parseArray(temp.getString("companies"));
                            if (jsonArray != null) {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
                                    stringBuilder.append(jsonObject.getString("cname"));
                                    stringBuilder.append(",");
                                }
                                //去除最后面的逗号
                                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                                bid.setCompetitiveCompany(stringBuilder.toString());
                            }
                            //System.out.println(temp.getString("bidAmount"));
                            bids.add(bid);
                            //num = personaBidsMapper.insertPersonaBids(bid);
                        }
                    }
                }
                else {
                    endLoop = true;
                }
            }catch (Exception e){
                e.printStackTrace();
//                    将pageNum 减一，继续访问该页面
                if (reconnect < 5){
                    pageNum = pageNum - 1;
                    endLoop = false;
                    reconnect++;
                }else {
                    endLoop = true;
                    reconnect = 0;
                }
            }
        }
        int i = personaBidsMapper.saveBatchPersonaBids(bids);
        logger.info("录入：{}", i);
        return num;
    }

    @Override
    public int upPurchaserPersonaBidsByCompany(String keyword,String publishStartTime) {
        //请求页码
        int pageNum = 0;
        //查询企业列表
        List<PersonaBids> bids = new ArrayList<>();
        //是否终止循环变量
        boolean endLoop = false;

        //循环的次数
        int loopNum = 0;
        //重连次数
        int reconnect = 0;
        while (!endLoop) {
            pageNum++;
            String token = "7b1f73a2-3709-4be1-ae59-6536d47aae1b";
            //查询类别 1-标题 2-采购人 3-供应商
            String url ="http://open.api.tianyancha.com/services/open/m/bids/search?pageSize=20&pageNum=" + pageNum + "&keyword=" + keyword + "&searchType=2,3&publishEndTime="+DateUtils.getDate()+"&publishStartTime="+publishStartTime+ "&type=1,2,4";
            //获取查询数据
            try {
                String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());
                JSONObject resultObj = JSONObject.parseObject(result);
                String code = resultObj.getString("error_code");
                //判断异常信息
                if (!"0".equals(code)){
                    break;
                }
                //获取不到数据，退出本次循环
                String totalString = resultObj.getJSONObject("result").getString("total");
                if (totalString == null){
                    continue;
                }
                int total = Integer.parseInt(totalString);
                //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                loopNum = total / 20 + 1;
                //设置最多循环次数，避免出现过多请求接口导致收费过多
                if (pageNum > 50) {
                    endLoop = true;
                } else if (pageNum <= loopNum) {
                    //获取最后一页，获取完后结循环
                    if (pageNum == loopNum) {
                        endLoop = true;
                    }
                    JSONArray lists = resultObj.getJSONObject("result").getJSONArray("items");

                    if (lists.size() > 0) {
                        for (Object object : lists) {
                            JSONObject temp = JSONObject.parseObject(object.toString());
                            PersonaBids bid = new PersonaBids();
                            //cy.setCompanytype(Long.valueOf(temp.getString("")));
                            bid.setPublishTime(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishTime"))));
                            bid.setBidUrl(temp.getString("bidUrl"));
                            //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                            bid.setPurchaser(StringUtils.subStringAfter(temp.getString("purchaser"), "："));
                            bid.setLink(temp.getString("link"));
                            bid.setTitle(temp.getString("title"));
                            bid.setUuid(temp.getString("uuid"));
                            bid.setContent(temp.getString("content"));
                            //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                            bid.setProxy(StringUtils.subStringAfter(temp.getString("proxy"), "："));
                            bid.setAbs(temp.getString("abs"));
                            bid.setIntro(temp.getString("intro"));
                            bid.setType(temp.getString("type"));
                            bid.setProvince(temp.getString("province"));
                            //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                            bid.setBidWinner(StringUtils.subStringAfter(temp.getString("bidWinner"), "："));
                            bid.setKeyword(keyword);
                            bid.setCreateTime(new Date());
                            bid.setSource("https://open.tianyancha.com/open/1063");
                            bid.setBidAmount(StringUtils.subStringNumber(temp.getString("bidAmount")));
                            JSONArray jsonArray = JSONArray.parseArray(temp.getString("companies"));
                            if (jsonArray != null) {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
                                    stringBuilder.append(jsonObject.getString("cname"));
                                    stringBuilder.append(",");
                                }
                                //去除最后面的逗号
                                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                                bid.setCompetitiveCompany(stringBuilder.toString());
                            }
                            //System.out.println(temp.getString("bidAmount"));
                            bids.add(bid);
                        }
                    }
                }
                else {
                    endLoop = true;
                }
            }catch (Exception e){
//                    将pageNum 减一，继续访问该页面
                if (reconnect < 5){
                    pageNum = pageNum - 1;
                    endLoop = false;
                    reconnect++;
                }else {
                    endLoop = true;
                    reconnect = 0;
                }
            }

        }
        int i = personaBidsMapper.saveBatchPersonaBids(bids);
        logger.info("插入：{}", i);
        return i;
    }

    /**
     * demo
     * @param personaBids
     * @return
     */
    @Override
    public AjaxResult insertPersonaGovBids(PersonaBids personaBids) throws UnsupportedEncodingException {
        //添加缓存--缓存未查询单位名称
        List<String> customers = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        String localkey = COMPANY_LISTS+":win";
        List<Object> cacheList = redisCache.getCacheList(localkey);
        if (!cacheList.isEmpty()){
            for (Object obj : cacheList){
                customers.add((String)obj);
            }
        }else{
            //查询客户单位列表
            customers =customerVoMapper.selectCustomerUnitList();
            if (!customers.isEmpty()){
                redisCache.setCacheList(localkey, customers);
                redisCache.expire(localkey, 86400, TimeUnit.SECONDS);
            }
        }
        String url1 ="http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=";
        int num=0;
        //获取近三月查询时间 &start_time=2021%3A01%3A01&end_time=2022%3A10%3A15
        String beginTime = DateUtils.lastMonthTime();
        String endTime = DateUtils.getDate();
        String sleTime = "&start_time="+beginTime.replace("-", "%3A")+"&end_time="+endTime.replace("-", "%3A");
        try {
            for (String unit: customers){
                logger.info("单位名称: " +unit);
                //搜全文-中标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜标题-中标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜全文-招标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜标题-招标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                String kw = CodeUtils.getUrlCode(unit);
                String url2 ="&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw="+kw+sleTime+"&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=";
                List<PersonaBids> bids = CrawlerBidInfoUtil.getSpiderBidInfoList(url1,url2,unit);
                //防止频繁访问（模拟网络延迟）
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (bids.size()>0){
                    num = mybatisBatchUtils.batchUpdateOrInsert(bids, PersonaBidsMapper.class,
                            (item, personaBidsMapper) -> personaBidsMapper.insertPersonaBids(item));
                }
                //剔除查询过的单位
                delList.add(unit);
            }
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            //刷新缓存
            redisCache.deleteObject(localkey);
            //剔除查询过的单位
            customers.removeAll(delList);
            if (!customers.isEmpty() && customers.size()>0){
                redisCache.setCacheList(localkey, customers);
                redisCache.expire(localkey, 86400, TimeUnit.SECONDS);
            }
        }
        return AjaxResult.success(num);
    }

    /**
     *中国政府采购网-标题-招标公告
     * @param personaBids
     * @return
     */
    @Override
    public AjaxResult insertPersonaGovTitleCallBids(PersonaBids personaBids) {
        //添加缓存--缓存未查询单位名称
        List<String> customers = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        String localkey = COMPANY_LISTS+"title:call";
        List<Object> cacheList = redisCache.getCacheList(localkey);
        if (!cacheList.isEmpty()){
            for (Object obj : cacheList){
                customers.add((String)obj);
            }
        }else{
            //查询客户单位列表
            customers =customerVoMapper.selectCustomerUnitList();
            if (!customers.isEmpty()){
                redisCache.setCacheList(localkey, customers);
                redisCache.expire(localkey, 86400, TimeUnit.SECONDS);
            }
        }
        String url1="http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=";
        int num=0;
        //获取近三月查询时间 &start_time=2021%3A01%3A01&end_time=2022%3A10%3A15
        String beginTime = DateUtils.lastMonthTime();
        String endTime = DateUtils.getDate();
        String sleTime = "&start_time="+beginTime.replace("-", "%3A")+"&end_time="+endTime.replace("-", "%3A");
        try{
            for (String unit: customers){
                logger.info("单位名称: " +unit);
                //搜全文-中标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜标题-中标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜全文-招标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜标题-招标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                String kw = CodeUtils.getUrlCode(unit);
                String url2 ="&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw="+kw+sleTime+"&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=";
                List<PersonaBids> bids =CrawlerBidInfoUtil.getSpiderBidInfoList(url1,url2,unit);
                //防止频繁访问（模拟网络延迟）
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (bids.size()>0){
                    num = mybatisBatchUtils.batchUpdateOrInsert(bids, PersonaBidsMapper.class,
                            (item, personaBidsMapper) -> personaBidsMapper.insertPersonaBids(item));
                }
                //剔除查询过的单位
                delList.add(unit);
            }
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            //刷新缓存
            redisCache.deleteObject(localkey);
            //剔除查询过的单位
            customers.removeAll(delList);
            if (!customers.isEmpty() && customers.size()>0){
                redisCache.setCacheList(localkey, customers);
                redisCache.expire(localkey, 86400, TimeUnit.SECONDS);
            }
        }
        return AjaxResult.success(num);
    }

    /**
     *中国政府采购网-标题-中标公告
     * @param personaBids
     * @return
     */
    @Override
    public AjaxResult insertPersonaGovTitleWinBids(PersonaBids personaBids) {
        //添加缓存--缓存未查询单位名称
        List<String> customers = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        String localkey = COMPANY_LISTS+"title:win";
        List<Object> cacheList = redisCache.getCacheList(localkey);
        if (!cacheList.isEmpty()){
            for (Object obj : cacheList){
                customers.add((String)obj);
            }
        }else{
            //查询客户单位列表
            customers =customerVoMapper.selectCustomerUnitList();
            if (!customers.isEmpty()){
                redisCache.setCacheList(localkey, customers);
                redisCache.expire(localkey, 86400, TimeUnit.SECONDS);
            }
        }
        String url1="http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=";
        //获取行业类型列表
        List<IndustryType> industrys =getIndustryType();
        int num=0;
        //获取近三月查询时间 &start_time=2021%3A01%3A01&end_time=2022%3A10%3A15
        String beginTime = DateUtils.lastMonthTime();
        String endTime = DateUtils.getDate();
        String sleTime = "&start_time="+beginTime.replace("-", "%3A")+"&end_time="+endTime.replace("-", "%3A");
        try{
            for (String unit: customers){
                logger.info("单位名称: " +unit);
                //搜全文-中标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜标题-中标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜全文-招标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜标题-招标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                String kw = CodeUtils.getUrlCode(unit);
                String url2 ="&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw="+kw+sleTime+"&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=";
                List<PersonaBids> bids =CrawlerBidInfoUtil.getSpiderInfoList(url1,url2,unit);
                //数据处理--判断行业类型
                bids.forEach(personaBids1 -> {
                    //判断行业类型
                    String industry =analyseIndustryType(industrys, personaBids1.getTitle(), personaBids1.getPurchaser(), personaBids1.getContent());
                    personaBids1.setIndustryType(industry);
                    personaBids1.setContent(null);
                });
                //防止频繁访问（模拟网络延迟）
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (bids.size()>0){
                    num = mybatisBatchUtils.batchUpdateOrInsert(bids, PersonaBidsMapper.class,
                            (item, personaBidsMapper) -> personaBidsMapper.insertPersonaBids(item));
                }
                //剔除查询过的单位
                delList.add(unit);
            }
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            //刷新缓存
            redisCache.deleteObject(localkey);
            //剔除查询过的单位
            customers.removeAll(delList);
            if (!customers.isEmpty() && customers.size()>0){
                redisCache.setCacheList(localkey, customers);
                redisCache.expire(localkey, 86400, TimeUnit.SECONDS);
            }
        }
        return AjaxResult.success(num);
    }

    /**
     *中国政府采购网-全文-招标公告
     * @param personaBids
     * @return
     */
    @Override
    public AjaxResult insertPersonaGovContentCallBids(PersonaBids personaBids) {
        //添加缓存--缓存未查询单位名称
        List<String> customers = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        String localkey = COMPANY_LISTS+"content:call";
        List<Object> cacheList = redisCache.getCacheList(localkey);
        if (!cacheList.isEmpty()){
            for (Object obj : cacheList){
                customers.add((String)obj);
            }
        }else{
            //查询客户单位列表
            customers =customerVoMapper.selectCustomerUnitList();
            if (!customers.isEmpty()){
                redisCache.setCacheList(localkey, customers);
                redisCache.expire(localkey, 86400, TimeUnit.SECONDS);
            }
        }
        String url1 ="http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=";
        int num=0;
        //获取近三月查询时间 &start_time=2021%3A01%3A01&end_time=2022%3A10%3A15
        String beginTime = DateUtils.lastMonthTime();
        String endTime = DateUtils.getDate();
        String sleTime = "&start_time="+beginTime.replace("-", "%3A")+"&end_time="+endTime.replace("-", "%3A");
        try{
            for (String unit: customers){
                logger.info("单位名称: " +unit);
                //搜全文-中标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜标题-中标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜全文-招标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜标题-招标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                String kw = CodeUtils.getUrlCode(unit);
                String url2 ="&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw="+kw+sleTime+"&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=";
                List<PersonaBids> bids =CrawlerBidInfoUtil.getSpiderBidInfoList(url1, url2, unit);
                //防止频繁访问（模拟网络延迟）
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (bids.size()>0){
                    num = mybatisBatchUtils.batchUpdateOrInsert(bids, PersonaBidsMapper.class,
                            (item, personaBidsMapper) -> personaBidsMapper.insertPersonaBids(item));
                }
                //剔除查询过的单位
                delList.add(unit);
            }
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            //刷新缓存
            redisCache.deleteObject(localkey);
            //剔除查询过的单位
            customers.removeAll(delList);
            if (!customers.isEmpty() && customers.size()>0){
                redisCache.setCacheList(localkey, customers);
                redisCache.expire(localkey, 86400, TimeUnit.SECONDS);
            }
        }
        return AjaxResult.success(num);
    }

    /**
     *中国政府采购网-全文-中标公告
     * @param personaBids
     * @return
     */
    @Override
    public AjaxResult insertPersonaGovContentWinBids(PersonaBids personaBids) {
        //添加缓存--缓存未查询单位名称
        List<String> customers = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        String localkey = COMPANY_LISTS+"content:win";
        List<Object> cacheList = redisCache.getCacheList(localkey);
        if (!cacheList.isEmpty()){
            for (Object obj : cacheList){
                customers.add((String)obj);
            }
        }else{
            //查询客户单位列表
            customers =customerVoMapper.selectCustomerUnitList();
            if (!customers.isEmpty()){
                redisCache.setCacheList(localkey, customers);
                redisCache.expire(localkey, 86400, TimeUnit.SECONDS);
            }
        }
        String url1 ="http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=";
        //获取行业类型列表
        List<IndustryType> industrys =getIndustryType();
        int num=0;
        //获取近三月查询时间 &start_time=2021%3A01%3A01&end_time=2022%3A10%3A15
        String beginTime = DateUtils.lastMonthTime();
        String endTime = DateUtils.getDate();
        String sleTime = "&start_time="+beginTime.replace("-", "%3A")+"&end_time="+endTime.replace("-", "%3A");
        try{
            for (String unit: customers){
                logger.info("单位名称: " +unit);
                //搜全文-中标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜标题-中标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜全文-招标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=2&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                //搜标题-招标公告
                //http://search.ccgp.gov.cn/bxsearch?searchtype=1&page_index=1&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=1&dbselect=bidx&kw=%E8%BF%90%E7%BB%B4&start_time=2023%3A07%3A24&end_time=2023%3A10%3A24&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=
                String kw = CodeUtils.getUrlCode(unit);
                String url2 ="&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=7&dbselect=bidx&kw="+kw+sleTime+"&timeType=6&displayZone=&zoneId=&pppStatus=0&agentName=";

                List<PersonaBids> bids =CrawlerBidInfoUtil.getSpiderInfoList(url1, url2, unit);
                //数据处理--判断行业类型
                bids.forEach(personaBids1 -> {
                    //判断行业类型
                    String industry =analyseIndustryType(industrys, personaBids1.getTitle(), personaBids1.getPurchaser(), personaBids1.getContent());
                    personaBids1.setIndustryType(industry);
                    personaBids1.setContent(null);
                });
                //防止频繁访问（模拟网络延迟）
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (bids.size()>0){
                    num = mybatisBatchUtils.batchUpdateOrInsert(bids, PersonaBidsMapper.class,
                            (item, personaBidsMapper) -> personaBidsMapper.insertPersonaBids(item));
                }
                //剔除查询过的单位
                delList.add(unit);
            }
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            //刷新缓存
            redisCache.deleteObject(localkey);
            //剔除查询过的单位
            customers.removeAll(delList);
            if (!customers.isEmpty() && customers.size()>0){
                redisCache.setCacheList(localkey, customers);
                redisCache.expire(localkey, 86400, TimeUnit.SECONDS);
            }
        }
        return AjaxResult.success(num);
    }

    @Override
    public int addGxPublicResourcesByMultithreading(int startPage, int endPage) {
        int num = 0;
        try {
            List<IndustryType> industryTypes = industryTypeMapper.selectIndustryTypeList(new IndustryType());
            List<PersonaBids> personaBids = addGXPublicResources(startPage, endPage,industryTypes);
            if (!personaBids.isEmpty()){
                num = personaBidsMapper.saveBatchPersonaBids(personaBids);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return num;
    }

    @Override
    public AjaxResult addGxPublicResourcesBiding() {
        ArrayList<PublicBiddingURL> publicBiddingURL = BidConstants.getPublicBiddingURL();
        for (PublicBiddingURL biddingURL : publicBiddingURL) {
            //构造访问招标公告url
            String url = "http://gxggzy.gxzf.gov.cn/igs/front/search/list.html?=&filter[DOCTITLE]=&pageNumber=1&pageSize=10&index=gxggzy_jyfw&type=jyfw&filter[parentparentid]="+biddingURL.getParentId()+"&filter[parentchnldesc]="+biddingURL.getParentChnlDesc()+"&filter[chnldesc]="+biddingURL.getChnlDesc()+"&filter[SITEID]=234&orderProperty=PUBDATE&orderDirection=desc&filter[AVAILABLE]=true";
            BufferedReader bufferedReader = null;
            StringBuilder result = new StringBuilder();
            try {
                //获取DefaultHttpClient请求;
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                InputStream inputStream = response.getEntity().getContent();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String line;
                while(null != (line = bufferedReader.readLine())) {
                    result.append(line);
                }
                //处理返回参数
                JSONObject resultObj = JSONObject.parseObject(result.toString());
                int totalPage = Integer.parseInt(resultObj.getJSONObject("page").getString("totalPages"));
                int loopNum = 0;
                if (totalPage > 1000){
                    loopNum = 1000;
                }else {
                    loopNum = totalPage;
                }
                int startPage = 0;
                int endPage = 0;
                while (endPage < loopNum){

                }
                JSONArray contents = resultObj.getJSONObject("page").getJSONArray("content");

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 爬取一条广西公共资源交易平台的招投标中标数据
     */
    @Override
    public void addOneGXPublicResources() {
        try {
            List<IndustryType> industryTypes = industryTypeMapper.selectIndustryTypeList(new IndustryType());
            List<PersonaBids> personaBids = SpiderBidsInfoUtil.addOneGXPublicResources(industryTypes);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 批量新增全国公共资源交易平台（广西壮族自治区）招标、中标数据
     * @param equal 查询条件
     */
    @Async
    @Override
    public void insertGxggzyBids(String equal) {
        logger.info("-----------" + "["+ Thread.currentThread().getName() +"]" +
                "开始爬取全国公共资源交易平台（广西壮族自治区）-----------");
        long start = DateUtils.getNowDate().getTime();
        // 查询客户单位列表
        List<String> customers = new ArrayList<>();
        List<IndustryType> industryTypeList = new ArrayList<>();
//        List<String> surplus = new ArrayList<>();
        synchronized (this) {
            customers = customerVoMapper.selectCustomerUnitList();
            industryTypeList = industryTypeMapper.selectIndustryTypeList(new IndustryType());
        }
        // 当前线程爬取任务开始，创建只属于当前线程使用的浏览器驱动
        WebDriver driver = SeleniumUtil.getDriver();
//        List<String> customers = new ArrayList<>();
//        customers.add("中国电信");
//        customers.add("南宁市公安局");
//        customers.add("防城港市公安局");

        for (String customer : customers) {
            // 设置时间间隔
            String startTime = DateUtils.lastMonthTime() + " 00:00:00";
            String endTime = DateUtils.getDate() + " 23:59:59";
            List<PersonaBids> list = CrawlerGxggzyBidInfoUtil.crawlerGxggzyBids(customer, equal, startTime,
                    endTime, driver);
            if (list.size() > 0){
                for (PersonaBids bid : list) {
                    String industryType = analyseIndustryType(industryTypeList, bid.getTitle(), bid.getPurchaser(), bid.getContent());
                    bid.setIndustryType(industryType);
                    bid.setContent(null);
                }
                // 当前线程执行新增操作加锁
                synchronized (this) {
                    mybatisBatchUtils.batchUpdateOrInsert(list, PersonaBidsMapper.class,
                            (item, personaBidsMapper) -> personaBidsMapper.insertPersonaBids(item));
                }
            }
        }
        // 当前线程爬取任务结束，关闭所有窗口，退出当前线程创建的浏览器驱动
        SeleniumUtil.closeDriver(driver);
        long end = DateUtils.getNowDate().getTime();
        logger.info("-----------" + "["+ Thread.currentThread().getName() +"]" +
                "-----------爬取全国公共资源交易平台（广西壮族自治区）结束，耗时：" + (end - start) + "毫秒-----------");
    }

    @Async
    @Override
    public void addBidsByTyc(CustomerBusinessVo customerBusinessVo) {
        List<IndustryType> industryTypes = industryTypeMapper.selectIndustryTypeList(new IndustryType());
        //新增数量
        int num = 0;

//        List<CustomerBusinessVo> customersList = customersVoMapper.getHasCompanyIdCustomerList();
//        for (CustomerBusinessVo customer : customersList) {

        //调用天眼查-新增入库
//            String companyId = customer.getCompanyId();
//            String companyName = customer.getCompany();

        String companyId = customerBusinessVo.getCompanyId();
        String companyName = customerBusinessVo.getCompany();
        long timeFromDate = new Date().getTime();
        String[] pubDates = {"2024","2023","2022","2021","2020","2019"};
        for (String pubDate : pubDates){
            //请求页码
            int pageNum = 0;
            //查询企业列表
            //是否终止循环变量
            boolean endLoop = false;

            //循环的次数
            int loopNum = 0;

            //重连次数
            int reconnect = 0;
            while (!endLoop) {
                pageNum++;

                String url = "https://capi.tianyancha.com/cloud-business-state/bid/listV2?pageSize=20&pageNum=" + pageNum  +"&_="+timeFromDate+"&graphId=" + companyId + "&pubDate=" + pubDate;
                //System.out.println(executeGet(url, token));
                //获取查询数据
                StringBuilder result = new StringBuilder();
                BufferedReader bufferedReader = null;

                try {
                    //获取DefaultHttpClient请求;
                    HttpClient client = HttpClientBuilder.create().build();
                    HttpGet request = new HttpGet(url);
                    HttpResponse response = client.execute(request);
                    InputStream inputStream = response.getEntity().getContent();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                    String line;
                    while(null != (line = bufferedReader.readLine())) {
                        result.append(line);
                    }

                    //处理返回参数
                    JSONObject resultObj = JSONObject.parseObject(result.toString());
                    String code = resultObj.getString("errorCode");
                    //判断异常信息
                    if (!"0".equals(code)){
                        break;
                    }
                    //获取不到数据，退出本次循环
                    String totalString = resultObj.getJSONObject("data").getString("realTotal");
                    if (totalString == null){
                        continue;
                    }
                    int total = Integer.parseInt(totalString);
                    //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                    loopNum = total / 20 + 1;
                    //设置最多循环次数，避免出现过多请求接口导致收费过多
                    if (pageNum > 1000) {
                        endLoop = true;
                    } else if (pageNum <= loopNum) {
                        //获取最后一页，获取完后结循环
                        if (pageNum == loopNum) {
                            endLoop = true;
                        }
                        JSONArray lists = resultObj.getJSONObject("data").getJSONArray("items");

                        JSONObject temp = new JSONObject();
                        if (lists.size() > 0) {
                            for (Object object : lists) {
                                temp = JSONObject.parseObject(object.toString());
                                PersonaBids bid = new PersonaBids();
                                //cy.setCompanytype(Long.valueOf(temp.getString("")));
                                bid.setPublishTime(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishTime"))));

                                //从免费接口获取的天眼查url在访问时会有限制，现改为 https://www.tianyancha.com/bid/ 为首部的url
                                String lastBidUrl = BidStringUtils.getStringAfter(temp.getString("bidUrl"), "https://m.tianyancha.com/app/h5/bid/");
                                bid.setBidUrl("https://www.tianyancha.com/bid/"+ lastBidUrl);
                                //添加招标人信息
                                JSONArray purchaserList = temp.getJSONArray("purchaserList");
                                if (purchaserList != null){
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < purchaserList.size(); i++) {
                                        JSONObject jsonObject = JSONObject.parseObject(purchaserList.get(i).toString());
                                        if (i == purchaserList.size() - 1){
                                            stringBuilder.append(jsonObject.getString("name"));
                                        }else {
                                            stringBuilder.append(jsonObject.getString("name")).append(",");
                                        }
                                    }
                                    bid.setPurchaser(stringBuilder.toString());
                                }
                                //添加中标人信息
                                JSONArray supplierList = temp.getJSONArray("supplierList");
                                if (purchaserList != null){
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < supplierList.size(); i++) {
                                        //供应商过多时拿取前20个
                                        if ( i < 20){
                                            JSONObject jsonObject = JSONObject.parseObject(supplierList.get(i).toString());
                                            if (i == supplierList.size() - 1){
                                                stringBuilder.append(jsonObject.getString("name"));
                                            }else {
                                                stringBuilder.append(jsonObject.getString("name")).append(",");
                                            }
                                        }
                                    }
                                    bid.setBidWinner(stringBuilder.toString());
                                }
                                bid.setLink(temp.getString("link"));
                                bid.setTitle(temp.getString("title"));
                                bid.setUuid(temp.getString("uuid"));
                                bid.setContent(temp.getString("content"));
                                //若存在冒号，则截取冒号之后的公司名称（中文冒号）
                                bid.setProxy(StringUtils.subStringAfter(temp.getString("proxy"), "："));
                                bid.setAbs(temp.getString("abs"));
                                bid.setIntro(temp.getString("intro"));
                                bid.setType(temp.getString("infoCategory"));
                                bid.setProvince(temp.getString("area"));
                                bid.setSource("https://www.tianyancha.com/");
                                bid.setKeyword(companyName);
                                //截取招投标中的金额（只包含数字和小数点）
                                bid.setBidAmount(StringUtils.subStringNumber(temp.getString("bidAmount")));
                                if (bid.getTitle() != null){
                                    //通过招投标标题获取行业类型，如获取不到则继续判断
                                    String industry = analyseIndustryType(industryTypes, bid.getTitle(),bid.getPurchaser(),bid.getBidUrl());
                                    if (industry != null){
                                        bid.setIndustryType(industry);
                                    }

                                }
                                num = num + personaBidsMapper.insertPersonaBids(bid);
                            }
                        }
                    }
                    else {
                        endLoop = true;
                    }
                }catch (RuntimeException e){
                    e.printStackTrace();
//                    将pageNum 减一，继续访问该页面
                    if (reconnect < 5){
                        pageNum = pageNum - 1;
                        endLoop = false;
                        reconnect++;
                    }else {
                        endLoop = true;
                        reconnect = 0;
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    endLoop = true;
                }
            }
        }

//        }
        logger.info(companyName + "入库" + num + "条招投标记录");
    }

    /**
     * 爬取广西公共资源交易中心招投标数据
     * @param starPage 爬取页面的起始页
     * @param endPage 爬取页面的终止页
     * @return 爬取的招投标数据列表
     * @throws IOException
     */
    private List<PersonaBids> addGXPublicResources(int starPage, int endPage, List<IndustryType> industryTypes) throws IOException {

        List<PersonaBids> personaBidsList = new ArrayList<>();
        for(int s = starPage; s <= endPage; s++){
            String url = "http://gxggzy.gxzf.gov.cn/igs/front/search/list.html?=&filter[DOCTITLE]=中标结果公告&pageNumber="+s+"&pageSize=10&index=gxggzy_jyfw&type=jyfw&filter[parentparentid]=&filter[parentchnldesc]=&filter[chnldesc]=&filter[SITEID]=234&orderProperty=PUBDATE&orderDirection=desc&filter[AVAILABLE]=true";
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;

            try {
                //获取DefaultHttpClient请求;
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                InputStream inputStream = response.getEntity().getContent();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String line;
                while(null != (line = bufferedReader.readLine())) {
                    result.append(line);
                }
                //处理返回参数
                JSONObject resultObj = JSONObject.parseObject(result.toString());
                JSONArray contents = resultObj.getJSONObject("page").getJSONArray("content");
                JSONObject temp = new JSONObject();
                if (contents.size() >0){
                    for (Object object: contents){
                        temp = JSONObject.parseObject(object.toString());
                        //获取公共链接
                        String docPubUrl = temp.getString(DETAIL_URL);
                        Document doc = null;
                        boolean isConnectException = false;
                        //timeOutNum 超时次数
                        int timeOutNum = 0;
                        while (doc == null && timeOutNum <= 5 && !isConnectException){
                            try {
                                // 利用jsoup连接目标url网页获取整个html对象
                                doc = Jsoup.connect(docPubUrl).userAgent(UserAgentUtil.getUserAgent()).timeout(60000).get();
                                //防止频繁访问（模拟网络延迟）
//                                Thread.sleep(200);
                            } catch (SocketTimeoutException e){
                                timeOutNum++;
                                System.out.println("第"+timeOutNum+"次重新连接网站...");
                                e.printStackTrace();
                            } catch (SocketException e){
                                timeOutNum++;
                                System.out.println("第"+timeOutNum+"次重新连接网站...");
                                e.printStackTrace();
                            } catch (Exception e){
                                isConnectException = true;
                                e.printStackTrace();
                            }
                        }
                        if (doc != null && !temp.getString("DOCTITLE").contains("流标")) {
                            //获取主页面的HTML对象
                            Elements mainHtml = doc.select("div[class^=ewb-page-main]");
                            //获取主页面的字符
                            String mainContent = doc.select("div[class^=ewb-page-main]").text();
                            //获取存在文本中的中标信息
                            List<TableElement> tableElemts = DataTableUtil
                                    .getFitElement(doc);
                            List<PersonaBids> personaBids = null;
                            //表中存在中标信息
                            if (tableElemts != null && tableElemts.size() > 0) {
                                personaBids = TableUtil.extractPropertyInfos(tableElemts);
                                if (personaBids != null && personaBids.size() > 0) {
                                    for (PersonaBids personaBidsInfo : personaBids) {
                                        personaBidsInfo.setLink(temp.getString("DOCPUBURL"));
                                        personaBidsInfo.setPublishTime(DateUtils.parseDate(temp.getString("PUBDATE")));
                                        personaBidsInfo.setTitle(temp.getString("DOCTITLE"));
                                        //获取招标人名称
                                        if (!StringUtils.isNotEmpty(personaBidsInfo.getPurchaser())){
                                            String purchaser = "";
                                            for (int i = 0; i < BidConstants.PURCHASER_PROPERTIES.length; i++){

                                                String hasPurchaserLine = BidStringUtils.subStringBetween(mainContent, BidConstants.PURCHASER_PROPERTIES[i], " ");
                                                if(StringUtils.isNotEmpty(hasPurchaserLine) && hasPurchaserLine.contains("，")){
                                                    purchaser = BidStringUtils.subEndString(hasPurchaserLine, "，");
                                                    if (purchaser.contains("；")){
                                                        purchaser = BidStringUtils.subEndString(purchaser,"；");
                                                        personaBidsInfo.setPurchaser(purchaser.trim().replace("（公章）","").trim());
                                                    }else {
                                                        personaBidsInfo.setPurchaser(purchaser.trim().replace("（公章）","").trim());
                                                    }

                                                }else if (StringUtils.isNotEmpty(hasPurchaserLine)){
                                                    personaBidsInfo.setPurchaser(hasPurchaserLine.trim().replace("（公章）","").trim());
                                                }

                                            }
                                            if (StringUtils.isEmpty(personaBidsInfo.getPurchaser())){
                                                if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(mainContent, "受", "的委托"))){
                                                    purchaser = BidStringUtils.subStringBetween(mainContent, "受", "的委托");
                                                    personaBidsInfo.setPurchaser(purchaser);
                                                } else if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(mainContent, "受", "委托"))){
                                                    purchaser = BidStringUtils.subStringBetween(mainContent, "受", "委托");
                                                    personaBidsInfo.setPurchaser(purchaser);
                                                }
                                            }
                                        }

                                        // 使用雪花算法生成id
                                        long id = SnowflakeIdWorker.generateId();
                                        personaBidsInfo.setUuid(String.valueOf(id));
//                                            personaBidsInfo.setSource(doc.select("span[class ^= ewb-details-source]").text());
                                        //爬取的网站为广西公共资源交易中心
                                        personaBidsInfo.setProvince("广西");
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
                                        String pubDate = temp.getString("PUBDATE");
                                        //需转换时区
                                        String pubDateZone = pubDate.replace("Z", " UTC");
                                        personaBidsInfo.setPublishTime(simpleDateFormat.parse(pubDateZone));
//                                        personaBidsInfo.setContent(mainHtml.toString());
                                        personaBidsInfo.setType("中标公示");
//                                        String industry = IndustryTypeUtils.decideIndustryType(industryType, personaBidsInfo.getPurchaser());
//                                        if (industry == null || "".equals(industry)){
//                                            industry = IndustryTypeUtils.decideIndustryType(industryType, temp.getString("DOCTITLE"));
//                                        }
                                        //判断行业类型
                                        String industry = analyseIndustryType(industryTypes, personaBidsInfo.getPurchaser(), personaBidsInfo.getTitle(), mainContent);
                                        personaBidsInfo.setIndustryType(industry);
                                        personaBidsInfo.setSource("http://gxggzy.gxzf.gov.cn");
                                        personaBidsInfo.setCreateTime(DateUtils.getNowDate());
                                    }
                                    if (personaBids != null){
                                        personaBidsList.addAll(personaBids);
                                    }
                                }
                            } else {
                                //表中不存在中标信息，则中标信息存在于文本中
                                List<PersonaBids> bidWinningInfoList = DataTableUtil.getBidWinningInfo(mainContent);
                                for (PersonaBids personaBidsInfo : bidWinningInfoList){
                                    // 使用雪花算法生成id
                                    long id = SnowflakeIdWorker.generateId();
                                    personaBidsInfo.setUuid(String.valueOf(id));
                                    personaBidsInfo.setLink(temp.getString("DOCPUBURL"));
                                    personaBidsInfo.setTitle(temp.getString("DOCTITLE"));
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
                                    String pubDate = temp.getString("PUBDATE");
                                    //需转换时区
                                    String pubDateZone = pubDate.replace("Z", " UTC");
                                    personaBidsInfo.setPublishTime(simpleDateFormat.parse(pubDateZone));
                                    //爬取的网站为广西公共资源交易中心
                                    personaBidsInfo.setProvince("广西");
                                    personaBidsInfo.setType("中标公告");
                                    personaBidsInfo.setCreateTime(DateUtils.getNowDate());
                                    personaBidsInfo.setSource("http://gxggzy.gxzf.gov.cn");
                                    //判断行业类型
                                    String industry = analyseIndustryType(industryTypes, personaBidsInfo.getTitle(),personaBidsInfo.getPurchaser(), mainContent);
                                    if (StringUtils.isNotEmpty(industry)){
                                        personaBidsInfo.setIndustryType(industry);
                                    }
                                    personaBidsInfo.setIndustryType(industry);
                                }
                                if (bidWinningInfoList != null){
                                    personaBidsList.addAll(bidWinningInfoList);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return personaBidsList;
    }
}
