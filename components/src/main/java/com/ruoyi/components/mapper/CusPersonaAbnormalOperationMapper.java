package com.ruoyi.components.mapper;

import com.ruoyi.components.domain.CusPersonaAbnormalOperation;

/**
 * 经营异常信息Mapper接口
 * 
 * @author persona
 * @date 2023-02-08
 */
public interface CusPersonaAbnormalOperationMapper {
    /**
     * 新增经营异常信息
     * 
     * @param cusPersonaAbnormalOperation 经营异常信息
     * @return 结果
     */
    public int insertCusPersonaAbnormalOperation(CusPersonaAbnormalOperation cusPersonaAbnormalOperation);
}
