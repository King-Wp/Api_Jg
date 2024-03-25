package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.CusPersonaCompanyCert;

/**
 * 企业证书信息Mapper接口
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public interface CusPersonaCompanyCertMapper 
{
    /**
     * 新增企业证书信息
     * 
     * @param cusPersonaCompanyCert 企业证书信息
     * @return 结果
     */
    public int insertCusPersonaCompanyCert(CusPersonaCompanyCert cusPersonaCompanyCert);
}
