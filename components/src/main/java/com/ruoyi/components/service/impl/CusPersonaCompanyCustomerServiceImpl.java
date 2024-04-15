package com.ruoyi.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.enums.UrlAddressEnum;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.components.domain.CusPersonaCompanyCustomer;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import com.ruoyi.components.mapper.CusPersonaCompanyCustomerMapper;
import com.ruoyi.components.service.ICusPersonaCompanyCustomerService;
import com.ruoyi.components.utils.HttpApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 企业客户信息Service业务层处理
 * 
 * @author wucilong
 * @date 2022-09-05
 */
@Service
public class CusPersonaCompanyCustomerServiceImpl implements ICusPersonaCompanyCustomerService
{
    @Resource
    private CusPersonaCompanyCustomerMapper cusPersonaCompanyCustomerMapper;

    private static final Logger logger = LoggerFactory.getLogger(CusPersonaCompanyCustomerServiceImpl.class);

    /**
     * 调用天眼查批量新增客户表中企业单位的客户信息
     * @return 添加结果
     */
    @Async
    @Override
    public void addCompanyCustomerByTyc(CustomerBusinessVo customerBusinessVo, String userName) {
        //新增数量
        int num = 0;

        //调用天眼查-新增入库
        String companyId = customerBusinessVo.getCompanyId();
        String companyName = customerBusinessVo.getCompany();
        //请求页码
        int pageNum = 0;
        //是否终止循环变量
        boolean endLoop = false;
        //循环的次数
        int loopNum = 0;

        //重连次数
        int reconnect = 0;
        while (!endLoop) {
            pageNum++;
            String url = "http://open.api.tianyancha.com/services/open/m/customer/2.0?pageSize=20&pageNum=" + pageNum + "&id=" + companyId;
            try {
                String result = HttpApiUtils.executeGet(url, UrlAddressEnum.TOKEN_API.getVal());
                JSONObject resultObj = JSONObject.parseObject(result);
                String code = resultObj.getString("error_code");
                //判断异常信息
                if (!"0".equals(code)) {
                    break;
                }
                //获取不到数据，退出本次循环
                String totalString = resultObj.getJSONObject("result").getJSONObject("pageBean").getString("total");
                if (totalString == null) {
                    endLoop = true;
                }
                int total = Integer.parseInt(totalString);
                //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                loopNum = total / 20 + 1;
                //设置最多循环次数，避免出现过多请求接口导致收费过多
                if (pageNum > 20) {
                    endLoop = true;
                } else if (pageNum <= loopNum) {
                    //获取最后一页，获取完后结循环
                    if (pageNum == loopNum) {
                        endLoop = true;
                    }
                    JSONArray lists = resultObj.getJSONObject("result").getJSONObject("pageBean").getJSONArray("result");
                    if (lists.size() > 0) {
                        for (Object object : lists) {
                            JSONObject temp = JSONObject.parseObject(object.toString());
                            CusPersonaCompanyCustomer customerObject = new CusPersonaCompanyCustomer();
                            customerObject.setAlias(temp.getString("alias"));
                            String amt = temp.getString("amt");
                            if (!(amt == null || "".equals(amt))){
                                customerObject.setAmt(BigDecimal.valueOf(Double.parseDouble(temp.getString("amt"))));
                            }
                            String announcementDate = temp.getString("announcement_date");
                            if (!(announcementDate == null || "".equals(announcementDate))){
                                customerObject.setAnnouncementDate(DateUtils.dateToStamp(Long.valueOf((temp.getString("announcement_date")))));
                            }
                            String clientGraphId = temp.getString("client_graphId");
                            if (!(clientGraphId == null || "".equals(clientGraphId))){
                                customerObject.setClientGraphId(Long.parseLong(temp.getString("client_graphId")));
                            }
                            customerObject.setClientName(temp.getString("client_name"));
                            customerObject.setDataSource(temp.getString("dataSource"));
                            customerObject.setLogo(temp.getString("logo"));
                            customerObject.setRatio(temp.getString("ratio"));
                            customerObject.setRelationship(temp.getString("relationship"));
                            customerObject.setCreateBy(userName);
                            customerObject.setCompanyId(companyId);
                            customerObject.setCompanyName(companyName);
                            customerObject.setCreateTime(DateUtils.getNowDate());

                            num = num + cusPersonaCompanyCustomerMapper.insertCusPersonaCompanyCustomer(customerObject);
                        }
                    }
                } else {
                    endLoop = true;
                }
            } catch (RuntimeException e) {
                //将pageNum 减一，继续访问该页面
                e.printStackTrace();
                if (reconnect < 2) {
                    pageNum = pageNum - 1;
                    endLoop = false;
                    reconnect++;
                } else {
                    endLoop = true;
                    reconnect = 0;
                }
            }
        }

        logger.info(companyName + "入库" + num + "条客户记录");
    }
}
