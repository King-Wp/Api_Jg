package com.ruoyi.components.mapper;

import com.ruoyi.components.domain.CusPersonaDishonestPerson;
import org.apache.ibatis.annotations.Mapper;

/**
 * 失信被执行人Mapper接口
 * 
 * @author wucilong
 * @date 2023-02-08
 */

@Mapper
public interface CusPersonaDishonestPersonMapper {

    /**
     * 新增失信被执行人
     * 
     * @param cusPersonaDishonestPerson 失信被执行人
     * @return 结果
     */
    public int insertCusPersonaDishonestPerson(CusPersonaDishonestPerson cusPersonaDishonestPerson);

}
