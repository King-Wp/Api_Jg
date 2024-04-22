package com.ruoyi.components.service;

/**
 * 公司详情产品信息Service接口
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public interface ICusPersonaCompanyProductService 
{

    /**
     * 入库客户表中单位的产品信息
     * @return 插入结果
     */
    Integer addProductByTyc(String companyId, String companyName,String userName);
}
