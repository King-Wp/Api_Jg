package com.ruoyi.web.controller.apiTycController;


import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.components.domain.ReceiveParameters.KeyWordsParams;
import com.ruoyi.components.service.ICusPersonaCompanyBusinessService;
import com.ruoyi.components.service.IReptileService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    private ICusPersonaCompanyBusinessService iPersonaCompanyBusinessService;

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
        return iPersonaCompanyBusinessService.addCompanyBusinessByTycCompanyId(companyId, userName, queryKeyword, enterpriseType);
    }


}
