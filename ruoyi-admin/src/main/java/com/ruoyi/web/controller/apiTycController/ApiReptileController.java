package com.ruoyi.web.controller.apiTycController;


import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.components.domain.CustomerPortraitParameter;
import com.ruoyi.components.domain.ReceiveParameters.KeyWordsParams;
import com.ruoyi.components.service.IReptileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: 11653
 * @createTime: 2024/03/21 14:25
 * @package: com.ruoyi.web.controller.system
 * @description:
 */

@RestController
@RequestMapping("/crawl/reptile")
public class ApiReptileController {

    @Resource
    private IReptileService reptileService;

    /**
     * 新增客户画像
     * @param customerPortraitParameter 传递参数
     * @return
     */
    @GetMapping("/addCustomerPortrait")
    public AjaxResult addCustomerPortrait(CustomerPortraitParameter customerPortraitParameter){
        reptileService.addCustomerReportRemind(customerPortraitParameter);
        return AjaxResult.success();
    }

    /**
     * 客户洞察关键词获取
     * business 单位经营范围
     * bidTitles 单位名称查询招投标标题金额top10
     * contracts 单位名称查询业绩合同名称
     * @return
     */
    @GetMapping("/customer/keyWords")
    public String[] keyToLevel(KeyWordsParams keyWordsParams){
        return reptileService.keyToLevel(keyWordsParams);
    }

    /**
     * 生态关键词
     * baseId 商机ID 没有可以为空
     * business 单位经营范围
     * bidTitles 单位名称查询招投标标题金额top10
     * contracts 单位名称查询业绩合同名称
     * info
     * @param keyWordsParams 参数对象
     * @return
     */
    @GetMapping("/customer/visitKeyWord")
    public String[] visitKeyWord(KeyWordsParams keyWordsParams){
        return reptileService.getVisitKeyWord(keyWordsParams);
    }

    /**
     * RtbReportP 商机报备列表
     * baseId 商机id
     * @param keyWordsParams 参数对象
     * @return
     */
    @GetMapping("/customer/reportKeyWord")
    public JSONObject reportKeyWord(KeyWordsParams keyWordsParams){
        return reptileService.getReportKeyword(keyWordsParams);
    }

}
