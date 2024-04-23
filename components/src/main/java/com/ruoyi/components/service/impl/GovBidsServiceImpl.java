package com.ruoyi.components.service.impl;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.components.domain.GovBids;
import com.ruoyi.components.domain.IndustryType;
import com.ruoyi.components.domain.PersonaBids;
import com.ruoyi.components.mapper.CustomersVoMapper;
import com.ruoyi.components.mapper.IndustryTypeMapper;
import com.ruoyi.components.mapper.PersonaBidsMapper;
import com.ruoyi.components.service.IGovBidsService;
import com.ruoyi.components.utils.ChromeUtils;
import com.ruoyi.components.utils.IndustryTypeUtils;
import com.ruoyi.components.utils.MybatisBatchUtils;
import com.ruoyi.components.utils.PutBidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 招投标数据Service业务层处理
 *
 * @author Leeyq
 * @date 2022-08-25
 */
@Service
public class GovBidsServiceImpl implements IGovBidsService
{
    private static final Logger logger = LoggerFactory.getLogger(GovBidsServiceImpl.class);

    private static final String COMPANY_LISTS = "zfcg.gxzf.gov.cn";
    private static final String INDUSTRY_TYPE = "cus.persona.industry.type";

    @Autowired
    private CustomersVoMapper customerVoMapper;
    @Autowired
    private IndustryTypeMapper industryTypeMapper;
    @Autowired
    private MybatisBatchUtils mybatisBatchUtils;
    @Autowired
    private RedisCache redisCache;

