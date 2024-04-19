package com.ruoyi.web.controller.apiTycController;

import com.ruoyi.components.domain.TyCompany;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import com.ruoyi.components.service.ApiThirdPartyService;
import com.ruoyi.components.service.ICusPersonaAnnualReportsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: 11653
 * @createTime: 2024/04/15 10:29
 * @package: com.ruoyi.web.controller.apiTycController
 * @description: 付费API 远程开放接口
 */

@RestController
@RequestMapping("/crawl/paidAPI")
public class ThirdPartyPaymentInterface {

    @Resource
    private ApiThirdPartyService apiThirdPartyService;
    @Resource
    private ICusPersonaAnnualReportsService iCusPersonaAnnualReportsService;

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
}
