package com.comp.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.comp.components.domain.CusPersonaCompanyProduct;
import com.comp.components.domain.vo.CustomerBusinessVo;
import com.comp.components.mapper.CusPersonaCompanyProductMapper;
import com.comp.components.service.ICusPersonaCompanyProductService;
import com.ruoyi.common.enums.UrlAddressEnum;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.HttpApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 公司详情产品信息Service业务层处理
 * 
 * @author wucilong
 * @date 2022-09-05
 */
@Service
public class CusPersonaCompanyProductServiceImpl implements ICusPersonaCompanyProductService
{
    @Autowired
    private CusPersonaCompanyProductMapper cusPersonaCompanyProductMapper;

    private static final Logger logger = LoggerFactory.getLogger(CusPersonaCompanyProductServiceImpl.class);


    /**
     * 入库客户表中单位的产品信息
     * @return 插入结果
     */
    @Async
    @Override
    public void addProductByTyc(CustomerBusinessVo customerBusinessVo, String userName) {
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
            try {
                String url = "http://open.api.tianyancha.com/services/open/m/appbkInfo/2.0?pageSize=20&pageNum=" + pageNum + "&id=" + companyId;
                String result = HttpApiUtils.executeGet(url, UrlAddressEnum.TOKEN_API.getUrl());
                JSONObject resultObj = JSONObject.parseObject(result);
                String code = resultObj.getString("error_code");
                //判断异常信息
                if (!"0".equals(code)) {
                    break;
                }
                //获取不到数据，退出本次循环
                String totalString = resultObj.getJSONObject("result").getString("total");
                if (totalString == null) {
                    endLoop = true;
                }
                int total = Integer.parseInt(totalString);
                //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                loopNum = total / 20 + 1;
                //设置最多循环次数，避免出现过多请求接口导致收费过多
                if (pageNum > 10) {
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
                            CusPersonaCompanyProduct product = new CusPersonaCompanyProduct();
                            product.setProductName(temp.getString("name"));
                            product.setBrief(temp.getString("brief"));
                            product.setClasses(temp.getString("classes"));
                            product.setIcon(temp.getString("icon"));
                            product.setFilterName(temp.getString("filterName"));
                            product.setType(temp.getString("type"));
                            product.setCompanyId(companyId);
                            product.setCompanyName(companyName);
                            product.setCreateBy(userName);
                            product.setCreateTime(DateUtils.getNowDate());
                            num = num + cusPersonaCompanyProductMapper.insertCusPersonaCompanyProduct(product);
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
            }catch (Exception e){
                endLoop = true;
                reconnect = 0;
            }
        }
        logger.info(companyName + "入库" + num + "条产品记录");
    }

}
