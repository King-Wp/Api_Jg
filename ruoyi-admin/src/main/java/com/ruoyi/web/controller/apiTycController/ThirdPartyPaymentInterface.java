package com.ruoyi.web.controller.apiTycController;

import com.ruoyi.components.domain.TyCompany;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import com.ruoyi.components.service.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: 11653
 * @createTime: 2024/04/15 10:29
 * @package: com.ruoyi.web.controller.apiTycController
 * @description: 付费API、官方API接口
 */

@RestController
@RequestMapping("/crawl/paidAPI")
public class ThirdPartyPaymentInterface {

    @Resource
    private ApiThirdPartyService apiThirdPartyService;
    @Resource
    private ICusPersonaAnnualReportsService iCusPersonaAnnualReportsService;
    @Resource
    private ICusPersonaCompanyCertService iCusPersonaCompanyCertService;
    @Resource
    private ICusPersonaCompanyCustomerService iCusPersonaCompanyCustomerService;
    @Resource
    private ICusPersonaCompanyNodeService iCusPersonaCompanyNodeService;
    @Resource
    private ICusPersonaCompanySupplierService iCusPersonaCompanySupplierService;
    @Resource
    private ICusPersonaDishonestPersonService iCusPersonaDishonestPersonService;
    @Resource
    private ICusPersonaJudicialCaseService iCusPersonaJudicialCaseService;


    /**
     * 关键字查询公司名称列表
     * @param keyword 关键字
     * @param pageNum 页数
     * @return 每页返回对多20条数据
     */
    @GetMapping("/companyList")
    public List<TyCompany> companyList(@RequestParam("keyword")String keyword, @RequestParam("pageNum")Integer pageNum) {
        return apiThirdPartyService.selectCompanyListByTyc(keyword, pageNum);
    }

    /**
     * 获取并入库企业经营异常信息
     * @param customerBusinessVo Url参数
     * @return 添加条数
     */
    @PostMapping("/businessExInfo")
    public int businessExInfo(@RequestBody CustomerBusinessVo customerBusinessVo) {
        return apiThirdPartyService.addAbnormalOperationByTyc(customerBusinessVo);
    }

    /**
     * 企业动产抵押信息
     * @param customerBusinessVo Url参数
     * @return 添加条数
     */
    @PostMapping("/chattelMortgage")
    public int chattelMortgage(@RequestBody CustomerBusinessVo customerBusinessVo) {
        return apiThirdPartyService.addChattelMortgageByTyc(customerBusinessVo);
    }

    /**
     * 客户单位基本信息
     * @param customers Url参数
     * @return 添加条数
     */
    @PostMapping("/business")
    public int business(@RequestBody List<String> customers) {
        return apiThirdPartyService.addCompanyBusinessByTyc(customers);
    }

    /**
     * 调用天眼查获取企业年报信息
     * @param companyId 企业id
     * @param companyName 企业名称
     * @param userName 用户名称
     */
    @GetMapping("/annualReports")
    public int addAnnualReportsByTyc(@RequestParam("companyId")String companyId ,
                                      @RequestParam("companyName")String companyName,
                                      @RequestParam("userName")String userName){
        return iCusPersonaAnnualReportsService.addAnnualReportsByTyc(companyId,companyName,userName);
    }

    /**
     * 新增客户表中的企业证书信息
     * @param companyId 天眼查企业id
     * @param companyName 企业名称
     * @param userName 用户名称
     * @return 新增条数
     */
    @GetMapping("/addCertByTyc")
    public Integer addCertByTyc(@RequestParam("companyId")String companyId,
                                @RequestParam("companyName")String companyName,
                                @RequestParam("userName")String userName){
        return iCusPersonaCompanyCertService.addCertByTyc(companyId,companyName,userName);
    }

    /**
     * 批量新增客户表中企业单位的客户信息
     * @param companyId 天眼查企业id
     * @param companyName 企业名称
     * @param userName 用户名称
     * @return 新增条数
     */
    @GetMapping("/companyCustomer")
    public Integer addCompanyCustomerByTyc(@RequestParam("companyId")String companyId,
                                @RequestParam("companyName")String companyName,
                                @RequestParam("userName")String userName){
        return iCusPersonaCompanyCustomerService.addCompanyCustomerByTyc(companyId,companyName,userName);
    }

    /**
     * 根据公司名称通过天眼查入库企业关系图谱
     * @param companyId 天眼查企业id
     * @param companyName 企业名称
     * @param userName 用户名称
     * @return 插入条数
     */
    @GetMapping("/corporateRelations")
    public Integer addCorporateRelationsByTyc(@RequestParam("companyId")String companyId,
                                       @RequestParam("companyName")String companyName,
                                       @RequestParam("userName")String userName){
        return iCusPersonaCompanyNodeService.addCorporateRelationsByTyc(companyId,companyName,userName);
    }

    /**
     * 通过天眼查入库企业关系图谱 (批量)
     * @param customerList 获取通过天眼查查询过的客户公司列表（可级联获取天眼查companyId）
     * @param userName 用户名
     * @return 插入条数
     */
    @PostMapping("/corporateRelations")
    public Integer addCorporateRelationsByTyc(@RequestBody List<CustomerBusinessVo> customerList ,
                                       @RequestParam("userName") String userName){
        return iCusPersonaCompanyNodeService.addCorporateRelationsByTyc(customerList,userName);
    }

    /**
     * 入库客户表中单位的供应商信息
     * @param companyId 天眼查ID
     * @param companyName 企业名称
     * @param userName 用户名称
     * @return 新增条数
     */
    @GetMapping("/supplier")
    public Integer addSupplierByTyc(@RequestParam("companyId")String companyId,
                                    @RequestParam("companyName")String companyName,
                                    @RequestParam("userName")String userName){
        return iCusPersonaCompanySupplierService.addSupplierByTyc(companyId, companyName, userName);
    }

    /**
     * 入库客户表中单位的供应商信息
     * @param companyId 天眼查ID
     * @param companyName 企业名称
     * @param userName 用户名称
     * @return 新增条数
     */
    @GetMapping("/courtNotice")
    public Integer addTycCourtNoticeByCompany(@RequestParam("companyId")String companyId,
                                    @RequestParam("companyName")String companyName,
                                    @RequestParam("userName")String userName){
        return iCusPersonaCompanySupplierService.addSupplierByTyc(companyId, companyName, userName);
    }

    /**
     * 入库失信被执行人信息
     * @param companyId 天眼查ID
     * @param companyName 企业名称
     * @param userName 用户名称
     * @return 新增条数
     */
    @GetMapping("/dishonestPerson")
    public Integer addDishonestPersonByTyc(@RequestParam("companyId")String companyId,
                                           @RequestParam("companyName")String companyName,
                                           @RequestParam("userName")String userName){
        return iCusPersonaDishonestPersonService.addDishonestPersonByTyc(companyId, companyName, userName);
    }

    /**
     * 将司法解析数据入库
     * @param companyId 天眼查ID
     * @param companyName 企业名称
     * @param userName 用户名称
     * @return 新增条数
     */

    @GetMapping("/judicialCase")
    public Integer addJudicialCaseByTyc(@RequestParam("companyId")String companyId,
                                        @RequestParam("companyName")String companyName,
                                        @RequestParam("userName")String userName){
        return iCusPersonaJudicialCaseService.addJudicialCaseByTyc(companyId, companyName, userName);
    }

}
