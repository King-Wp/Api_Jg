package com.ruoyi.web.controller.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 11653
 * @createTime: 2024/03/21 14:25
 * @package: com.ruoyi.web.controller.system
 * @description:
 */

@RestController
@RequestMapping("/tianyan/reptile")
public class ApiReptileController {


    @GetMapping("/test")
    public void test(){
        System.out.println("test");
    }

}
