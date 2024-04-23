package com.ruoyi.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.components.domain.CusPersonaAbnormalOperation;
import com.ruoyi.components.domain.CusPersonaChattelMortgage;
import com.ruoyi.components.domain.CusPersonaCompanyBusiness;
import com.ruoyi.components.domain.TyCompany;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import com.ruoyi.components.exception.CustomException;
import com.ruoyi.components.mapper.CusPersonaAbnormalOperationMapper;
import com.ruoyi.components.mapper.CusPersonaChattelMortgageMapper;
import com.ruoyi.components.service.ApiThirdPartyService;
import com.ruoyi.components.service.ICusPersonaCompanyBusinessService;
import com.ruoyi.components.service.IReptileService;
import com.ruoyi.components.utils.HttpApiUtils;
import com.ruoyi.components.utils.PutBidUtils;
import com.ruoyi.components.utils.compareUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.ruoyi.common.enums.UrlAddressEnum.TOKEN_API;
import static com.ruoyi.common.enums.UrlAddressEnum.TOKEN_UNIT_PRICE;
import static com.ruoyi.components.utils.HttpApiUtils.getCompanyDetailSpider;

/**
 * @author: 11653
 * @createTime: 2024/04/07 16:58
 * @package: com.comp.components.service.impl
 * @description:
 */
@Slf4j
@Service
public class ApiThirdPartyServiceImpl implements ApiThirdPartyService {

    private static final String SUCCESS_CODE = "0";

    @Resource
    private CusPersonaAbnormalOperationMapper cusPersonaAbnormalOperationMapper;
    @Resource
    private CusPersonaChattelMortgageMapper cusPersonaChattelMortgageMapper;
    @Resource
    private CusPersonaCompanyBusinessServiceImpl cusPersonaCompanyBusinessService;
    @Resource
    private IReptileService iReptileService;
    @Resource
    private ICusPersonaCompanyBusinessService icusPersonaCompanyBusinessService;

    @Override
    public List<TyCompany> selectCompanyListByTyc(String keyword, Integer pageNum) {
        List<TyCompany> companies = new ArrayList<>();
        //关键词查询天眼查公司列表(单位全称)
        String url = "http://open.api.tianyancha.com/services/open/search/2.0?word=" + keyword + "&pageSize=20&pageNum=" + pageNum;
        String result = HttpApiUtils.executeGet(url, TOKEN_UNIT_PRICE.getVal());
        //处理数据
        JSONObject resultObj = JSONObject.parseObject(result);
        if (ObjectUtils.isNotEmpty(resultObj) && "0".equals(resultObj.getString("error_code"))) {
            //查询企业列表
            JSONArray lists = resultObj.getJSONObject("result").getJSONArray("items");
            if (ObjectUtils.isNotEmpty(lists)) {
                for (Object object : lists) {
                    JSONObject temp = JSONObject.parseObject(object.toString());
                    String matchtype = temp.getString("matchType");
                    String regstatus = temp.getString("regStatus");
                    String name = temp.getString("name");

                    //匹配度筛选
                    String[] strs = PutBidUtils.doKeyPost(keyword);
                    Boolean isMatch = StringUtils.containsWordsWithAC(name, strs);
                    if ("公司名称匹配".equals(matchtype) && !"注销".equals(regstatus)) {
                        TyCompany cy = new TyCompany();
                        cy.setRegnumber(temp.getString("regnumber"));//注册号
                        cy.setRegstatus(regstatus);//经营状态
                        cy.setCreditcode(temp.getString("creditCode"));//统一社会信用代码
                        cy.setEstiblishtime(temp.getString("estiblishTime"));//成立日期
                        cy.setRegcapital(temp.getString("regCapital"));//注册资本
                        cy.setCompanytype(Long.valueOf(temp.getString("companyType")));//机构类型-1：公司；2：香港企业；3：社会组织；4：律所；5：事业单位；6：基金会；7-不存在法人、注册资本、统一社会信用代码、经营状态;8：台湾企业；9-新机构
                        cy.setName(name);//公司名称
                        cy.setCompanyId(Long.valueOf(temp.getString("id")));//公司id
                        cy.setOrgnumber(temp.getString("orgNumber"));//组织机构代码
                        cy.setType(Long.valueOf(temp.getString("type")));//1-公司 2-人
                        cy.setBase(temp.getString("base"));//省份
                        cy.setLegalpersonname(temp.getString("legalPersonName"));//法人
                        cy.setMatchtype(matchtype);//匹配原因
                        cy.setSameRatio(compareUtil.getSimilarityRatio(keyword, name));//匹配相似度
                        cy.setIsMatch(isMatch);//是否匹配关键词
                        companies.add(cy);
                    }
                }
                //排序
                companies.sort(Comparator.comparing(TyCompany::getSameRatio).reversed());//倒序
            }
        } else {
            throw new CustomException("第三方查询服务异常，请联系系统管理员。");
        }

        return companies;
    }

