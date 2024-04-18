package com.ruoyi.components.service.impl;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.*;
import com.ruoyi.components.domain.CusPersonaCompanyBusiness;
import com.ruoyi.components.mapper.CusPersonaCompanyBusinessMapper;
import com.ruoyi.components.service.ICusPersonaCompanyBusinessService;
import com.ruoyi.components.service.IReptileService;
import com.ruoyi.components.utils.HttpApiUtils;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.ObjectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
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
     * 客户单位的工商信息
     *
     * @param companyId      企业id
     * @param userName       用户名称
     * @param queryKeyword   查询关键字
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
        if (!customers.isEmpty()) {
            for (String keyword : customers) {
                String url = "http://open.api.tianyancha.com/services/open/ic/baseinfoV2/2.0?&keyword=" + keyword;
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
                        String companyProfile = getBusinessAliasByTyc(companyBusiness.getString("id"));
                        if (companyProfile != null) {
                            business.setCompanyProfile(companyProfile);
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
                    if (StringUtils.isNotNull(industryObj)) {
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
                    business.setCreateBy(SecurityUtils.getLoginUser().getUser().getUserName());
                    i = cusPersonaCompanyBusinessMapper.insertCusPersonaCompanyBusiness(business);
                }
            }
        }
        return i;
    }

    @Override
    public int cusPersonaBidsBusiness(String companyName, String userName, String id,String enterpriseType) {

        int mun = 0;

        try {
            String docPubUrl = "https://www.tianyancha.com/company/" + id;
            // 利用jsoup连接目标url网页获取整个html对象
            Document doc = Jsoup.connect(docPubUrl).userAgent(UserAgentUtil.getUserAgent()).get();

            //防止频繁访问（模拟网络延迟）
//                            Thread.sleep(500);
            if (doc != null) {
                Element nextData = doc.getElementById("__NEXT_DATA__");
                Assert.notNull(nextData, companyName);
                String s = BidStringUtils.subStringBetween(nextData.toString(), "<script id=\"__NEXT_DATA__\" type=\"application/json\">", "</script>");
                JSONObject jsonObject = JSONObject.parseObject(s);
                JSONArray queries = jsonObject.getJSONObject("props").getJSONObject("pageProps").getJSONObject("dehydratedState").getJSONArray("queries");
                if (queries.size() > 0) {

                    Elements span = doc.select("div[class^=index_detail-linewrap__AKtCa index_-intro__ma3Qd]").select("span");
                    JSONObject companyBusiness = queries.getJSONObject(0).getJSONObject("state").getJSONObject("data").getJSONObject("data");
                    CusPersonaCompanyBusiness business = new CusPersonaCompanyBusiness();

                    Assert.notNull(companyBusiness.getString("name"));
//                                CusPersonaCompanyBusiness existBusinessInfo = cusPersonaCompanyBusinessMapper.existBidWinnerBusinessInfo(companyBusiness.getString("name"));
//                                //企业工商表中未存在该companyId的信息，则入库，若存在，则不入库
//                                if (existBusinessInfo == null){
                    if (span.size() > 0) {
                        String companyProfile = span.get(1).text();
                        if (companyProfile != null) {
                            business.setCompanyProfile(companyProfile);
                        }
                    }
                    if (companyBusiness.getString("id") != null) {
                        business.setCompanyId(Long.parseLong(companyBusiness.getString("id")));
                    }
                    if (companyBusiness.getString("name") != null) {
                        business.setCompanyName(companyBusiness.getString("name"));
                    }
                    if (companyBusiness.getString("percentileScore") != null) {
                        business.setPercentileScore(companyBusiness.getString("percentileScore"));
                    }
                    if (companyBusiness.getString("email") != null) {
                        business.setCompanyEmail(companyBusiness.getString("email"));
                    }
                    if (companyBusiness.getString("actualCapital") != null) {
                        business.setActualCapital(companyBusiness.getString("actualCapital"));
                    }
                    if (companyBusiness.getString("actualCapitalCurrency") != null) {
                        business.setActualCapitalCurrency(companyBusiness.getString("actualCapitalCurrency"));
                    }

                    if (companyBusiness.getString("approvedTime") != null) {
                        business.setApprovedTime(DateUtils.dateToStamp(Long.valueOf(companyBusiness.getString("approvedTime"))));
                    }
                    if (companyBusiness.getString("base") != null) {
                        business.setBase(companyBusiness.getString("base"));
                    }
                    if (companyBusiness.getString("companyOrgType") != null) {
                        //对天眼查的企业类型进行处理，方便对行业类型进行统计
                        String companyOrgTypeString = "";
                        String companyOrgType = companyBusiness.getString("companyOrgType");
                        if (companyOrgType.contains("国有")) {
                            companyOrgTypeString = "国企";
                        } else if (companyOrgType.contains("股份")) {
                            companyOrgTypeString = "股份有限公司";
                        } else if (companyOrgType.contains("有限")) {
                            companyOrgTypeString = "有限责任公司";
                        } else {
                            companyOrgTypeString = companyOrgType;
                        }
                        business.setCompanyOrgType(companyOrgTypeString);
                    } else if (companyBusiness.getString("types") != null) {
                        //事业单位和政府无companyOrgType字段，政府可从types获取企业性质
                        String types = companyBusiness.getString("types");
                        if ("机关".equals(types)) {
                            business.setCompanyOrgType("政府机关");
                        }

                    } else {
                        JSONArray tagList = companyBusiness.getJSONArray("tagList");
                        //数组中含有事业单位信息则设置该企业性质
                        for (int j = 0; j < tagList.size(); j++) {
                            if ("事业单位".equals(tagList.getJSONObject(j).getString("title"))) {
                                business.setCompanyOrgType(tagList.getJSONObject(j).getString("title"));
                            }
                        }
                    }
                    if (companyBusiness.getString("taxQualification") != null) {
                        business.setTaxQualification(companyBusiness.getString("taxQualification"));
                    }
                    if (companyBusiness.getString("property3") != null) {
                        business.setEnglishName(companyBusiness.getString("property3"));
                    }

                    if (companyBusiness.getString("estiblishTime") != null) {
                        business.setEstiblishTime(DateUtils.dateToStamp(Long.valueOf(companyBusiness.getString("estiblishTime"))));
                    }
                    if (companyBusiness.getString("businessScope") != null) {
                        business.setBusinessScope(companyBusiness.getString("businessScope"));
                    }
                    if (companyBusiness.getString("historyNames") != null) {
                        business.setHistoryNames(companyBusiness.getString("historyNames"));
                    }
                    if (companyBusiness.getString("industry") != null) {
                        business.setIndustry(companyBusiness.getString("industry"));
                    }
                    if (companyBusiness.getString("isMicroEnt") != null) {
                        business.setIsMicroEnt(Integer.parseInt(companyBusiness.getString("isMicroEnt")));
                    }
                    if (companyBusiness.getString("legalPersonName") != null) {
                        business.setLegalPersonName(companyBusiness.getString("legalPersonName"));
                    }
                    if (companyBusiness.getString("orgNumber") != null) {
                        business.setOrgNumber(companyBusiness.getString("orgNumber"));
                    }
                    if (companyBusiness.getString("phoneNumber") != null) {
                        business.setPhoneNumber(companyBusiness.getString("phoneNumber"));
                    }
                    if (companyBusiness.getString("regCapital") != null) {
                        business.setRegCapital(companyBusiness.getString("regCapital"));
                    }
                    if (companyBusiness.getString("regCapitalCurrency") != null) {
                        business.setRegCapitalCurrency(companyBusiness.getString("regCapitalCurrency"));
                    }
                    if (companyBusiness.getString("regInstitute") != null) {
                        business.setRegInstitute(companyBusiness.getString("regInstitute"));
                    }
                    if (companyBusiness.getString("regLocation") != null) {
                        business.setRegLocation(companyBusiness.getString("regLocation"));
                    }
                    if (companyBusiness.getString("regNumber") != null) {
                        business.setRegNumber(companyBusiness.getString("regNumber"));
                    }
                    if (companyBusiness.getString("regStatus") != null) {
                        business.setRegStatus(companyBusiness.getString("regStatus"));
                    }
                    if (companyBusiness.getString("socialStaffNum") != null) {
                        business.setSocialStaffNum(companyBusiness.getString("socialStaffNum"));
                    }
                    if (companyBusiness.getString("staffNumRange") != null) {
                        business.setStaffNumRange(companyBusiness.getString("staffNumRange"));
                    }
                    if (companyBusiness.getString("taxNumber") != null) {
                        business.setTaxNumber(companyBusiness.getString("taxNumber"));
                    }
                    if (companyBusiness.getString("toTime") != null) {
                        business.setToTime(DateUtils.dateToStamp(Long.valueOf(companyBusiness.getString("toTime"))));
                    }
                    if (companyBusiness.getString("websiteList") != null) {
                        business.setWebsiteList(companyBusiness.getString("websiteList"));
                    }
                    if (companyBusiness.getString("creditCode") != null) {
                        business.setCreditCode(companyBusiness.getString("creditCode"));
                    }
                    if (companyBusiness.getString("logo") != null) {
                        business.setLogo(companyBusiness.getString("logo"));
                    }
                    if (companyBusiness.getString("alias") != null) {
                        business.setAlias(companyBusiness.getString("alias"));
                    }
                    JSONObject industryObj = companyBusiness.getJSONObject("industryAll");
                    if (industryObj != null) {
                        if (industryObj.getString("category") != null) {
                            business.setCategory(industryObj.getString("category"));
                        }
                        if (industryObj.getString("categoryBig") != null) {
                            business.setCategoryBig(industryObj.getString("categoryBig"));
                        }
                        if (industryObj.getString("categoryMiddle") != null) {
                            business.setCategoryMiddle(industryObj.getString("categoryMiddle"));
                        }
                        if (industryObj.getString("categorySmall") != null) {
                            business.setCategorySmall(industryObj.getString("categorySmall"));
                        }
                    }
                    //获取祥云中的企业性质
                    if (!(enterpriseType == null || "".equals(enterpriseType))) {
                        business.setEnterpriseType(enterpriseType);
                    }
                    business.setCreateTime(DateUtils.getNowDate());
                    business.setCreateBy(userName);
                    business.setQueryKeyword(companyName);
                    mun = cusPersonaCompanyBusinessMapper.insertCusPersonaBidsBusiness(business);
//                                }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return mun;
    }

    /**
     * 获取工商信息的logo
     *
     * @param companyId 企业id
     * @return
     */
    @Override
    public CusPersonaCompanyBusiness getBusinessLogoByTyc(String companyId) {
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

    /**
     * 公司简介
     *
     * @param companyId 天眼查id
     * @return 简介
     */
    @Override
    public String getBusinessAliasByTyc(String companyId) {
        String docPubUrl = "https://www.tianyancha.com/company/" + companyId;
        Document doc = null;
        String alias = "";
        try {
            // 利用jsoup连接目标url网页获取整个html对象
            doc = Jsoup.connect(docPubUrl).userAgent(UserAgentUtil.getUserAgent()).get();
            //防止频繁访问（模拟网络延迟）
//                            Thread.sleep(500);
            if (doc != null) {
                Element nextData = doc.getElementById("__NEXT_DATA__");
                if (nextData != null) {
                    String s = BidStringUtils.subStringBetween(nextData.toString(), "<script id=\"__NEXT_DATA__\" type=\"application/json\">", "</script>");
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    JSONArray queries = jsonObject.getJSONObject("props").getJSONObject("pageProps").getJSONObject("dehydratedState").getJSONArray("queries");
                    if (queries.size() > 0) {
                        JSONObject companyBusiness = queries.getJSONObject(0).getJSONObject("state").getJSONObject("data").getJSONObject("data");
                        if (companyBusiness.getString("alias") != null) {
                            alias = companyBusiness.getString("alias");
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return alias;
    }

}
