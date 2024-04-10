package com.ruoyi.components.mapper;

import com.ruoyi.components.domain.TyCompany;

/**
 * 天眼查企业Mapper接口
 * 
 * @author Leeyq
 * @date 2022-07-13
 */
public interface TyCompanyMapper {


    /**
     * 新增天眼查企业
     * 
     * @param tyCompany 天眼查企业
     * @return 结果
     */
    public int insertTyCompany(TyCompany tyCompany);
}