    /**
     * 新增招投标数据--中标公告
     *
     * @param govBids 招投标数据
     * @return 结果
     */
    @Override
    public int insertGovBids(GovBids govBids) {
        //获取广西政府采购网-招投标数据(首页接口地址)
        String url = "http://zfcg.gxzf.gov.cn/front/search/all";
        //请求ti
        JSONObject parammap = new JSONObject();
        //String keyword = govBids.getKeyword();//广西壮族自治区应急管理厅
        String[] array =new String[]{};//区域
        parammap.put("districtCode",array);
        parammap.put("firstCode", "ZcyAnnouncement");
        //更新最近一周时间
        parammap.put("publishDateBegin", DateUtils.lastMonthStamp());
        parammap.put("publishDateEnd", System.currentTimeMillis());
        //ZcyAnnouncement1 采购公告, ZcyAnnouncement2 结果公告
        parammap.put("secondCode", "ZcyAnnouncement2");

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

        String keyword ="";
        //获取行业类型列表
        List<IndustryType> industrys =getIndustryType();
        String ret = analyseIndustryType(industrys,"","","");
        int num=0;
        try {
            for (String unit: customers){
                logger.info("单位名称: " +unit);
                keyword=unit;
                //keyword="钦州市第一人民医院";
                parammap.put("keyword",keyword);
                parammap.put("pageNo", 1);
                parammap.put("pageSize","15");

                //获取记录数
                String result0 = PutBidUtils.doPost(url, parammap.toJSONString());
                //System.out.println("--------------中标公告---------------单位名称---"+unit+"-----------------------------------------");
                //System.out.println(result0);
                JSONObject resultObj0 = JSONObject.parseObject(result0);
                Integer total=Integer.valueOf(resultObj0.getJSONObject("hits").getString("total"));
                logger.info("查询记录数: "+total);
                int page = total/15 + 1;
                //分页查询
                for ( int j = 1; j <= page; j ++) {
                    parammap.put("pageNo", j);
                    //System.out.println("查询第"+j+"页");
                    //防止频繁访问（模拟网络延迟）
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //继续中断任务
                    try {

                        String result = PutBidUtils.doPost(url, parammap.toJSONString());
                        // System.out.println("---------------------------" + unit + "----第" + j + "页-----------------------------------");
//                System.out.println(result);
                        JSONObject resultObj = JSONObject.parseObject(result);

                        //查询投标信息列表
                        List<PersonaBids> bids = new ArrayList<PersonaBids>();
                        JSONArray lists = resultObj.getJSONObject("hits").getJSONArray("hits");
                        JSONObject data = new JSONObject();
                        JSONObject temp = new JSONObject();
                        int sum = 0;
                        if (lists.size() > 0) {
                            for (int i = 0; i < lists.size(); i++) {
                                data = (JSONObject) lists.get(i);
                                temp = JSONObject.parseObject(data.getJSONObject("_source").toString());//
                                //判断标题或详情是否符合当前单位
                                if ((temp.getString("title").contains(keyword) && !temp.getString("title").contains("废标公告"))
                                        || (temp.getString("content").contains(keyword) && !temp.getString("title").contains("废标公告"))) {
                                    PersonaBids bid = new PersonaBids();
                                    bid.setType(temp.getString("pathName"));
                                    bid.setTitle(temp.getString("title"));
                                    bid.setContent(temp.getString("content"));
                                    //详情链接
                                    String info = "http://zfcg.gxzf.gov.cn" + temp.getString("url");
                                    bid.setLink(info);
                                    //System.out.println("详情url: " + info);
                                    Map<String, Object> map = ChromeUtils.getPageBySelenium(info);
                                    //获取 采购人,中标单位,中标金额
                                    bid.setBidAmount(map.get("money").toString());
                                    bid.setPurchaser(map.get("cgrxx").toString());
                                    bid.setProxy(map.get("proxy").toString());
                                    bid.setBidWinner(map.get("company").toString());
                                    bid.setPublishTime(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishDate"))));
                                    //bid.setProvince(temp.getString("districtCode"));
                                    //判断行业类型
                                    String industry =analyseIndustryType(industrys, bid.getTitle(), bid.getPurchaser(), bid.getContent());
                                    bid.setIndustryType(industry);
                                    bid.setContent(null);
                                    bid.setProvince("广西");
                                    bid.setSource("http://zfcg.gxzf.gov.cn");
                                    bid.setKeyword(keyword);
                                    bids.add(bid);
                                    sum++;
                                }
                            }
                        }
                        //log.info("客户单位: " + keyword + " 记录总数 " + total + " 第" + j + "页-累计入库数量: " + sum);

                        if (bids.size() > 0) {

                            // System.out.println("入库记录数: " + bids.size());
                            num = mybatisBatchUtils.batchUpdateOrInsert(bids, PersonaBidsMapper.class,
                                    (item, personaBidsMapper) -> personaBidsMapper.insertPersonaBids(item));
                        }
                        //当前页匹配数为0,停止查询当前单位
//                        if (sum==0){
//                            break;
//                        }

                        //不做处理
                    }catch (Throwable e){

                    }
                }
                //剔除查询过的单位
                delList.add(unit);
            }
        }catch (Throwable e){
            //e.printStackTrace();
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
        return num;
    }

    /**
     * 招标公告
     * @param govBids
     * @return
     */
    @Override
    public int insertGovCallBids(GovBids govBids) {
        //获取广西政府采购网-招投标数据(首页接口地址)
        String url = "http://zfcg.gxzf.gov.cn/front/search/all";
        //请求ti
        JSONObject parammap = new JSONObject();
        //String keyword = govBids.getKeyword();//广西壮族自治区应急管理厅
        String[] array =new String[]{};//区域
        parammap.put("districtCode",array);
        parammap.put("firstCode", "ZcyAnnouncement");
//        parammap.put("publishDateBegin", 1609488743000L);
//        parammap.put("publishDateEnd", 1665821543000L);
        //更新最近一周时间
        parammap.put("publishDateBegin", DateUtils.lastMonthStamp());
        parammap.put("publishDateEnd", System.currentTimeMillis());
        //ZcyAnnouncement1 采购公告, ZcyAnnouncement2 结果公告
        parammap.put("secondCode", "ZcyAnnouncement1");

        //添加缓存--缓存未查询单位名称
        List<String> customers = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        String localkey = COMPANY_LISTS+":call";
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
        List<String> customer0 =customerVoMapper.selectCustomerUnitList();
        String keyword ="";
        int num=0;
        try {
            for (String unit: customer0){
                logger.info("单位名称: "+unit);
                keyword=unit;
                //keyword="钦州市第一人民医院";
                parammap.put("keyword",keyword);
                parammap.put("pageNo", 1);
                parammap.put("pageSize","15");

                //获取记录数
                String result0 = PutBidUtils.doPost(url, parammap.toJSONString());
                // System.out.println("----------------招标公告-------------单位名称---"+unit+"-----------------------------------------");
                JSONObject resultObj0 = JSONObject.parseObject(result0);
                Integer total=Integer.valueOf(resultObj0.getJSONObject("hits").getString("total"));
                logger.info("查询记录数: "+total);
                int page = total/15 + 1;
                //分页查询
                for ( int j = 1; j <= page; j ++) {
                    parammap.put("pageNo", j);
                    //System.out.println("查询第"+j+"页");
                    //防止频繁访问（模拟网络延迟）
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //继续中断任务
                    try {

                        String result = PutBidUtils.doPost(url, parammap.toJSONString());
                        // System.out.println("---------------------------" + unit + "----第" + j + "页-----------------------------------");
                        //System.out.println(result);
                        JSONObject resultObj = JSONObject.parseObject(result);

                        //查询投标信息列表
                        List<PersonaBids> bids = new ArrayList<PersonaBids>();
                        JSONArray lists = resultObj.getJSONObject("hits").getJSONArray("hits");
                        JSONObject data = new JSONObject();
                        JSONObject temp = new JSONObject();
                        int sum = 0;
                        if (lists.size() > 0) {
                            for (int i = 0; i < lists.size(); i++) {
                                data = (JSONObject) lists.get(i);
                                System.out.println(data.toJSONString());
                                temp = JSONObject.parseObject(data.getJSONObject("_source").toString());//
                                //判断标题或详情是否符合当前单位
                                if ((temp.getString("title").contains(keyword) && temp.getString("title").contains("招标公告"))
                                        || (temp.getString("content").contains(keyword) && temp.getString("title").contains("招标公告"))) {
                                    PersonaBids bid = new PersonaBids();
//                            bid.setArticleId(Long.valueOf(temp.getString("articleId")));
//                            bid.setSiteId(Long.valueOf(temp.getString("siteId")));
//                            bid.setFirstCode(temp.getString("firstCode"));
//                            bid.setSecondCode(temp.getString("secondCode"));
//                            bid.setAuthor(temp.getString("author"));
//                            bid.setRemark(temp.getString("remark"));
//                            bid.setPath(temp.getString("path"));
//                            bid.setInvalid(Long.valueOf(temp.getString("invalid")));
                                    bid.setType(temp.getString("pathName"));
                                    bid.setTitle(temp.getString("title"));
                                    //bid.setContent(temp.getString("content"));
                                    //详情链接
                                    String info = "http://zfcg.gxzf.gov.cn" + temp.getString("url");
                                    bid.setLink(info);
                                    System.out.println("URL "+temp.getString("url"));
                                    //  System.out.println("详情url: " + info);
//                                    Map<String, Object> map = ChromeUtils.getPageBySelenium(info);
//                                    //获取 采购人,中标单位,中标金额
//                                    bid.setBidAmount(map.get("money").toString());
//                                    bid.setPurchaser(map.get("cgrxx").toString());
//                                    bid.setProxy(map.get("proxy").toString());
//                                    bid.setBidWinner(map.get("company").toString());
                                    bid.setPublishTime(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishDate"))));
                                    //bid.setProvince(temp.getString("districtCode"));
                                    bid.setProvince("广西");
                                    bid.setSource("http://zfcg.gxzf.gov.cn");
                                    bid.setKeyword(keyword);
                                    bids.add(bid);
                                    sum++;
                                }
                            }
                        }
                        //  log.info("客户单位: " + keyword + " 记录总数 " + total + " 第" + j + "页-累计入库数量: " + sum);

                        if (bids.size() > 0) {

                            // System.out.println("入库记录数: " + bids.size());
                            num = mybatisBatchUtils.batchUpdateOrInsert(bids, PersonaBidsMapper.class,
                                    (item, personaBidsMapper) -> personaBidsMapper.insertPersonaBids(item));
                        }
                        //当前页匹配数为0,停止查询当前单位
//                        if (sum == 0) {
//                            break;
//                        }
                        //不做处理
                    }catch (Throwable e){

                    }
                }
                //剔除查询过的单位
                delList.add(unit);
            }
        }catch (Throwable e){
            //e.printStackTrace();
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
        return num;
    }

    /**
     * 招标预告-招标文件预公示
     * @param govBids
     * @return
     */
    @Override
    public int insertGovPlanBids(GovBids govBids) {
        //获取广西政府采购网-招投标数据(首页接口地址)
        String url = "http://zfcg.gxzf.gov.cn/front/search/all";
        //请求ti
        JSONObject parammap = new JSONObject();
        //String keyword = govBids.getKeyword();//广西壮族自治区应急管理厅
        String[] array =new String[]{};//区域
        parammap.put("districtCode",array);
        parammap.put("firstCode", "ZcyAnnouncement");
//        parammap.put("publishDateBegin", 1609488743000L);
//        parammap.put("publishDateEnd", 1665821543000L);
        //更新最近一周时间
        parammap.put("publishDateBegin", DateUtils.lastMonthStamp());
        parammap.put("publishDateEnd", System.currentTimeMillis());
        //ZcyAnnouncement1 采购公告, ZcyAnnouncement2 结果公告, ZcyAnnouncement5 招标预告
        parammap.put("secondCode", "ZcyAnnouncement5");

        //添加缓存--缓存未查询单位名称
        List<String> customers = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        String localkey = COMPANY_LISTS+":plan";
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

        String keyword ="";
        int num=0;
        try {
            for (String unit: customers){
                logger.info("单位名称: "+unit);
                keyword=unit;
                //keyword="钦州市第一人民医院";
                parammap.put("keyword",keyword);
                parammap.put("pageNo", 1);
                parammap.put("pageSize","15");

                //获取记录数
                String result0 = PutBidUtils.doPost(url, parammap.toJSONString());
                // System.out.println("----------------招标公告-------------单位名称---"+unit+"-----------------------------------------");
                JSONObject resultObj0 = JSONObject.parseObject(result0);
                Integer total=Integer.valueOf(resultObj0.getJSONObject("hits").getString("total"));
                logger.info("查询记录数: "+total);
                int page = total/15 + 1;
                //分页查询
                for ( int j = 1; j <= page; j ++) {
                    parammap.put("pageNo", j);
                    //System.out.println("查询第"+j+"页");
                    //防止频繁访问（模拟网络延迟）
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //继续中断任务
                    try {

                        String result = PutBidUtils.doPost(url, parammap.toJSONString());
                        // System.out.println("---------------------------" + unit + "----第" + j + "页-----------------------------------");
                        //System.out.println(result);
                        JSONObject resultObj = JSONObject.parseObject(result);

                        //查询投标信息列表
                        List<PersonaBids> bids = new ArrayList<PersonaBids>();
                        JSONArray lists = resultObj.getJSONObject("hits").getJSONArray("hits");
                        JSONObject data = new JSONObject();
                        JSONObject temp = new JSONObject();
                        int sum = 0;
                        if (lists.size() > 0) {
                            for (int i = 0; i < lists.size(); i++) {
                                data = (JSONObject) lists.get(i);
                                temp = JSONObject.parseObject(data.getJSONObject("_source").toString());//
                                //判断标题或详情是否符合当前单位
                                if ((temp.getString("title").contains(keyword) && temp.getString("title").contains("预公示"))
                                        || (temp.getString("content").contains(keyword) && temp.getString("title").contains("预公示"))) {
                                    PersonaBids bid = new PersonaBids();
//                            bid.setArticleId(Long.valueOf(temp.getString("articleId")));
//                            bid.setSiteId(Long.valueOf(temp.getString("siteId")));
//                            bid.setFirstCode(temp.getString("firstCode"));
//                            bid.setSecondCode(temp.getString("secondCode"));
//                            bid.setAuthor(temp.getString("author"));
//                            bid.setRemark(temp.getString("remark"));
//                            bid.setPath(temp.getString("path"));
//                            bid.setInvalid(Long.valueOf(temp.getString("invalid")));
                                    bid.setType(temp.getString("pathName"));
                                    bid.setTitle(temp.getString("title"));
                                    //bid.setContent(temp.getString("content"));
                                    //详情链接
                                    String info = "http://zfcg.gxzf.gov.cn" + temp.getString("url");
                                    ///http://zfcg.gxzf.gov.cn/ZcyAnnouncement/ZcyAnnouncement5/ZcyAnnouncement3014/RmCmZn3iVQ89nqgDjRkgeA==.html
                                    bid.setLink(info);
                                    System.out.println("详情url: " + info);
                                    Map<String, Object> map = ChromeUtils.getPageBySelenium(info);
                                    //获取 采购人,中标单位,中标金额
                                    bid.setBidAmount(map.get("money").toString());
                                    bid.setPurchaser(map.get("cgrxx").toString());
                                    bid.setProxy(map.get("proxy").toString());
                                    bid.setBidWinner(map.get("company").toString());
                                    bid.setPublishTime(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishDate"))));
                                    //bid.setProvince(temp.getString("districtCode"));
                                    bid.setProvince("广西");
                                    bid.setSource("http://zfcg.gxzf.gov.cn");
                                    bid.setKeyword(keyword);
                                    bids.add(bid);
                                    sum++;
                                }
                            }
                        }
                        //  log.info("客户单位: " + keyword + " 记录总数 " + total + " 第" + j + "页-累计入库数量: " + sum);

                        if (bids.size() > 0) {

                            // System.out.println("入库记录数: " + bids.size());
                            num = mybatisBatchUtils.batchUpdateOrInsert(bids, PersonaBidsMapper.class,
                                    (item, personaBidsMapper) -> personaBidsMapper.insertPersonaBids(item));
                        }
                        //当前页匹配数为0,停止查询当前单位
//                        if (sum == 0) {
//                            break;
//                        }
                        //不做处理
                    }catch (Throwable e){

                    }
                }
                //剔除查询过的单位
                delList.add(unit);
            }
        }catch (Throwable e){
            //e.printStackTrace();
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
        return num;
    }

    @Override
    public int insertGovPurposeBids(GovBids govBids) {
        //获取广西政府采购网-招投标数据(首页采购意向地址)
        String url = "http://zfcg.gxzf.gov.cn/front/search/category";
        //请求ti
        JSONObject parammap = new JSONObject();
        //String keyword = govBids.getKeyword();//广西壮族自治区应急管理厅
        String[] array =new String[]{};//区域
        parammap.put("districtCode",array);
        //更新最近一周时间
        parammap.put("publishDateBegin", DateUtils.nextMonthStamp());
        parammap.put("publishDateEnd", System.currentTimeMillis());
        //ZcyAnnouncement1 采购公告, ZcyAnnouncement10016 采购意向
        parammap.put("categoryCode", "ZcyAnnouncement10016");

        //添加缓存--缓存未查询单位名称
        List<String> customers = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        String localkey = COMPANY_LISTS+":want";
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

        String keyword ="";
        int num=0;
        try {
            for (String unit: customers){
                logger.info("单位名称: "+unit);
                keyword=unit;
                //keyword="灌阳县公安局";
                parammap.put("keyword",keyword);
                parammap.put("pageNo", 1);
                parammap.put("pageSize","15");
                //获取记录数
                String result0 = PutBidUtils.doPost(url, parammap.toJSONString());
                // System.out.println("----------------招标公告-------------单位名称---"+unit+"-----------------------------------------");
                JSONObject resultObj0 = JSONObject.parseObject(result0);
                Integer total=Integer.valueOf(resultObj0.getJSONObject("hits").getString("total"));
                logger.info("查询记录数: "+total);
                int page = total/15 + 1;
                //分页查询
                for ( int j = 1; j <= page; j ++) {
                    parammap.put("pageNo", j);
                    //System.out.println("查询第"+j+"页");
                    //防止频繁访问（模拟网络延迟）
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //继续中断任务
                    try {

                        String result = PutBidUtils.doPost(url, parammap.toJSONString());
                        // System.out.println("---------------------------" + unit + "----第" + j + "页-----------------------------------");
                        //System.out.println(result);
                        JSONObject resultObj = JSONObject.parseObject(result);

                        //查询投标信息列表
                        List<PersonaBids> bids = new ArrayList<PersonaBids>();
                        JSONArray lists = resultObj.getJSONObject("hits").getJSONArray("hits");
                        int sum = 0;
                        if (lists.size() > 0) {
                            for (int i = 0; i < lists.size(); i++) {
                                JSONObject data = (JSONObject) lists.get(i);
                                JSONObject temp = JSONObject.parseObject(data.getJSONObject("_source").toString());//
                                //判断标题或详情是否符合当前单位
                                if ((temp.getString("title").contains(keyword) && temp.getString("title").contains("采购意向"))) {
                                    System.out.println("--------------"+temp.getString("title")+"----------------");
                                    PersonaBids bid = new PersonaBids();
                                    bid.setType(temp.getString("pathName"));
                                    bid.setTitle(temp.getString("title"));
                                    //详情链接
                                    if("null".equals(temp.getString("url"))) {
                                        continue;
                                    }
                                    String info = "http://zfcg.gxzf.gov.cn" + temp.getString("url");
                                    ///http://zfcg.gxzf.gov.cn/ZcyAnnouncement/ZcyAnnouncement5/ZcyAnnouncement3014/RmCmZn3iVQ89nqgDjRkgeA==.html
                                    bid.setLink(info);
                                    //System.out.println("详情url: " + info);
                                    Map<String, Object> map = ChromeUtils.getPlanPageBySelenium(info);
                                    //获取 采购人,中标单位,中标金额
                                    bid.setBidAmount(map.get("price").toString());
                                    bid.setPurchaser(map.get("unit").toString());
                                    bid.setPublishTime(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishDate"))));
                                    bid.setProvince(temp.getString("districtName"));
                                    bid.setSource("http://zfcg.gxzf.gov.cn");
                                    bid.setKeyword(keyword);
                                    bids.add(bid);
                                    sum++;
                                }
                            }
                        }
                        //  log.info("客户单位: " + keyword + " 记录总数 " + total + " 第" + j + "页-累计入库数量: " + sum);

                        if (bids.size() > 0) {

                            System.out.println("入库记录数: " + bids.size());
                            num = mybatisBatchUtils.batchUpdateOrInsert(bids, PersonaBidsMapper.class,
                                    (item, personaBidsMapper) -> personaBidsMapper.insertPersonaBids(item));
                        }
                        //不做处理
                    }catch (Throwable e){

                    }
                }
                //剔除查询过的单位
                delList.add(unit);
            }
        }catch (Throwable e){
            //e.printStackTrace();
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
        return num;
    }


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

}
