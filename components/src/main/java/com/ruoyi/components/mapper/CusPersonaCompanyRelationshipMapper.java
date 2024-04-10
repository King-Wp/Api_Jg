package com.ruoyi.components.mapper;

import com.ruoyi.components.domain.CusPersonaCompanyRelationship;

/**
 * 企业关系图谱Mapper接口
 *
 * @author wucilong
 * @date 2022-09-05
 */
public interface CusPersonaCompanyRelationshipMapper {

    /**
     * 新增企业关系图谱
     *
     * @param cusPersonaCompanyRelationship 企业关系图谱
     * @return 结果
     */
    public int insertCusPersonaCompanyRelationship(CusPersonaCompanyRelationship cusPersonaCompanyRelationship);

}
