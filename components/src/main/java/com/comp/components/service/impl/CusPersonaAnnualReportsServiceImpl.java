package com.comp.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.comp.components.domain.CusPersonaAnnualReports;
import com.comp.components.domain.vo.CustomerBusinessVo;
import com.comp.components.mapper.CusPersonaAnnualReportsMapper;
import com.comp.components.service.ICusPersonaAnnualReportsService;
import com.ruoyi.common.enums.UrlAddressEnum;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.HttpApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 企业年报Service业务层处理
 * 
 * @author wucilong
 * @date 2022-12-29
 */
@Service
public class CusPersonaAnnualReportsServiceImpl implements ICusPersonaAnnualReportsService
{
    @Autowired
    private CusPersonaAnnualReportsMapper cusPersonaAnnualReportsMapper;

    private static final Logger logger = LoggerFactory.getLogger(CusPersonaAnnualReportsServiceImpl.class);

    @Override
    public void addAnnualReportsByTyc(CustomerBusinessVo customerBusinessVo, String userName) {
        //新增数量
        int num = 0;
        //调用天眼查-新增入库
        String companyId = customerBusinessVo.getCompanyId();
        String companyName = customerBusinessVo.getCompany();

        //是否终止循环变量
        boolean endLoop = false;
        // 重连次数
        int reconnect = 0;
        List<CusPersonaAnnualReports> cusPersonaAnnualReportsList = new ArrayList<>();
        while (!endLoop) {
            String url = "http://open.api.tianyancha.com/services/open/ic/annualreport/2.0?keyword=" + companyId;
            try {
                String result = HttpApiUtils.executeGet(url, UrlAddressEnum.TOKEN_API.getUrl());
                JSONObject resultObj = JSONObject.parseObject(result);
                String code = resultObj.getString("error_code");
                //判断异常信息
                if (!"0".equals(code)) {
                    break;
                }

                JSONArray lists = resultObj.getJSONObject("result").getJSONArray("items");

                JSONObject temp = new JSONObject();
                if (lists.size() > 0) {
                    for (Object object : lists) {
                        temp = JSONObject.parseObject(object.toString());
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        CusPersonaAnnualReports annualReports = new CusPersonaAnnualReports();
                        annualReports.setCompanyId(companyId);
                        if (!(temp.getString("releaseTime") == null || "".equals(temp.getString("releaseTime")))){
                            annualReports.setReleaseTime(simpleDateFormat.parse(temp.getString("releaseTime")));
                        }
                        //获取年报基本信息
                        JSONObject baseInfo = temp.getJSONObject("baseInfo");
                        if (baseInfo !=  null){
                            annualReports.setEmployeeNum(baseInfo.getString("employeeNum"));
                            annualReports.setTotalAssets(baseInfo.getString("totalAssets"));
                            annualReports.setTotalProfit(baseInfo.getString("totalProfit"));
                            annualReports.setTotalLiability(baseInfo.getString("totalLiability"));
                            annualReports.setCompanyName(baseInfo.getString("companyName"));
                            annualReports.setPostcode(baseInfo.getString("postcode"));
                            annualReports.setTotalSales(baseInfo.getString("totalSales"));
                            annualReports.setRetainedProfit(baseInfo.getString("retainedProfit"));
                            annualReports.setTotalTax(baseInfo.getString("totalTax"));
                            annualReports.setTotalEquity(baseInfo.getString("totalEquity"));
                            annualReports.setCreditCode(baseInfo.getString("creditCode"));
                            annualReports.setPhoneNumber(baseInfo.getString("phoneNumber"));
                            annualReports.setPostalAddress(baseInfo.getString("postalAddress"));
                            annualReports.setPrimeBusProfit(baseInfo.getString("primeBusProfit"));
                            annualReports.setManageState(baseInfo.getString("manageState"));
                            annualReports.setEmail(baseInfo.getString("email"));
                        }


                        //获取年报社保信息
                        JSONObject socialSecurity = temp.getJSONObject("reportSocialSecurityInfo");
                        if (socialSecurity != null){
                            annualReports.setMedicalInsurancePayAmount(socialSecurity.getString("medicalInsurancePayAmount"));
                            annualReports.setMedicalInsuranceOweAmount(socialSecurity.getString("medicalInsuranceOweAmount"));
                            annualReports.setUnemploymentInsuranceBase(socialSecurity.getString("unemploymentInsuranceBase"));
                            annualReports.setUnemploymentInsuranceOweAmount(socialSecurity.getString("unemploymentInsuranceOweAmount"));
                            annualReports.setUnemploymentInsurancePayAmount(socialSecurity.getString("unemploymentInsurancePayAmount"));
                            annualReports.setEndowmentInsurance(socialSecurity.getString("endowmentInsurance"));
                            annualReports.setMaternityInsurancePayAmount(socialSecurity.getString("maternityInsurancePayAmount"));
                            annualReports.setEmploymentInjuryInsurancePayAmount(socialSecurity.getString("employmentInjuryInsurancePayAmount"));
                            annualReports.setMedicalInsurance(socialSecurity.getString("medicalInsurance"));
                            annualReports.setMedicalInsuranceBase(socialSecurity.getString("medicalInsuranceBase"));
                            annualReports.setUnemploymentInsurance(socialSecurity.getString("unemploymentInsurance"));
                            annualReports.setEndowmentInsuranceBase(socialSecurity.getString("endowmentInsuranceBase"));
                            annualReports.setMaternityInsuranceBase(socialSecurity.getString("maternityInsuranceBase"));
                            annualReports.setEndowmentInsurancePayAmount(socialSecurity.getString("endowmentInsurancePayAmount"));
                            annualReports.setEmploymentInjuryInsurance(socialSecurity.getString("employmentInjuryInsurance"));
                            annualReports.setEndowmentInsuranceOweAmount(socialSecurity.getString("endowmentInsuranceOweAmount"));
                            annualReports.setMaternityInsurance(socialSecurity.getString("maternityInsurance"));
                            annualReports.setMaternityInsuranceOweAmount(socialSecurity.getString("maternityInsuranceOweAmount"));
                            annualReports.setEmploymentInjuryInsuranceOweAmount(socialSecurity.getString("employmentInjuryInsuranceOweAmount"));

                        }

                        //获取网站信息
                        JSONArray webInfoList = temp.getJSONArray("webInfoList");
                        if (webInfoList != null && webInfoList.size() > 0){
                            JSONObject webInfo = webInfoList.getJSONObject(0);
                            annualReports.setWebsite(webInfo.getString("website"));
                            annualReports.setWebName(webInfo.getString("name"));
                        }
                        annualReports.setCreateBy(userName);
                        annualReports.setCreateTime(DateUtils.getNowDate());
                        cusPersonaAnnualReportsList.add(annualReports);

                    }
                    for (CusPersonaAnnualReports annual : cusPersonaAnnualReportsList){
                        num = num + cusPersonaAnnualReportsMapper.insertCusPersonaAnnualReports(annual);
                    }
                }
                endLoop = true;
            } catch (RuntimeException e) {
                cusPersonaAnnualReportsList.clear();
                if (reconnect < 1) {
                    endLoop = false;
                    reconnect++;
                } else {
                    endLoop = true;
                    reconnect = 0;
                }
                e.printStackTrace();
            }
            catch (ParseException e) {
                endLoop = true;
                reconnect = 0;
                e.printStackTrace();
            }
        }
        logger.info(companyName + "入库" + num + "条企业年报记录");
    }
}
