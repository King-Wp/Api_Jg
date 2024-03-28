package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.HttpApiUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.PersonaBids;
import com.ruoyi.system.mapper.PersonaBidsMapper;
import com.ruoyi.system.service.IPersonaBidsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    @Autowired
    private PersonaBidsMapper personaBidsMapper;

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
        List<PersonaBids> bids = new ArrayList<PersonaBids>();
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
                String result = HttpApiUtils.executeGet(url);
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
                if (pageNum > 500) {
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
                            bid.setSource("https://open.tianyancha.com");
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
                            bids.add(bid);
                            num = personaBidsMapper.insertPersonaBids(bid);
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
        return num;
    }
}
