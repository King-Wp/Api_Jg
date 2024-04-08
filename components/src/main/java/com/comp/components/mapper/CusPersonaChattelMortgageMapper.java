package com.comp.components.mapper;

import com.comp.components.domain.CusPersonaChattelMortgage;

/**
 * 动产抵押信息Mapper接口
 * 
 * @author wucilong
 * @date 2023-02-08
 */
public interface CusPersonaChattelMortgageMapper {
    /**
     * 新增动产抵押信息
     * 
     * @param cusPersonaChattelMortgage 动产抵押信息
     * @return 结果
     */
    public int insertCusPersonaChattelMortgage(CusPersonaChattelMortgage cusPersonaChattelMortgage);
}
