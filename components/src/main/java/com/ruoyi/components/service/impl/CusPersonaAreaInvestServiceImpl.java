package com.ruoyi.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.components.domain.CusPersonaAreaInvest;
import com.ruoyi.components.domain.CusPersonaCategoryInvest;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import com.ruoyi.components.mapper.CusPersonaAreaInvestMapper;
import com.ruoyi.components.mapper.CusPersonaCategoryInvestMapper;
import com.ruoyi.components.service.ICusPersonaAreaInvestService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * areaInvestService业务层处理
 * 
 * @author wucilong
 * @date 2022-11-04
 */
@Service
public class CusPersonaAreaInvestServiceImpl implements ICusPersonaAreaInvestService
{

    @Resource
    private CusPersonaAreaInvestMapper cusPersonaAreaInvestMapper;
    @Resource
    private CusPersonaCategoryInvestMapper cusPersonaCategoryInvestMapper;

    private static final Logger logger = LoggerFactory.getLogger(CusPersonaAreaInvestServiceImpl.class);

    /**
     * 通过天眼查入库客户单位对外投资情况
     * @param companyId 天眼查id
     * @param companyName 公司名称
     * @param userName 用户名称
     * @return 录入条数
     */

    @Override
    public int addInvestByTycCompany(String companyId , String companyName, String userName) {
        int res = 0;
        long timeFromDate = new Date().getTime();
        String url = "https://capi.tianyancha.com/cloud-company-background/company/invest/statistics?_="+timeFromDate+"&gid="+companyId;
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            //获取DefaultHttpClient请求;
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            InputStream inputStream = response.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while(null != (line = bufferedReader.readLine())) {
                result.append(line);
            }

            //处理返回参数
            JSONObject resultObj = JSONObject.parseObject(result.toString());
            if (resultObj.getJSONObject("data") != null){
                JSONArray categoryInvest = resultObj.getJSONObject("data").getJSONArray("category");
                JSONArray areaInvest = resultObj.getJSONObject("data").getJSONArray("area");
                if (areaInvest != null){
                    for (int i = 0; i < areaInvest.size(); i++){
                        CusPersonaAreaInvest cusPersonaAreaInvest = new CusPersonaAreaInvest();
                        JSONObject areaInvestJSONObject = areaInvest.getJSONObject(i);
                        cusPersonaAreaInvest.setAreaName(areaInvestJSONObject.getString("name"));
                        cusPersonaAreaInvest.setAreaNum(Long.parseLong(areaInvestJSONObject.getString("num")));
                        cusPersonaAreaInvest.setAreaKey(areaInvestJSONObject.getString("key"));
                        cusPersonaAreaInvest.setCompanyId(companyId);
                        cusPersonaAreaInvest.setCompanyName(companyName);
                        cusPersonaAreaInvest.setCreateBy(userName);
                        cusPersonaAreaInvest.setCreateTime(DateUtils.getNowDate());

                        res = cusPersonaAreaInvestMapper.insertCusPersonaAreaInvest(cusPersonaAreaInvest);
                    }
                    for (int j = 0; j < categoryInvest.size(); j++){
                        CusPersonaCategoryInvest cusPersonaCategoryInvest = new CusPersonaCategoryInvest();
                        JSONObject categoryInvestJSONObject = categoryInvest.getJSONObject(j);
                        cusPersonaCategoryInvest.setCategoryName(categoryInvestJSONObject.getString("name"));
                        cusPersonaCategoryInvest.setCategoryNum(Long.parseLong(categoryInvestJSONObject.getString("num")));
                        cusPersonaCategoryInvest.setCategoryKey(categoryInvestJSONObject.getString("key"));
                        cusPersonaCategoryInvest.setCompanyId(companyId);
                        cusPersonaCategoryInvest.setCompanyName(companyName);
                        cusPersonaCategoryInvest.setCreateBy(userName);
                        cusPersonaCategoryInvest.setCreateTime(DateUtils.getNowDate());

                        res = res + cusPersonaCategoryInvestMapper.insertCusPersonaCategoryInvest(cusPersonaCategoryInvest);
                    }
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("{}入库{}条对外投资记录", companyName, res);
        return res;
    }

    /**
     *  通过天眼查批量入库客户单位对外投资情况
     * @param hasCompanyIdCustomerList 获取通过天眼查查询过的客户公司列表
     * @param userName 用户名称
     * @return 插入条数
     */
    @Override
    public int addInvestByTyc(List<CustomerBusinessVo> hasCompanyIdCustomerList, String userName) {
        int res = 0;
        for (CustomerBusinessVo companyBusiness : hasCompanyIdCustomerList){
            long timeFromDate = new Date().getTime();
            String url = "https://capi.tianyancha.com/cloud-company-background/company/invest/statistics?_="+timeFromDate+"&gid="+companyBusiness.getCompanyId();
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;

            try {
                //获取DefaultHttpClient请求;
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                InputStream inputStream = response.getEntity().getContent();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                while(null != (line = bufferedReader.readLine())) {
                    result.append(line);
                }

                //处理返回参数
                JSONObject resultObj = JSONObject.parseObject(result.toString());
                if (resultObj.getJSONObject("data") != null){
                    JSONArray categoryInvest = resultObj.getJSONObject("data").getJSONArray("category");
                    JSONArray areaInvest = resultObj.getJSONObject("data").getJSONArray("area");
                    if (areaInvest != null){
                        for (int i = 0; i < areaInvest.size(); i++){
                            CusPersonaAreaInvest cusPersonaAreaInvest = new CusPersonaAreaInvest();
                            JSONObject areaInvestJSONObject = areaInvest.getJSONObject(i);
                            cusPersonaAreaInvest.setAreaName(areaInvestJSONObject.getString("name"));
                            cusPersonaAreaInvest.setAreaNum(Long.parseLong(areaInvestJSONObject.getString("num")));
                            cusPersonaAreaInvest.setAreaKey(areaInvestJSONObject.getString("key"));
                            cusPersonaAreaInvest.setCompanyId(companyBusiness.getCompanyId());
                            cusPersonaAreaInvest.setCompanyName(companyBusiness.getCompany());
                            cusPersonaAreaInvest.setCreateBy(userName);
                            cusPersonaAreaInvest.setCreateTime(DateUtils.getNowDate());

                            res = cusPersonaAreaInvestMapper.insertCusPersonaAreaInvest(cusPersonaAreaInvest);
                        }
                        for (int j = 0; j < categoryInvest.size(); j++){
                            CusPersonaCategoryInvest cusPersonaCategoryInvest = new CusPersonaCategoryInvest();
                            JSONObject categoryInvestJSONObject = categoryInvest.getJSONObject(j);
                            cusPersonaCategoryInvest.setCategoryName(categoryInvestJSONObject.getString("name"));
                            cusPersonaCategoryInvest.setCategoryNum(Long.parseLong(categoryInvestJSONObject.getString("num")));
                            cusPersonaCategoryInvest.setCategoryKey(categoryInvestJSONObject.getString("key"));
                            cusPersonaCategoryInvest.setCompanyId(companyBusiness.getCompanyId());
                            cusPersonaCategoryInvest.setCompanyName(companyBusiness.getCompany());
                            cusPersonaCategoryInvest.setCreateBy(userName);
                            cusPersonaCategoryInvest.setCreateTime(DateUtils.getNowDate());

                            res = res + cusPersonaCategoryInvestMapper.insertCusPersonaCategoryInvest(cusPersonaCategoryInvest);
                        }
                    }

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return res;
    }
}
