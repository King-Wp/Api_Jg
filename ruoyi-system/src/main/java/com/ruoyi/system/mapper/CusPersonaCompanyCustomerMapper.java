package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.CusPersonaCompanyCustomer;

/**
 * 企业客户信息Mapper接口
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public interface CusPersonaCompanyCustomerMapper 
{
    /**
     * 新增企业客户信息
     * 
     * @param cusPersonaCompanyCustomer 企业客户信息
     * @return 结果
     */
    public int insertCusPersonaCompanyCustomer(CusPersonaCompanyCustomer cusPersonaCompanyCustomer);

}
