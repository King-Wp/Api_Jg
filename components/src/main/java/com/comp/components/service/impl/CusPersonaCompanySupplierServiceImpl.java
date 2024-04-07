package com.comp.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.comp.components.domain.CusPersonaCompanySupplier;
import com.comp.components.domain.vo.CustomerBusinessVo;
import com.comp.components.mapper.CusPersonaCompanySupplierMapper;
import com.comp.components.service.ICusPersonaCompanySupplierService;
import com.ruoyi.common.enums.UrlAddressEnum;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.HttpApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 公司详情供应商信息Service业务层处理
 * 
 * @author wucilong
 * @date 2022-09-05
 */
@Service
public class CusPersonaCompanySupplierServiceImpl implements ICusPersonaCompanySupplierService
{
    @Resource
    private CusPersonaCompanySupplierMapper cusPersonaCompanySupplierMapper;

    private static final Logger logger = LoggerFactory.getLogger(CusPersonaCompanySupplierServiceImpl.class);

    /**
     * 调用天眼查入库公司供应商信息
     * @return 插入结果
     */
    @Async
    @Override
    public void addSupplierByTyc(CustomerBusinessVo customerBusinessVo, String userName) {
        //新增数量
        int num = 0;

        //调用天眼查-新增入库
        String companyId = customerBusinessVo.getCompanyId();
        String companyName = customerBusinessVo.getCompany();

//            String companyId = "476909213";
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
            String url = "http://open.api.tianyancha.com/services/open/m/supply/2.0?pageSize=20&pageNum=" + pageNum + "&id=" + companyId;
            try {
                String result = HttpApiUtils.executeGet(url, UrlAddressEnum.TOKEN_API.getUrl());
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
                if (pageNum > 100) {
                    endLoop = true;
                } else if (pageNum <= loopNum) {
                    //获取最后一页，获取完后结循环
                    if (pageNum == loopNum) {
                        endLoop = true;
                    }
                    JSONArray lists = resultObj.getJSONObject("result").getJSONObject("pageBean").getJSONArray("result");

                    JSONObject temp = new JSONObject();
                    if (lists.size() > 0) {
                        for (Object object : lists) {
                            temp = JSONObject.parseObject(object.toString());
                            CusPersonaCompanySupplier supplierObject = new CusPersonaCompanySupplier();
                            supplierObject.setAlias(temp.getString("alias"));
                            String amt = temp.getString("amt");
                            if (!(amt == null || "".equals(amt))){
                                supplierObject.setAmt(BigDecimal.valueOf(Double.parseDouble(temp.getString("amt"))));
                            }
                            String announcementDate = temp.getString("announcement_date");
                            if (!(announcementDate == null || "".equals(announcementDate))){
                                supplierObject.setAnnouncementDate(DateUtils.dateToStamp(Long.valueOf((temp.getString("announcement_date")))));
                            }
                            String supplierGraphId = temp.getString("supplier_graphId");
                            if (!(supplierGraphId == null || "".equals(supplierGraphId))){
                                supplierObject.setSupplierGraphId(Long.parseLong(supplierGraphId));
                            }
                            supplierObject.setSupplierName(temp.getString("supplier_name"));
                            supplierObject.setDataSource(temp.getString("dataSource"));
                            supplierObject.setLogo(temp.getString("logo"));
                            supplierObject.setRatio(temp.getString("ratio"));
                            supplierObject.setRelationship(temp.getString("relationship"));
                            supplierObject.setCreateBy(userName);
                            supplierObject.setCompanyId(companyId);
                            supplierObject.setCompanyName(companyName);
                            supplierObject.setCreateTime(DateUtils.getNowDate());

                            num = num + cusPersonaCompanySupplierMapper.insertCusPersonaCompanySupplier(supplierObject);
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
        logger.info(companyName + "入库" + num + "条供应商记录");
    }
}
