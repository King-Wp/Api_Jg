package com.ruoyi.components.service;

import com.ruoyi.components.domain.vo.CustomerBusinessVo;

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
    public void addJudicialCaseByTyc(CustomerBusinessVo customerBusinessVo, String userName);

}
