package com.ruoyi.components.service.impl;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.components.domain.CusPersonaDishonestPerson;
import com.ruoyi.components.mapper.CusPersonaDishonestPersonMapper;
import com.ruoyi.components.service.ICusPersonaDishonestPersonService;
import com.ruoyi.components.utils.HttpApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ruoyi.common.enums.UrlAddressEnum.TOKEN_API;

/**
 * 失信被执行人Service业务层处理
 * 
 * @author wucilong
 * @date 2023-02-08
 */
@Service
public class CusPersonaDishonestPersonServiceImpl implements ICusPersonaDishonestPersonService
{
    @Autowired
    private CusPersonaDishonestPersonMapper cusPersonaDishonestPersonMapper;

    private static final Logger logger = LoggerFactory.getLogger(CusPersonaDishonestPersonServiceImpl.class);

    /**
     * 通过天眼查收费接口获取并入库失信被执行人信息
     */
    @Override
    public Integer addDishonestPersonByTyc(String companyId,String companyName, String userName) {
        //新增数量
        int num = 0;
        //请求页码
        int pageNum = 0;
        //是否终止循环变量
        boolean endLoop = false;
        //循环的次数
        int loopNum = 0;
        //重连次数
        int reconnect = 0;

        while (!endLoop) {
            //将访问的页数加一
            pageNum++;
            String url = "http://open.api.tianyancha.com/services/open/jr/dishonest/2.0?keyword="+companyId+"&pageNum="+pageNum+"&pageSize=20";
            JSONObject resultObj = null;
            try {
                String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());
                resultObj = JSONObject.parseObject(result);
            } catch (RuntimeException e) {
                //返回空时解析Json会出现异常，则将pageNum 减一，继续访问该页面
                e.printStackTrace();
                if (reconnect < 2) {
                    pageNum = pageNum - 1;
                    reconnect++;
                } else {
                    //重新访问超过一定次数则退出循环
                    endLoop = true;
                    reconnect = 0;
                }
            }
            if (resultObj != null){
                String code = resultObj.getString("error_code");
                //判断异常信息
                if ("0".equals(code)) {
                    String totalString = resultObj.getJSONObject("result").getString("total");
                    if (totalString == null) {
                        //获取不到数据，退出本次循环
                        break;
                    }
                    int total = Integer.parseInt(totalString);
                    //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                    loopNum = total / 20 + 1;
                    //设置最多循环次数，避免出现过多请求接口导致收费过多
                    if (pageNum > 5) {
                        endLoop = true;
                    } else if (pageNum <= loopNum) {
                        //获取最后一页，获取完后结束循环
                        if (pageNum == loopNum) {
                            endLoop = true;
                        }
                        JSONArray lists = resultObj.getJSONObject("result").getJSONArray("items");

                        if (lists.size() > 0) {
                            for (Object object : lists) {
                                try {
                                    JSONObject temp = JSONObject.parseObject(object.toString());
                                    CusPersonaDishonestPerson dishonestPerson = new CusPersonaDishonestPerson();
                                    dishonestPerson.setCompanyId(companyId);
                                    dishonestPerson.setCompanyName(companyName);
                                    dishonestPerson.setCaseCode(temp.getString("casecode"));
                                    dishonestPerson.setCourtName(temp.getString("courtname"));
                                    if (temp.getString("duty").length() < 500){
                                        dishonestPerson.setDuty(temp.getString("duty"));
                                    }
                                    if (!(temp.getString("publishdate") == null || "".equals(temp.getString("publishdate")))){
                                        dishonestPerson.setPublishDate(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishdate"))));
                                    }
                                    dishonestPerson.setDisruptTypeName(temp.getString("disrupttypename"));
                                    dishonestPerson.setGistId(temp.getString("gistid"));
                                    dishonestPerson.setPerformance(temp.getString("performance"));
                                    dishonestPerson.setGistUnit(temp.getString("gistunit"));
                                    dishonestPerson.setCreateBy(userName);
                                    dishonestPerson.setCreateTime(DateUtils.getNowDate());
                                    num = num + cusPersonaDishonestPersonMapper.insertCusPersonaDishonestPerson(dishonestPerson);

                                }catch (RuntimeException e){
                                    logger.error("解析JSON异常",e);
                                }

                            }
                        }
                    } else {
                        endLoop = true;
                    }
                }else if ("300004".equals(code) || "300001".equals(code)){
                    //若频繁访问或访问失败，则重新访问该页面数据
                    if (reconnect < 2) {
                        pageNum = pageNum - 1;
                        reconnect++;
                    } else {
                        //重新访问超过一定次数则退出循环
                        endLoop = true;
                        reconnect = 0;
                    }
                }else {
                    //其他原因则退出循环
                    break;
                }

            }
        }
        logger.info("{}入库{}条企业失信被执行人记录", companyName, num);
        return num;
    }
}