    @Override
    public int addAbnormalOperationByTyc(CustomerBusinessVo customerBusinessVo) {
        //新增数量
        int num = 0;
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
            //将访问的页数加一
            pageNum++;
            String url = "http://open.api.tianyancha.com/services/open/mr/abnormal/2.0?pageSize=20&keyword=" + companyId + "&pageNum=" + pageNum;
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
            if (resultObj != null) {
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

                        JSONObject temp = new JSONObject();
                        if (lists.size() > 0) {
                            for (Object object : lists) {
                                try {
                                    temp = JSONObject.parseObject(object.toString());
                                    CusPersonaAbnormalOperation abnormalOperation = new CusPersonaAbnormalOperation();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    abnormalOperation.setCompanyName(companyName);
                                    abnormalOperation.setCompanyId(companyId);
                                    if (!(temp.getString("putDate") == null || "".equals(temp.getString("putDate")))) {
                                        abnormalOperation.setPutDate(format.parse(temp.getString("putDate")));
                                    }

                                    abnormalOperation.setPutDepartment(temp.getString("putDepartment"));
                                    abnormalOperation.setPutReason(temp.getString("putReason"));
                                    if (!(temp.getString("removeDate") == null || "".equals(temp.getString("removeDate")))) {
                                        abnormalOperation.setRemoveDate(format.parse(temp.getString("removeDate")));
                                    }
                                    abnormalOperation.setRemoveReason(temp.getString("removeReason"));
                                    abnormalOperation.setCreateBy(customerBusinessVo.getCreateBy());
                                    abnormalOperation.setCreateTime(DateUtils.getNowDate());
                                    num = num + cusPersonaAbnormalOperationMapper.insertCusPersonaAbnormalOperation(abnormalOperation);

                                } catch (RuntimeException | ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        endLoop = true;
                    }
                } else if ("300004".equals(code) || "300001".equals(code)) {
                    //若频繁访问或访问失败，则重新访问该页面数据
                    if (reconnect < 2) {
                        pageNum = pageNum - 1;
                        reconnect++;
                    } else {
                        //重新访问超过一定次数则退出循环
                        endLoop = true;
                        reconnect = 0;
                    }
                } else {
                    //其他原因则退出循环
                    break;
                }
            }
        }
        return num;
    }

