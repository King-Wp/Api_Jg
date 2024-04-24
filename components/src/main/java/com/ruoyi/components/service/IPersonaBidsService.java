package com.ruoyi.components.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.components.domain.PersonaBids;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 招投标数据Service接口
 * 
 * @author Leeyq
 * @date 2022-08-25
 */
public interface IPersonaBidsService 
{

    /**
     * 新增招投标数据
     * 
     * @param personaBids 招投标数据
     * @return 结果
     */
    AjaxResult insertPersonaBids(PersonaBids personaBids);

    /**
     * 新增客户列表中的招投标数据
     *
     * @param customersStringList 客户单位列表
     * @return 结果
     */
    int insertCustomerPersonaBids(List<String> customersStringList);

    AjaxResult insertPersonaGovBids(PersonaBids personaBids) throws UnsupportedEncodingException;

    AjaxResult insertPersonaGovTitleCallBids(PersonaBids personaBids);

    AjaxResult insertPersonaGovTitleWinBids(PersonaBids personaBids);

    AjaxResult insertPersonaGovContentCallBids(PersonaBids personaBids);

    AjaxResult insertPersonaGovContentWinBids(PersonaBids personaBids);

    /**
     * 多线程爬取广西公共资源交易平台的招投标中标数据
     * @return
     */
    int addGxPublicResourcesByMultithreading(int starPage, int endPage);

    /**
     * 通过多线程爬取广西公共资源交易平台的招投标招标数据
     * @return
     */
    AjaxResult addGxPublicResourcesBiding();

    /**
     * 爬取一条广西公共资源交易平台的招投标中标数据
     */
    void addOneGXPublicResources();

    /**
     * 批量新增全国公共资源交易平台（广西壮族自治区）招标、中标数据
     * @param equal 查询条件
     */
    void insertGxggzyBids(String equal);

    void addBidsByTyc(CustomerBusinessVo customerBusinessVo);

    int savePurchaserPersonaBids(List<String> customerList);

    int savePurchaserPersonaBidsByCompany(String company);

    int upPurchaserPersonaBidsByCompany(String keyword,String publishStartTime);
}
