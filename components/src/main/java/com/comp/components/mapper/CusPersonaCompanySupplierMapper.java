package com.comp.components.mapper;

import com.comp.components.domain.CusPersonaCompanySupplier;

/**
 * 公司详情供应商信息Mapper接口
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public interface CusPersonaCompanySupplierMapper 
{
    /**
     * 新增公司详情供应商信息
     * 
     * @param cusPersonaCompanySupplier 公司详情供应商信息
     * @return 结果
     */
    public int insertCusPersonaCompanySupplier(CusPersonaCompanySupplier cusPersonaCompanySupplier);
}
