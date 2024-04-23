package com.ruoyi.components.service;

import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 客户画像司法解析Service接口
 * 
 * @author wucilong
 * @date 2022-09-08
 */
public interface ICusPersonaJudicialCaseService 
{
    /**
     * 调用天眼查接口，将司法解析数据入库
     *
     * @param customerBusinessVo company 客户单位名称 companyId 天眼查公司ID
     */
    Integer addJudicialCaseByTyc(String companyId,String companyName, String userName);

    /**
     * 调用天眼查接口，将司法解析数据入库
     *
     * @param cusPersonaJudicialCase 客户公司名称
     * @return 结果
     */
    AjaxResult addJudicialCase(String companyName);

}
