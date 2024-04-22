package com.ruoyi.components.service;


/**
 * 失信被执行人Service接口
 * 
 * @author wucilong
 * @date 2023-02-08
 */
public interface ICusPersonaDishonestPersonService {
    /**
     * 通过天眼查收费接口获取并入库失信被执行人信息
     */
    Integer addDishonestPersonByTyc(String companyId,String companyName, String userName);
}
