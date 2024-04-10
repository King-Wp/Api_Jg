package com.ruoyi.web.controller.APISD;


import com.ruoyi.components.domain.CustomerPortraitParameter;
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
@RequestMapping("/tianyan/reptile2")
public class ApiReptileController {

    @Resource
    private IReptileService reptileService;

    @GetMapping("/addCustomerPortrait")
    public String addCustomerPortrait(CustomerPortraitParameter testRequestDTO){
        reptileService.addCustomerReportRemind(testRequestDTO);
        return "测试成功";
    }

}
