package com.ruoyi.components.service;

import com.ruoyi.components.domain.vo.CustomerBusinessVo;

/**
 * 公司详情供应商信息Service接口
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public interface ICusPersonaCompanySupplierService 
{

    /**
     * 调用天眼查入库公司供应商信息
     * @return 插入结果
     */
    public void addSupplierByTyc(CustomerBusinessVo customerBusinessVo, String userName);

}
