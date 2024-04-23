package com.ruoyi.web.controller.apiTycController;

import com.ruoyi.components.domain.GovBids;
import com.ruoyi.components.service.IGovBidsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: 11653
 * @createTime: 2024/04/23 17:00
 * @package: com.ruoyi.web.controller.apiTycController
 * @description: 广西政府采购网
 */

@RestController
@RequestMapping("/crawl/purchasing")
public class GXPurchasingNetwork {

    @Resource
    private IGovBidsService idService;

    @PostMapping("/insert")
    public int insertGovBids(@RequestBody GovBids govBids){
        return idService.insertGovBids(govBids);
    }

    @PostMapping("/insertGovCall")
    public int insertGovCallBids(@RequestBody GovBids govBids){
        return idService.insertGovCallBids(govBids);
    }

    @PostMapping("/insertGovPlan")
    public int insertGovPlanBids(@RequestBody GovBids govBids){
        return idService.insertGovPlanBids(govBids);
    }

    @PostMapping("/insertGovPurpose")
    public int insertGovPurposeBids(@RequestBody GovBids govBids){
        return idService.insertGovPurposeBids(govBids);
    }
}
