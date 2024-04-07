package com.comp.components.mapper;

import com.comp.components.domain.CusPersonaLegal;

/**
 * 司法解析Mapper接口
 * 
 * @author wucilong
 * @date 2022-12-21
 */
public interface CusPersonaLegalMapper 
{

    /**
     * 新增司法解析
     * 
     * @param cusPersonaLegal 司法解析
     * @return 结果
     */
    public int insertCusPersonaLegal(CusPersonaLegal cusPersonaLegal);
}
