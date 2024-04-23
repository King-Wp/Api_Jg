package com.ruoyi.components.mapper;

import com.ruoyi.components.domain.CusPersonaJudicialCase;

import java.util.List;

/**
 * 客户画像司法解析Mapper接口
 * 
 * @author wucilong
 * @date 2022-09-08
 */
public interface CusPersonaJudicialCaseMapper 
{

    /**
     * 新增客户画像司法解析
     * 
     * @param cusPersonaJudicialCase 客户画像司法解析
     * @return 结果
     */
    int insertCusPersonaJudicialCase(CusPersonaJudicialCase cusPersonaJudicialCase);

    int saveBatchJudicialCase(List<CusPersonaJudicialCase> list);

}
