package com.ruoyi.components.service;

/**
 * 企业证书信息Service接口
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public interface ICusPersonaCompanyCertService 
{

    /**
     * 通过天眼查新增客户表中的企业证书信息
     * @return
     */
    Integer addCertByTyc(String companyId, String companyName, String userName);

}
