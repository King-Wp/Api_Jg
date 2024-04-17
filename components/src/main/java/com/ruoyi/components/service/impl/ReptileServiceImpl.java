package com.ruoyi.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.BidStringUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.UserAgentUtil;
import com.ruoyi.components.domain.CusPersonaCompanyBusiness;
import com.ruoyi.components.domain.CustomerPortraitParameter;
import com.ruoyi.components.domain.ReceiveParameters.KeyWordsParams;
import com.ruoyi.components.domain.RtbReport;
import com.ruoyi.components.mapper.CusPersonaCompanyBusinessMapper;
import com.ruoyi.components.service.*;
import com.ruoyi.components.utils.HttpApiUtils;
import com.ruoyi.components.utils.PutBidUtils;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: 11653
 * @createTime: 2024/03/21 15:05
 * @package: com.ruoyi.system.service.impl
 * @description:
 */
@Service
public class ReptileServiceImpl implements IReptileService {

    private static final String KEYWORD_URL = "http://10.203.7.251:19002/CKE_out_results";

    private static final String SUCCESS_CODE = "0";

    @Resource
    private ICusPersonaCompanyBusinessService iCusPersonaCompanyBusinessService;
    @Resource
    private ICusPersonaCompanyNodeService iCusPersonaCompanyNodeService;
    @Resource
    private ICusPersonaAreaInvestService iCusPersonaAreaInvestService;
    @Resource
    private ICusPersonaCompanyProductService iCusPersonaCompanyProductService;
    @Resource
    private ICusPersonaCompanyCertService iCusPersonaCompanyCertService;
    @Resource
    private ICusPersonaCompanyCustomerService iCusPersonaCompanyCustomerService;
    @Resource
    private ICusPersonaCompanySupplierService iCusPersonaCompanySupplierService;
    @Resource
    private ICusPersonaJudicialCaseService iCusPersonaJudicialCaseService;
    @Resource
    private ICusPersonaCourtNoticeService iCusPersonaCourtNoticeService;
    @Resource
    private ICusPersonaLegalService iCusPersonaLegalService;
    @Resource
    private ICusPersonaAnnualReportsService iCusPersonaAnnualReportsService;
    @Resource
    private IPersonaBidsService iPersonaBidsService;
    @Resource
    private CusPersonaCompanyBusinessMapper cusPersonaCompanyBusinessMapper;

