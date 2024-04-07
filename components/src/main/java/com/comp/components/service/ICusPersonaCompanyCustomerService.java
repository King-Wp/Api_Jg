package com.comp.components.service;

import com.comp.components.domain.vo.CustomerBusinessVo;

/**
 * 企业客户信息Service接口
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public interface ICusPersonaCompanyCustomerService 
{

    /**
     * 调用天眼查批量新增客户表中企业单位的客户信息
     * @return 添加结果
     */
    public void addCompanyCustomerByTyc(CustomerBusinessVo customerBusinessVo, String userName);

}