    @Override
    public int addChattelMortgageByTyc(CustomerBusinessVo customerBusinessVo) {
        //新增数量
        int num = 0;
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
            //将访问的页数加一
            pageNum++;
            String url = "http://open.api.tianyancha.com/services/open/mr/mortgageInfo/2.0?pageSize=20&keyword=" + companyId + "&pageNum=" + pageNum;
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
            if (resultObj != null) {
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

                        JSONObject temp = new JSONObject();
                        if (lists.size() > 0) {
                            for (Object object : lists) {
                                try {
                                    temp = JSONObject.parseObject(object.toString()).getJSONObject("baseInfo");
                                    CusPersonaChattelMortgage chattelMortgage = new CusPersonaChattelMortgage();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    chattelMortgage.setCompanyId(companyId);
                                    chattelMortgage.setCompanyName(companyName);
                                    if (!(temp.getString("regDate") == null || "".equals(temp.getString("regDate")))) {
                                        chattelMortgage.setRegDate(format.parse(temp.getString("regDate")));
                                    }
                                    chattelMortgage.setAmount(temp.getString("amount"));
                                    if (!(temp.getString("publishDate") == null || "".equals(temp.getString("publishDate")))) {
                                        chattelMortgage.setPublishDate(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishDate"))));
                                    }
                                    chattelMortgage.setRegDepartment(temp.getString("regDepartment"));
                                    chattelMortgage.setTerm(temp.getString("term"));
                                    chattelMortgage.setRegNum(temp.getString("regNum"));
                                    chattelMortgage.setType(temp.getString("type"));
                                    chattelMortgage.setCreateBy(customerBusinessVo.getCreateBy());
                                    chattelMortgage.setCreateTime(DateUtils.getNowDate());

                                    num = num + cusPersonaChattelMortgageMapper.insertCusPersonaChattelMortgage(chattelMortgage);

                                } catch (RuntimeException | ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    } else {
                        endLoop = true;
                    }
                } else if ("300004".equals(code) || "300001".equals(code)) {
                    //若频繁访问或访问失败，则重新访问该页面数据
                    if (reconnect < 2) {
                        pageNum = pageNum - 1;
                        reconnect++;
                    } else {
                        //重新访问超过一定次数则退出循环
                        endLoop = true;
                        reconnect = 0;
                    }
                } else {
                    //其他原因则退出循环
                    break;
                }

            }

        }
        return num;
    }

    @Override
    public int addCompanyBusinessByTyc(List<String> customers) {
        return cusPersonaCompanyBusinessService.addCompanyBusinessByTyc(customers);
    }

    @Override
    public CusPersonaCompanyBusiness getNewBusinessInformation(String companyId, String companyName, String userName,
                                                String queryKeyword,String enterpriseType) {
        CusPersonaCompanyBusiness business = new CusPersonaCompanyBusiness();
        if (companyId != null) {
            String url = "http://open.api.tianyancha.com/services/open/ic/baseinfoV2/2.0?&keyword=" + companyId;
            //获取查询数据
            String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());
            //处理返回参数
            JSONObject resultObj = JSONObject.parseObject(result);

            String code = resultObj.getString("error_code");
            if (SUCCESS_CODE.equals(code)) {
                //获取企业基本信息
                JSONObject companyBusiness = resultObj.getJSONObject("result");
                if (StringUtils.isNotNull(companyBusiness.getString("id"))) {
                    business.setCompanyId(Long.parseLong(companyBusiness.getString("id")));
                    //爬取天眼查中的企业简介
                    String companyProfile = getCompanyDetailSpider(companyBusiness.getString("id"));
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
                business.setCreateBy(userName);
                business.setQueryKeyword(queryKeyword);
                //获取祥云中的企业性质
                if (!(enterpriseType == null || "".equals(enterpriseType))) {
                    business.setEnterpriseType(enterpriseType);
                }
                //调用天眼查免费的获取工商信息接口，拿到企业logo和纳税人资质
                CusPersonaCompanyBusiness businessFree = icusPersonaCompanyBusinessService.getBusinessLogoByTyc(companyId);;
                if (!(businessFree.getLogo() == null || "".equals(businessFree.getLogo()))) {
                    business.setLogo(businessFree.getLogo());
                }
                if (!(businessFree.getTaxQualification() == null || "".equals(businessFree.getTaxQualification()))) {
                    business.setTaxQualification(businessFree.getTaxQualification());
                }
            }
        }
        return business;
    }
}
