package com.ruoyi.components.mapper;

import com.ruoyi.components.domain.PersonaBids;

import java.util.List;

/**
 * 招投标数据Mapper接口
 * 
 * @author Leeyq
 * @date 2022-08-25
 */
public interface PersonaBidsMapper 
{

    /**
     * 新增招投标数据
     * 
     * @param personaBids 招投标数据
     * @return 结果
     */
    public int insertPersonaBids(PersonaBids personaBids);

    /**
     * 批量新增招投标中标数据
     * @param personaBidsList 招投标中标数据列表
     * @return 新增结果
     */
    public int saveBatchPersonaBids(List<PersonaBids> personaBidsList);
}
