package com.ruoyi.web.controller.APISD;

import com.ruoyi.system.service.ApiReptileService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: 11653
 * @createTime: 2024/03/21 14:25
 * @package: com.ruoyi.web.controller.system
 * @description:
 */

@RestController
@RequestMapping("/tianyan/reptile")
public class ApiReptileController {

    @Resource
    private ApiReptileService apiReptileService;

    @GetMapping("/addCustomerPortrait")
    public String addCustomerPortrait(CustomerPortraitParameter testRequestDTO){
        apiReptileService.addCustomerReportRemind(testRequestDTO);
        return "测试成功";
    }

}
