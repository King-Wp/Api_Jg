package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.TestRequestDTO;
import com.ruoyi.system.domain.vo.CustomerBusinessVo;
import com.ruoyi.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;

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
     * @param company        简称
     * @param companyId      天眼查ID
     * @param fullCompany    公司全称
     * @param userName       用户名称
     * @param enterpriseType 祥云企业类型
     * @throws SQLException
     */
    @Override
    public void aotoCustomerReportRemind(TestRequestDTO testRequestDTO){
        try {
            //更新客户单位画像信息
            CustomerBusinessVo tycCompanyInfo = new CustomerBusinessVo();
            tycCompanyInfo.setCompany(testRequestDTO.getFullCompany());
            tycCompanyInfo.setCompanyId(testRequestDTO.getCompanyId());

            //获取客户单位的工商信息
            iCusPersonaCompanyBusinessService.addCompanyBusinessByTycCompanyId(tycCompanyInfo, testRequestDTO.getUserName(), testRequestDTO.getCompany(), testRequestDTO.getEnterpriseType());
            //调用天眼查获取客户单位关系信息
            iCusPersonaCompanyNodeService.addCorporateRelationsByTyc(tycCompanyInfo, testRequestDTO.getUserName());
            //调用天眼查获取客户单位对外投资信息（处理好的数据，含有对外地区投资和行业投资）
            iCusPersonaAreaInvestService.addInvestByTycCompany(tycCompanyInfo, testRequestDTO.getUserName());
            //调用天眼查获取客户单位产品信息
            iCusPersonaCompanyProductService.addProductByTyc(tycCompanyInfo, testRequestDTO.getUserName());
            //调用天眼查获取客户单位证书信息
            iCusPersonaCompanyCertService.addCertByTyc(tycCompanyInfo, testRequestDTO.getUserName());
            //调用天眼查获取客户单位的客户数据
            iCusPersonaCompanyCustomerService.addCompanyCustomerByTyc(tycCompanyInfo, testRequestDTO.getUserName());
            //调用天眼查获取客户单位的供应商数据
            iCusPersonaCompanySupplierService.addSupplierByTyc(tycCompanyInfo, testRequestDTO.getUserName());
            //调用天眼查获取客户单位司法解析数据
            iCusPersonaJudicialCaseService.addJudicialCaseByTyc(tycCompanyInfo, testRequestDTO.getUserName());
            //调用天眼查获取客户单位开庭公告数据
            iCusPersonaCourtNoticeService.addTycCourtNoticeByCompany(tycCompanyInfo, testRequestDTO.getUserName());
            //调用天眼查获取客户单位法律诉讼数据
            iCusPersonaLegalService.addTycLegalByCompany(tycCompanyInfo, testRequestDTO.getUserName());
            //调用天眼查获取企业年报信息
            iCusPersonaAnnualReportsService.addAnnualReportsByTyc(tycCompanyInfo, testRequestDTO.getUserName());
            //获取招投标数据
            iPersonaBidsService.savePurchaserPersonaBidsByCompany(testRequestDTO.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int addAbnormalOperationByTyc(CustomerBusinessVo customerBusinessVo, String userName) {
        return 0;
    }
}
