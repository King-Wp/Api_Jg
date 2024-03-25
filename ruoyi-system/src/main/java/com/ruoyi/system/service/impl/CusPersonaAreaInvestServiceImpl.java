package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.CusPersonaAreaInvest;
import com.ruoyi.system.domain.CusPersonaCategoryInvest;
import com.ruoyi.system.domain.vo.CustomerBusinessVo;
import com.ruoyi.system.mapper.CusPersonaAreaInvestMapper;
import com.ruoyi.system.mapper.CusPersonaCategoryInvestMapper;
import com.ruoyi.system.service.ICusPersonaAreaInvestService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

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
     * @param customerBusinessVo 天眼查的公司ID, 客户单位名称
     * @return 入库结果
     */
    @Async
    @Override
    public void addInvestByTycCompany(CustomerBusinessVo customerBusinessVo, String userName) {
        String companyId = customerBusinessVo.getCompanyId();
        String companyName = customerBusinessVo.getCompany();
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
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
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
        logger.info(companyName + "入库" + res + "条对外投资记录");
    }
}
