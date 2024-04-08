package com.comp.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.comp.components.domain.CusPersonaAreaInvest;
import com.comp.components.domain.CusPersonaCategoryInvest;
import com.comp.components.domain.CusPersonaCompanyBusiness;
import com.comp.components.domain.CustomerPortraitParameter;
import com.comp.components.domain.vo.CustomerBusinessVo;
import com.comp.components.mapper.CustomersVoMapper;
import com.comp.components.service.*;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
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
}
