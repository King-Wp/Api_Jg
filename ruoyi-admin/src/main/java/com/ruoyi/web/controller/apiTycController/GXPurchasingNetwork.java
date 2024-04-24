package com.ruoyi.web.controller.apiTycController;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.components.domain.GovBids;
import com.ruoyi.components.domain.PersonaBids;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import com.ruoyi.components.service.IGovBidsService;
import com.ruoyi.components.service.IPersonaBidsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

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
    @Resource
    private IPersonaBidsService ipersonaBidsService;

    @PostMapping("/insert")
    public int insertGovBids(@RequestBody GovBids govBids) {
        return idService.insertGovBids(govBids);
    }

    @PostMapping("/insertGovCall")
    public int insertGovCallBids(@RequestBody GovBids govBids) {
        return idService.insertGovCallBids(govBids);
    }

    @PostMapping("/insertGovPlan")
    public int insertGovPlanBids(@RequestBody GovBids govBids) {
        return idService.insertGovPlanBids(govBids);
    }

    @PostMapping("/insertGovPurpose")
    public int insertGovPurposeBids(@RequestBody GovBids govBids) {
        return idService.insertGovPurposeBids(govBids);
    }

    @PostMapping("/insertPersona")
    public AjaxResult insertPersonaGovBids(@RequestBody PersonaBids personaBids) throws UnsupportedEncodingException {
        return ipersonaBidsService.insertPersonaGovBids(personaBids);
    }

    @PostMapping("/personaGovTitle")
    public AjaxResult insertPersonaGovTitleCallBids(@RequestBody PersonaBids personaBids) {
        return ipersonaBidsService.insertPersonaGovTitleCallBids(personaBids);
    }

    @PostMapping("/govTitleWin")
    public AjaxResult insertPersonaGovTitleWinBids(@RequestBody PersonaBids personaBids) {
        return ipersonaBidsService.insertPersonaGovTitleWinBids(personaBids);
    }

    @PostMapping("/contentCall")
    public AjaxResult insertPersonaGovContentCallBids(@RequestBody PersonaBids personaBids) {
        return ipersonaBidsService.insertPersonaGovContentCallBids(personaBids);
    }

    @PostMapping("/contentWin")
    public AjaxResult insertPersonaGovContentWinBids(@RequestBody PersonaBids personaBids) {
        return ipersonaBidsService.insertPersonaGovContentWinBids(personaBids);
    }

    /**
     * 多线程爬取广西公共资源交易平台的招投标中标数据
     *
     * @return
     */
    @GetMapping("/multithreading")
    public int addGxPublicResourcesByMultithreading(@RequestParam("starPage") int starPage,@RequestParam("endPage") int endPage) {
        return ipersonaBidsService.addGxPublicResourcesByMultithreading(starPage,endPage);
    }


    /**
     * 通过多线程爬取广西公共资源交易平台的招投标招标数据
     *
     * @return
     */
    @GetMapping("/resourcesBiding")
    public AjaxResult addGxPublicResourcesBiding() {
        return ipersonaBidsService.addGxPublicResourcesBiding();
    }


    /**
     * 爬取一条广西公共资源交易平台的招投标中标数据
     */
    @GetMapping("/gXPublicResources")
    public void addOneGXPublicResources() {
        ipersonaBidsService.addOneGXPublicResources();
    }

    /**
     * 批量新增全国公共资源交易平台（广西壮族自治区）招标、中标数据
     * @param equal 查询条件
     */
    @GetMapping("/gxggzyBids")
    public void insertGxggzyBids(@RequestParam("equal")String equal) {
        ipersonaBidsService.insertGxggzyBids(equal);
    }

    @PostMapping("/bids")
    public void addBidsByTyc(@RequestBody CustomerBusinessVo customerBusinessVo) {
        ipersonaBidsService.addBidsByTyc(customerBusinessVo);
    }
}
