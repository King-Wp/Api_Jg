package com.ruoyi.web.controller.apiTycController;


import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.components.domain.CusPersonaCompanyBusiness;
import com.ruoyi.components.domain.ReceiveParameters.KeyWordsParams;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import com.ruoyi.components.service.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: 11653
 * @createTime: 2024/03/21 14:25
 * @package: com.ruoyi.web.controller.system
 * @description: 爬虫接口 AI接口
 */

@RestController
@RequestMapping("/crawl/reptile")
public class ApiReptileController {

    @Resource
    private IReptileService reptileService;
    @Resource
    private ICusPersonaCompanyBusinessService icusPersonaCompanyBusinessService;
    @Resource
    private ICusPersonaAreaInvestService iCusPersonaAreaInvestService;
    @Resource
    private ICusPersonaCompanyProductService iCusPersonaCompanyProductService;
    @Resource
    private ICusPersonaJudicialCaseService iCusPersonaJudicialCaseService;


    /**
     * 客户洞察关键词获取
     * business 单位经营范围
     * bidTitles 单位名称查询招投标标题金额top10
     * contracts 单位名称查询业绩合同名称
     */
    @PostMapping("/customer/keyWords")
    public String[] keyToLevel(@RequestBody KeyWordsParams keyWordsParams) {
        return reptileService.keyToLevel(keyWordsParams);
    }

    /**
     * 生态关键词
     * baseId 商机ID 没有可以为空
     * business 单位经营范围
     * bidTitles 单位名称查询招投标标题金额top10
     * contracts 单位名称查询业绩合同名称
     * info
     *
     * @param keyWordsParams 参数对象
     */
    @PostMapping("/customer/visitKeyWord")
    public String[] visitKeyWord(@RequestBody KeyWordsParams keyWordsParams) {
        return reptileService.getVisitKeyWord(keyWordsParams);
    }

    /**
     * RtbReportP 商机报备列表
     * baseId 商机id
     *
     * @param keyWordsParams 参数对象
     */
    @PostMapping("/customer/reportKeyWord")
    public JSONObject reportKeyWord(@RequestBody KeyWordsParams keyWordsParams) {
        return reptileService.getReportKeyword(keyWordsParams);
    }

    /**
     * 免费获取工商信息
     *
     * @param companyName 企业名称
     * @param userName    创建用户
     */
    @GetMapping("/addBusinessFreeByTyc")
    public void addBusinessFreeByTyc(@RequestParam("companyName")String companyName, @RequestParam("userName")String userName) {
        reptileService.addBusinessFreeByTyc(companyName, userName);
    }

    /**
     * 获取公司简介
     *
     * @param companyId 企业id
     * @return 简介
     */
    @GetMapping("/introduction")
    public String getCompanyProfile(@RequestParam("companyId")String companyId) {
        return reptileService.getCompanyProfile(companyId);
    }

    /**
     * 客户单位的工商信息
     *
     * @param companyId      企业id
     * @param userName       用户名称
     * @param queryKeyword   查询关键字
     * @param enterpriseType 获取祥云中的企业性质
     * @return 入库结果
     */
    @GetMapping("/addCompanyBusiness")
    public Integer addCompanyBusinessByTycCompanyId(
            @RequestParam("companyId") String companyId, @RequestParam("userName") String userName,
            @RequestParam("queryKeyword") String queryKeyword, @RequestParam("enterpriseType") String enterpriseType
    ) {
        return icusPersonaCompanyBusinessService.addCompanyBusinessByTycCompanyId(companyId, userName, queryKeyword, enterpriseType);
    }

    /**
     * 获取工商信息的logo
     *
     * @param companyId 企业id
     * @return
     */
    @GetMapping("/businessLogo")
    public CusPersonaCompanyBusiness getBusinessLogoByTyc(@RequestParam("companyId")String companyId){
        return icusPersonaCompanyBusinessService.getBusinessLogoByTyc(companyId);
    }

    /**
     * 调用天眼查接口，入库中标单位及客户表的工商信息
     * @param companyName 企业名称
     * @param userName 用户名称
     * @param id 企业id
     * @param enterpriseType 祥云行业
     * @return 结果
     */
    @GetMapping("/cusPersonaBidsBusiness")
    public int CusPersonaBidsBusiness (
            @RequestParam("companyName")String companyName, @RequestParam("userName")String userName,
            @RequestParam("id")String id,@RequestParam("enterpriseType")String enterpriseType
    ){
        return icusPersonaCompanyBusinessService.cusPersonaBidsBusiness(companyName, userName, id, enterpriseType);
    }

    /**
     * 通过天眼查入库客户单位对外投资情况
     * @param companyId 天眼查id
     * @param companyName 公司名称
     * @param userName 用户名称
     * @return 入库条数
     */
    @GetMapping("/investByTycCompany")
    public int investByTycCompany(
            @RequestParam("companyId")String companyId , @RequestParam("companyName")String companyName,
            @RequestParam("userName")String userName){
        return iCusPersonaAreaInvestService.addInvestByTycCompany(companyId, companyName, userName);
    }

    /**
     * 通过天眼查批量入库客户单位对外投资情况
     * @param hasCompanyIdCustomerList 获取通过天眼查查询过的客户公司列表
     * @param userName 用户名称
     * @return 插入条数
     */
    @PostMapping("/batchInvest")
    public int addInvestByTyc(@RequestBody List<CustomerBusinessVo> hasCompanyIdCustomerList, @RequestParam("userName") String userName){
        return iCusPersonaAreaInvestService.addInvestByTyc(hasCompanyIdCustomerList, userName);
    }

    /**
     * 入库客户表中单位的产品信息
     * @param companyId 天眼查企业id
     * @param companyName 企业名称
     * @param userName 用户名称
     */
    @GetMapping("/product")
    public Integer addProductByTyc(@RequestParam("companyId")String companyId,
                                @RequestParam("companyName")String companyName,
                                @RequestParam("userName")String userName){
        return iCusPersonaCompanyProductService.addProductByTyc(companyId,companyName,userName);
    }

    @GetMapping("/judicialCase")
    public AjaxResult addJudicialCase(@RequestParam("companyName") String companyName){
        return iCusPersonaJudicialCaseService.addJudicialCase(companyName);
    }

}
