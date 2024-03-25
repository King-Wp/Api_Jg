package com.ruoyi.system.service;

import com.ruoyi.system.domain.vo.CustomerBusinessVo;

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
    public void addCertByTyc(CustomerBusinessVo customerBusinessVo, String userName);

}
