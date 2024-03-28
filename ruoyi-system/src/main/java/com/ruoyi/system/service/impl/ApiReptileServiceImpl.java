package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.vo.CustomerBusinessVo;
import com.ruoyi.system.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * @author: 11653
 * @createTime: 2024/03/21 15:05
 * @package: com.ruoyi.system.service.impl
 * @description:
 */

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

    /**
     * 自动客户单位画像
     *
     * @param company        简称
     * @param companyId      天眼查ID
     * @param fullCompany    公司全称
     * @param userName       用户名称
     * @param enterpriseType 祥云企业类型
     * @throws SQLException
     */
    @Override
    public void aotoCustomerReportRemind(String company, String companyId, String fullCompany, String userName, String enterpriseType) throws SQLException {
        try {
            //更新客户单位画像信息
            CustomerBusinessVo tycCompanyInfo = new CustomerBusinessVo();
            tycCompanyInfo.setCompany(fullCompany);
            tycCompanyInfo.setCompanyId(companyId);

            //获取客户单位的工商信息
            iCusPersonaCompanyBusinessService.addCompanyBusinessByTycCompanyId(tycCompanyInfo, userName, company, enterpriseType);
            //调用天眼查获取客户单位关系信息
            iCusPersonaCompanyNodeService.addCorporateRelationsByTyc(tycCompanyInfo, userName);
            //调用天眼查获取客户单位对外投资信息（处理好的数据，含有对外地区投资和行业投资）
            iCusPersonaAreaInvestService.addInvestByTycCompany(tycCompanyInfo, userName);
            //调用天眼查获取客户单位产品信息
            iCusPersonaCompanyProductService.addProductByTyc(tycCompanyInfo, userName);
            //调用天眼查获取客户单位证书信息
            iCusPersonaCompanyCertService.addCertByTyc(tycCompanyInfo, userName);
            //调用天眼查获取客户单位的客户数据
            iCusPersonaCompanyCustomerService.addCompanyCustomerByTyc(tycCompanyInfo, userName);
            //调用天眼查获取客户单位的供应商数据
            iCusPersonaCompanySupplierService.addSupplierByTyc(tycCompanyInfo, userName);
            //调用天眼查获取客户单位司法解析数据
            iCusPersonaJudicialCaseService.addJudicialCaseByTyc(tycCompanyInfo, userName);
            //调用天眼查获取客户单位开庭公告数据
            iCusPersonaCourtNoticeService.addTycCourtNoticeByCompany(tycCompanyInfo, userName);
            //调用天眼查获取客户单位法律诉讼数据
            iCusPersonaLegalService.addTycLegalByCompany(tycCompanyInfo, userName);
            //调用天眼查获取企业年报信息
            iCusPersonaAnnualReportsService.addAnnualReportsByTyc(tycCompanyInfo, userName);
            //获取招投标数据
            //personaBidsService.savePurchaserPersonaBidsByCompany(company);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
