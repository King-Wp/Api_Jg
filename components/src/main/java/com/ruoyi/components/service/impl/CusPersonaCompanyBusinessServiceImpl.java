package com.ruoyi.components.service.impl;


import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.UserAgentUtil;
import com.ruoyi.components.domain.CusPersonaCompanyBusiness;
import com.ruoyi.components.mapper.CusPersonaCompanyBusinessMapper;
import com.ruoyi.components.service.ICusPersonaCompanyBusinessService;
import com.ruoyi.components.service.IReptileService;
import com.ruoyi.components.utils.HttpApiUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.ruoyi.common.enums.UrlAddressEnum.TOKEN_API;


/**
 * 公司详情工商信息Service业务层处理
 *
 * @author Lgy
 * @date 2024-04-09
 */
@Service
public class CusPersonaCompanyBusinessServiceImpl implements ICusPersonaCompanyBusinessService {

    private static final String SUCCESS_CODE = "0";

    @Resource
    private CusPersonaCompanyBusinessMapper cusPersonaCompanyBusinessMapper;
    @Resource
    private IReptileService iReptileService;

    /**
     *  客户单位的工商信息
     * @param companyId 企业id
     * @param userName 用户名称
     * @param queryKeyword 查询关键字
     * @param enterpriseType 获取祥云中的企业性质
     * @return 入库结果
     */
    @Override
    public int addCompanyBusinessByTycCompanyId(String companyId, String userName, String queryKeyword, String enterpriseType) {
        //调用天眼查-新增入库
        String url = "http://open.api.tianyancha.com/services/open/ic/baseinfoV2/2.0?&keyword=" + companyId;
        int i = 0;
        if (companyId != null) {
            //获取查询数据
            String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());
            //处理返回参数
            JSONObject resultObj = JSONObject.parseObject(result);

            String code = resultObj.getString("error_code");
            if (SUCCESS_CODE.equals(code)) {
                CusPersonaCompanyBusiness business = new CusPersonaCompanyBusiness();
                //获取企业基本信息
                JSONObject companyBusiness = resultObj.getJSONObject("result");
                if (StringUtils.isNotNull(companyBusiness.getString("id"))) {
                    business.setCompanyId(Long.parseLong(companyBusiness.getString("id")));
                    //爬取天眼查中的企业简介
                    String companyProfile = HttpApiUtils.getCompanyDetailSpider(companyBusiness.getString("id"));
                    if (companyProfile != null) {
                        business.setCompanyProfile(companyProfile);
                    } else {//调用天眼查接口获取简介
                        business.setCompanyProfile(iReptileService.getCompanyProfile(companyId));
                    }
                }
                if (StringUtils.isNotNull(companyBusiness.getString("name"))) {
                    business.setCompanyName(companyBusiness.getString("name"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("percentileScore"))) {
                    business.setPercentileScore(companyBusiness.getString("percentileScore"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("email"))) {
                    business.setCompanyEmail(companyBusiness.getString("email"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("actualCapital"))) {
                    business.setActualCapital(companyBusiness.getString("actualCapital"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("actualCapitalCurrency"))) {
                    business.setActualCapitalCurrency(companyBusiness.getString("actualCapitalCurrency"));
                }

                if (StringUtils.isNotNull(companyBusiness.getString("approvedTime"))) {
                    business.setApprovedTime(DateUtils.dateToStamp(Long.valueOf(companyBusiness.getString("approvedTime"))));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("base"))) {
                    business.setBase(companyBusiness.getString("base"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("companyOrgType"))) {
                    business.setCompanyOrgType(companyBusiness.getString("companyOrgType"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("property3"))) {
                    business.setEnglishName(companyBusiness.getString("property3"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("alias"))) {
                    business.setAlias(companyBusiness.getString("alias"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("estiblishTime"))) {
                    business.setEstiblishTime(DateUtils.dateToStamp(Long.valueOf(companyBusiness.getString("estiblishTime"))));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("businessScope"))) {
                    business.setBusinessScope(companyBusiness.getString("businessScope"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("historyNames"))) {
                    business.setHistoryNames(companyBusiness.getString("historyNames"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("industry"))) {
                    business.setIndustry(companyBusiness.getString("industry"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("isMicroEnt"))) {
                    business.setIsMicroEnt(Integer.parseInt(companyBusiness.getString("isMicroEnt")));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("legalPersonName"))) {
                    business.setLegalPersonName(companyBusiness.getString("legalPersonName"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("orgNumber"))) {
                    business.setOrgNumber(companyBusiness.getString("orgNumber"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("phoneNumber"))) {
                    business.setPhoneNumber(companyBusiness.getString("phoneNumber"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("regCapital"))) {
                    business.setRegCapital(companyBusiness.getString("regCapital"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("regCapitalCurrency"))) {
                    business.setRegCapitalCurrency(companyBusiness.getString("regCapitalCurrency"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("regInstitute"))) {
                    business.setRegInstitute(companyBusiness.getString("regInstitute"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("regLocation"))) {
                    business.setRegLocation(companyBusiness.getString("regLocation"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("regNumber"))) {
                    business.setRegNumber(companyBusiness.getString("regNumber"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("regStatus"))) {
                    business.setRegStatus(companyBusiness.getString("regStatus"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("socialStaffNum"))) {
                    business.setSocialStaffNum(companyBusiness.getString("socialStaffNum"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("staffNumRange"))) {
                    business.setStaffNumRange(companyBusiness.getString("staffNumRange"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("taxNumber"))) {
                    business.setTaxNumber(companyBusiness.getString("taxNumber"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("toTime"))) {
                    business.setToTime(DateUtils.dateToStamp(Long.valueOf(companyBusiness.getString("toTime"))));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("websiteList"))) {
                    business.setWebsiteList(companyBusiness.getString("websiteList"));
                }
                if (StringUtils.isNotNull(companyBusiness.getString("creditCode"))) {
                    business.setCreditCode(companyBusiness.getString("creditCode"));
                }
                JSONObject industryObj = companyBusiness.getJSONObject("industryAll");
                if (industryObj != null) {
                    if (StringUtils.isNotEmpty(industryObj.getString("category"))) {
                        business.setCategory(industryObj.getString("category"));
                    }
                    if (StringUtils.isNotNull(industryObj.getString("categoryBig"))) {
                        business.setCategoryBig(industryObj.getString("categoryBig"));
                    }
                    if (StringUtils.isNotNull(industryObj.getString("categoryMiddle"))) {
                        business.setCategoryMiddle(industryObj.getString("categoryMiddle"));
                    }
                    if (StringUtils.isNotNull(industryObj.getString("categorySmall"))) {
                        business.setCategorySmall(industryObj.getString("categorySmall"));
                    }
                }
                business.setCreateTime(DateUtils.getNowDate());
                business.setCreateBy(userName);
                business.setQueryKeyword(queryKeyword);
                //获取祥云中的企业性质
                if (StringUtils.isNotEmpty(enterpriseType)) {
                    business.setEnterpriseType(enterpriseType);
                }
                //调用天眼查免费的获取工商信息接口，拿到企业logo和纳税人资质
                CusPersonaCompanyBusiness businessFree = getBusinessLogoByTyc(companyId);
                if (!(businessFree.getLogo() == null || "".equals(businessFree.getLogo()))) {
                    business.setLogo(businessFree.getLogo());
                }
                if (!(businessFree.getTaxQualification() == null || "".equals(businessFree.getTaxQualification()))) {
                    business.setTaxQualification(businessFree.getTaxQualification());
                }

                i = cusPersonaCompanyBusinessMapper.insertCusPersonaCompanyBusiness(business);
            }
        }
        return i;
    }

    @Override
    public int addCompanyBusinessByTyc(List<String> customers) {
        //调用天眼查-新增入库
        //客户单位列表
//        String keyword = "广西电网有限责任公司";
        int i = 0;
        if (!customers.isEmpty()){
            for (String keyword : customers){
                String url = "http://open.api.tianyancha.com/services/open/ic/baseinfoV2/2.0?&keyword="+keyword;
                //获取查询数据
                String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());
                //处理返回参数
                JSONObject resultObj = JSONObject.parseObject(result);

                String code = resultObj.getString("error_code");
                if (SUCCESS_CODE.equals(code)){
                    CusPersonaCompanyBusiness business = new CusPersonaCompanyBusiness();
                    //获取企业基本信息
                    JSONObject companyBusiness = resultObj.getJSONObject("result");
                    if (StringUtils.isNotNull(companyBusiness.getString("id"))){
                        business.setCompanyId(Long.parseLong(companyBusiness.getString("id")));
                        //爬取天眼查中的企业简介
                        String companyProfile = getCompanyDetailSpider(companyBusiness.getString("id"));
                        if (companyProfile != null){
                            business.setCompanyProfile(companyProfile);
                        }
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("name"))){
                        business.setCompanyName(companyBusiness.getString("name"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("percentileScore"))){
                        business.setPercentileScore(companyBusiness.getString("percentileScore"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("email"))){
                        business.setCompanyEmail(companyBusiness.getString("email"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("actualCapital"))){
                        business.setActualCapital(companyBusiness.getString("actualCapital"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("actualCapitalCurrency"))){
                        business.setActualCapitalCurrency(companyBusiness.getString("actualCapitalCurrency"));
                    }

                    if (StringUtils.isNotNull(companyBusiness.getString("approvedTime"))){
                        business.setApprovedTime(DateUtils.dateToStamp(Long.valueOf(companyBusiness.getString("approvedTime"))));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("base"))){
                        business.setBase(companyBusiness.getString("base"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("companyOrgType"))){
                        business.setCompanyOrgType(companyBusiness.getString("companyOrgType"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("property3"))){
                        business.setEnglishName(companyBusiness.getString("property3"));
                    }

                    if (StringUtils.isNotNull(companyBusiness.getString("estiblishTime"))){
                        business.setEstiblishTime(DateUtils.dateToStamp(Long.valueOf(companyBusiness.getString("estiblishTime"))));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("businessScope"))){
                        business.setBusinessScope(companyBusiness.getString("businessScope"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("historyNames"))){
                        business.setHistoryNames(companyBusiness.getString("historyNames"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("industry"))){
                        business.setIndustry(companyBusiness.getString("industry"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("isMicroEnt"))){
                        business.setIsMicroEnt(Integer.parseInt(companyBusiness.getString("isMicroEnt")));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("legalPersonName"))){
                        business.setLegalPersonName(companyBusiness.getString("legalPersonName"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("orgNumber"))){
                        business.setOrgNumber(companyBusiness.getString("orgNumber"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("phoneNumber"))){
                        business.setPhoneNumber(companyBusiness.getString("phoneNumber"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("regCapital"))){
                        business.setRegCapital(companyBusiness.getString("regCapital"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("regCapitalCurrency"))){
                        business.setRegCapitalCurrency(companyBusiness.getString("regCapitalCurrency"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("regInstitute"))){
                        business.setRegInstitute(companyBusiness.getString("regInstitute"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("regLocation"))){
                        business.setRegLocation(companyBusiness.getString("regLocation"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("regNumber"))){
                        business.setRegNumber(companyBusiness.getString("regNumber"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("regStatus"))){
                        business.setRegStatus(companyBusiness.getString("regStatus"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("socialStaffNum"))){
                        business.setSocialStaffNum(companyBusiness.getString("socialStaffNum"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("staffNumRange"))){
                        business.setStaffNumRange(companyBusiness.getString("staffNumRange"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("taxNumber"))){
                        business.setTaxNumber(companyBusiness.getString("taxNumber"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("toTime"))){
                        business.setToTime(DateUtils.dateToStamp(Long.valueOf(companyBusiness.getString("toTime"))));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("websiteList"))){
                        business.setWebsiteList(companyBusiness.getString("websiteList"));
                    }
                    if (StringUtils.isNotNull(companyBusiness.getString("creditCode"))){
                        business.setCreditCode(companyBusiness.getString("creditCode"));
                    }
                    JSONObject industryObj = companyBusiness.getJSONObject("industryAll");
                    if (StringUtils.isNotNull(industryObj)){
                        if (StringUtils.isNotEmpty(industryObj.getString("category"))){
                            business.setCategory(industryObj.getString("category"));
                        }
                        if (StringUtils.isNotNull(industryObj.getString("categoryBig"))){
                            business.setCategoryBig(industryObj.getString("categoryBig"));
                        }
                        if (StringUtils.isNotNull(industryObj.getString("categoryMiddle"))){
                            business.setCategoryMiddle(industryObj.getString("categoryMiddle"));
                        }
                        if (StringUtils.isNotNull(industryObj.getString("categorySmall"))){
                            business.setCategorySmall(industryObj.getString("categorySmall"));
                        }
                    }

                    business.setCreateTime(DateUtils.getNowDate());
                    business.setCreateBy(SecurityUtils.getLoginUser().getUser().getUserName());
                    i = cusPersonaCompanyBusinessMapper.insertCusPersonaCompanyBusiness(business);
                }
            }
        }
        return i;
    }

    /**
     * 获取工商信息的logo
     *
     * @param companyId
     * @return
     */
    private CusPersonaCompanyBusiness getBusinessLogoByTyc(String companyId) {
        JSONObject companyBusiness = HttpApiUtils.getBusinessLogoByTyc(companyId);
        CusPersonaCompanyBusiness business = new CusPersonaCompanyBusiness();

        if (ObjectUtils.isNotEmpty(companyBusiness)) {
            if (companyBusiness.getString("logo") != null) {
                business.setLogo(companyBusiness.getString("logo"));
            }
            if (companyBusiness.getString("taxQualification") != null) {
                business.setTaxQualification(companyBusiness.getString("taxQualification"));
            }
        }
        return business;
    }

    private static String getCompanyDetailSpider(String companyId){
        String docPubUrl = "https://www.tianyancha.com/company/"+companyId;
        Document doc = null;
        String companyProfile = "";
        // 利用jsoup连接目标url网页获取整个html对象
        try {
            doc = Jsoup.connect(docPubUrl).userAgent(UserAgentUtil.getUserAgent()).get();
            //防止频繁访问（模拟网络延迟）
            int temp = (int)(Math.random()*10000);
            Thread.sleep(temp);
            if (doc != null) {

                //获取主页面的HTML对象
                Elements span = doc.select("div[class^=index_detail-linewrap__AKtCa index_-intro__ma3Qd]").select("span");
                if (span.size() > 0){
                    companyProfile = doc.select("div[class^=index_detail-linewrap__AKtCa index_-intro__ma3Qd]").select("span").get(1).text();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return companyProfile;
    }

}
