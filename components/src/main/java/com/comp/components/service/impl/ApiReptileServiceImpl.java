package com.comp.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.comp.components.domain.CustomerPortraitParameter;
import com.comp.components.domain.TyCompany;
import com.comp.components.domain.vo.CustomerBusinessVo;
import com.comp.components.service.*;
import com.comp.components.utils.PutBidUtils;
import com.ruoyi.common.utils.HttpApiUtils;
import com.ruoyi.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author: 11653
 * @createTime: 2024/03/21 15:05
 * @package: com.ruoyi.system.service.impl
 * @description:
 */

@Slf4j
@Service
public class ApiReptileServiceImpl implements ApiReptileService {

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

    /**
     * 自动客户单位画像
     *
     * company        简称
     * companyId      天眼查ID
     * fullCompany    公司全称
     * userName       用户名称
     * enterpriseType 祥云企业类型
     * @throws SQLException
     */
    @Override
    public void addCustomerReportRemind(CustomerPortraitParameter customerPortraitParameter) {
        try {

            //TODO: 2024/3/21 根据名称获取天眼查公司名称、公司ID

            //更新客户单位画像信息
            CustomerBusinessVo tycCompanyInfo = new CustomerBusinessVo();
            tycCompanyInfo.setCompany(customerPortraitParameter.getFullCompany());
            tycCompanyInfo.setCompanyId(customerPortraitParameter.getCompanyId());

            //获取客户单位的工商信息
            iCusPersonaCompanyBusinessService.addCompanyBusinessByTycCompanyId(
                    tycCompanyInfo, customerPortraitParameter.getUserName(),
                    customerPortraitParameter.getCompany(),
                    customerPortraitParameter.getEnterpriseType());
            //调用天眼查获取客户单位关系信息
            iCusPersonaCompanyNodeService.addCorporateRelationsByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位对外投资信息（处理好的数据，含有对外地区投资和行业投资）
            iCusPersonaAreaInvestService.addInvestByTycCompany(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位产品信息
            iCusPersonaCompanyProductService.addProductByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位证书信息
            iCusPersonaCompanyCertService.addCertByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位的客户数据
            iCusPersonaCompanyCustomerService.addCompanyCustomerByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位的供应商数据
            iCusPersonaCompanySupplierService.addSupplierByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位司法解析数据
            iCusPersonaJudicialCaseService.addJudicialCaseByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位开庭公告数据
            iCusPersonaCourtNoticeService.addTycCourtNoticeByCompany(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位法律诉讼数据
            iCusPersonaLegalService.addTycLegalByCompany(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取企业年报信息
            iCusPersonaAnnualReportsService.addAnnualReportsByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //获取招投标数据
            iPersonaBidsService.savePurchaserPersonaBidsByCompany(customerPortraitParameter.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int addAbnormalOperationByTyc(CustomerBusinessVo customerBusinessVo, String userName) {
        return 0;
    }

    @Override
    public List<TyCompany> selectCompanyListByTyc(String keyword, Integer pageNum) {
        List<TyCompany> companies = new ArrayList<>();
        List<TyCompany> temps = new ArrayList<>();
        //关键词查询天眼查公司列表(单位全称)
        String token ="6999f69e-60ab-4d88-8867-c5554915d36e"; //单价token
        String url ="http://open.api.tianyancha.com/services/open/search/2.0?word="+keyword+"&pageSize=20&pageNum="+pageNum;
        String result = HttpApiUtils.executeGet(url, token);
        //处理数据
        JSONObject resultObj = JSONObject.parseObject(result);
        if ("0".equals(resultObj.getString("error_code"))){
            //查询企业列表
            JSONArray lists=resultObj.getJSONObject("result").getJSONArray("items");
            JSONObject temp = new JSONObject();
            if (lists.size()>0) {
                for (Object object : lists) {
                    temp = JSONObject.parseObject(object.toString());
                    String matchtype =temp.getString("matchType");
                    String regstatus =temp.getString("regStatus");
                    String name =temp.getString("name");

                    //匹配度筛选
                    String[] strs = PutBidUtils.doKeyPost(keyword);
                    Boolean isMatch = StringUtils.containsWordsWithAC(name,strs);
                    if("公司名称匹配".equals(matchtype) && !"注销".equals(regstatus))  {
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
                        cy.setSameRatio(compareUtil.getSimilarityRatio(keyword,name));//匹配相似度
                        cy.setIsMatch(isMatch);//是否匹配关键词
                        companies.add(cy);
                    }
                }
                //排序
                companies.sort(Comparator.comparing(TyCompany::getSameRatio).reversed());//倒序
            }
        }else{
            throw new CustomException("第三方查询服务异常，请联系系统管理员。");
        }

        return companies;
    }
}
