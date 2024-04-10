package com.ruoyi.components.mapper;

import com.ruoyi.components.domain.CusPersonaCompanyNode;

/**
 * 企业关系图谱节点Mapper接口
 *
 * @author wucilong
 * @date 2022-09-05
 */
public interface CusPersonaCompanyNodeMapper {

    /**
     * 新增企业关系图谱节点
     *
     * @param cusPersonaCompanyNode 企业关系图谱节点
     * @return 结果
     */
    public int insertCusPersonaCompanyNode(CusPersonaCompanyNode cusPersonaCompanyNode);

}