    @Override
    public void addCustomerReportRemind(CustomerPortraitParameter customerPortraitParameter) {
        //获取客户单位的工商信息
        iCusPersonaCompanyBusinessService.addCompanyBusinessByTycCompanyId(
                customerPortraitParameter.getTycCompanyInfo(), customerPortraitParameter.getUserName(),
                customerPortraitParameter.getCompany(),
                customerPortraitParameter.getEnterpriseType());
        //调用天眼查获取客户单位关系信息
        iCusPersonaCompanyNodeService.addCorporateRelationsByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位对外投资信息（处理好的数据，含有对外地区投资和行业投资）
        iCusPersonaAreaInvestService.addInvestByTycCompany(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位产品信息
        iCusPersonaCompanyProductService.addProductByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位证书信息
        iCusPersonaCompanyCertService.addCertByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位的客户数据
        iCusPersonaCompanyCustomerService.addCompanyCustomerByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位的供应商数据
        iCusPersonaCompanySupplierService.addSupplierByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位司法解析数据
        iCusPersonaJudicialCaseService.addJudicialCaseByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位开庭公告数据
        iCusPersonaCourtNoticeService.addTycCourtNoticeByCompany(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位法律诉讼数据
        iCusPersonaLegalService.addTycLegalByCompany(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取企业年报信息
        iCusPersonaAnnualReportsService.addAnnualReportsByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //获取招投标数据
        iPersonaBidsService.savePurchaserPersonaBidsByCompany(customerPortraitParameter.getUserName());
    }

    @Override
    public String[] keyToLevel(KeyWordsParams keyWordsParams) {
        if (ObjectUtils.isEmpty(keyWordsParams)) {
            return null;
        }
        String[] keyword = null;
        if (CollectionUtils.isNotEmpty(keyWordsParams.getBidTitles())
                && CollectionUtils.isNotEmpty(keyWordsParams.getContracts())
                && StringUtils.isNotEmpty(keyWordsParams.getBusiness())) {
            //封装数据
            Map<String, Object> map = new HashMap<>();
            map.put("id", 99999);
            map.put("level-p1", "");
            map.put("level-p2", keyWordsParams.getBidTitles().toArray(new String[0]));
            map.put("level-p3", keyWordsParams.getContracts().toArray(new String[0]));
            map.put("level-p4", StringUtils.nvl(keyWordsParams.getBusiness(), ""));
            map.put("level-p5", "");
            JSONArray arrs = new JSONArray();
            arrs.add(map);

            //提取关键词
            String result = PutBidUtils.doPost(KEYWORD_URL, arrs.toJSONString());
            JSONObject resultObj = JSONObject.parseObject(result);
            String code = (String) resultObj.get("code");
            JSONArray lists = resultObj.getJSONArray("data");
            JSONObject data = new JSONObject();
            if ("200".equals(code) && lists.size() > 0) {
                data = (JSONObject) lists.get(0);
            }
            if (data.getJSONArray("level-p2").size() > 0) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p2"));
            } else if (data.getJSONArray("level-p3").size() > 0) {
                keyword = com.ruoyi.common.utils.StringUtils.JsonToArray(data.getJSONArray("level-p3"));
            } else if (data.getJSONArray("level-p4").size() > 0) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p4"));
            }
        }
        return keyword;
    }

    @Override
    public String[] getVisitKeyWord(KeyWordsParams keyWordsParams) {
        JSONObject data = new JSONObject();
        if (CollectionUtils.isNotEmpty(keyWordsParams.getBidTitles())
                && CollectionUtils.isNotEmpty(keyWordsParams.getContracts())
                && StringUtils.isNotEmpty(keyWordsParams.getBusiness())) {
            //封装数据
            /**
             * "level-p2":"招投标标题",
             * "level-p3":"业绩合同名称",
             * "level-p4":"经营范围" ,  */
            Map<String, Object> map = new HashMap<>();
            map.put("id", keyWordsParams.getBaseId());
            map.put("level-p1", "");
            map.put("level-p2", keyWordsParams.getBidTitles().toArray(new String[0]));
            map.put("level-p3", keyWordsParams.getContracts().toArray(new String[0]));
            map.put("level-p4", StringUtils.nvl(keyWordsParams.getBusiness(), ""));
            map.put("level-p5", "");
            JSONArray arrs = new JSONArray();
            arrs.add(map);

            //提取关键词
            String result = PutBidUtils.doPost(KEYWORD_URL, arrs.toJSONString());
            JSONObject resultObj = JSONObject.parseObject(result);
            String code = (String) resultObj.get("code");
            JSONArray lists = resultObj.getJSONArray("data");
            if ("200".equals(code) && lists.size() > 0) {
                for (int i = 0; i < lists.size(); i++) {
                    data = (JSONObject) lists.get(i);
                }
            }
        }
        String[] keyword = null;
        if (ObjectUtils.isNotEmpty(data))
            if (CollectionUtils.isNotEmpty(data.getJSONArray("level-p2"))) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p2"));
            } else if (CollectionUtils.isNotEmpty(data.getJSONArray("level-p3"))) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p3"));
            } else if (CollectionUtils.isNotEmpty(data.getJSONArray("level-p4"))) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p4"));
            }
        return keyword;
    }

    @Override
    public JSONObject getReportKeyword(KeyWordsParams keyWordsParams) {
        JSONObject data = new JSONObject();
        RtbReport info = keyWordsParams.getInfo();
        if (StringUtils.isNotNull(info)) {
            String itemName = info.getItemName();//项目名称
            String content = info.getContent();//建设内容

            //封装数据
            Map<String, Object> map = new HashMap<>();
            map.put("id", keyWordsParams.getBaseId());
            map.put("level-p1", StringUtils.nvl(itemName, ""));
            map.put("level-p2", StringUtils.nvl(content, ""));
            map.put("level-p3", "");
            map.put("level-p4", "");
            map.put("level-p5", "");
            JSONArray arrs = new JSONArray();
            arrs.add(map);

            //提取关键词
            String result = PutBidUtils.doPost(KEYWORD_URL, arrs.toJSONString());
            JSONObject resultObj = JSONObject.parseObject(result);
            String code = (String) resultObj.get("code");
            JSONArray lists = resultObj.getJSONArray("data");
            if ("200".equals(code) && CollectionUtils.isNotEmpty(lists)) {
                for (int i = 0; i < lists.size(); i++) {
                    data = (JSONObject) lists.get(i);
                }
            }
        }
        return data;
    }

    @Override
    public void addBusinessFreeByTyc(String companyName, String userName) {
        int i = 0;
        long timeFromDate = new Date().getTime();
        String url = "https://capi.tianyancha.com/cloud-tempest/search/suggest/v3?_=" + timeFromDate;
        //请求ti
        JSONObject paramMap = new JSONObject();
        //查询关键字
        paramMap.put("keyword", companyName);
        boolean endFlag = false;
        //重连次数
        int rePostNum = 0;
        while (!endFlag) {
            try {
                String result = PutBidUtils.doPost(url, paramMap.toJSONString());
                Assert.notNull(result);
                JSONObject resultObj = JSONObject.parseObject(result);
                JSONArray data = resultObj.getJSONArray("data");
                if (data != null && data.size() > 0) {
                    String id = data.getJSONObject(0).getString("id");
                    String regStatus = data.getJSONObject(0).getString("regStatus");
                    //未注销的公司
                    if (Integer.parseInt(regStatus) == 0) {
                        String docPubUrl = "https://www.tianyancha.com/company/" + id;
                        Document doc = null;
                        // 利用jsoup连接目标url网页获取整个html对象
                        doc = Jsoup.connect(docPubUrl).userAgent(UserAgentUtil.getUserAgent()).get();
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

                                Assert.notNull(companyBusiness.getString("id"));
                                CusPersonaCompanyBusiness existBusinessInfo = cusPersonaCompanyBusinessMapper.existBusinessInfo(companyBusiness.getString("id"));
                                //企业工商表中未存在该companyId的信息，则入库，若存在，则将查询字符串添加到该工商信息的queryKeyword中
                                if (existBusinessInfo == null) {
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
                                        business.setCompanyOrgType(companyBusiness.getString("companyOrgType"));
                                    } else if (companyBusiness.getString("types") != null) {
                                        //事业单位和政府无companyOrgType字段，政府可从types获取企业性质
                                        business.setCompanyOrgType(companyBusiness.getString("types"));
                                    } else {
                                        JSONArray tagList = companyBusiness.getJSONArray("tagList");
                                        //数组中含有事业单位信息则设置该企业性质
                                        for (int j = 0; j < tagList.size(); j++) {
                                            if ("事业单位".equals(tagList.getJSONObject(j).getString("title"))) {
                                                business.setCompanyOrgType(tagList.getJSONObject(j).getString("title"));
                                            }
                                        }
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
                                    business.setCreateTime(DateUtils.getNowDate());
                                    business.setCreateBy(userName);
                                    business.setQueryKeyword(companyName);
                                    cusPersonaCompanyBusinessMapper.insertCusPersonaCompanyBusiness(business);
                                } else {
                                    //若存在，则将查询字符串添加到queryKeyword中
                                    String oldQueryKeyword = existBusinessInfo.getQueryKeyword();
                                    if (oldQueryKeyword == null) {
                                        existBusinessInfo.setQueryKeyword(companyName);
                                    } else {
                                        String[] split = oldQueryKeyword.split(",");
                                        boolean hasQuery = false;
                                        for (String query : split) {
                                            if (query.equals(companyName)) {
                                                //该公司名称已查询过
                                                hasQuery = true;
                                                break;
                                            }
                                        }
                                        if (!hasQuery) {
                                            existBusinessInfo.setQueryKeyword(oldQueryKeyword + "," + companyName);
                                        }
                                    }
                                    i = i + cusPersonaCompanyBusinessMapper.updateCusPersonaCompanyBusiness(existBusinessInfo);
                                }
                            }

                        }

                    }
                }
                endFlag = true;
                rePostNum = 0;
            } catch (HttpHostConnectException e) {
                //连接超时处理
                rePostNum++;
                if (rePostNum >= 5) {
                    endFlag = true;
                }
            } catch (Exception e) {
                endFlag = true;
                rePostNum = 0;
                e.printStackTrace();
            }

        }
    }

    @Override
    public String getCompanyProfile(String companyId) {
        String data ="";
        String url = "http://open.api.tianyancha.com/services/v4/open/profile?keyword=" + companyId;
        //获取查询数据
        String result = HttpApiUtils.getMessageByUrlToken(url);
        //处理返回参数
        JSONObject resultObj = JSONObject.parseObject(result);
        String code = resultObj.getString("error_code");
        if (SUCCESS_CODE.equals(code)){
            data = resultObj.getString("result");
        }
        return data;
    }
}
