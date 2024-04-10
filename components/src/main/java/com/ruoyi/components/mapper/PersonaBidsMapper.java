package com.ruoyi.components.mapper;

import com.ruoyi.components.domain.PersonaBids;

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
}
