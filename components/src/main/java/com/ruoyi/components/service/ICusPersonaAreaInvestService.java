package com.ruoyi.components.service;

/**
 * areaInvestService接口
 *
 * @author Lgy
 * @date 2022-11-04
 */
public interface ICusPersonaAreaInvestService {

    /**
     * 通过天眼查入库客户单位对外投资情况
     * @param companyId 天眼查id
     * @param companyName 公司名称
     * @param userName 用户名称
     */

    int addInvestByTycCompany(String companyId , String companyName, String userName);
}
