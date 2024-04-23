package com.ruoyi.components.service;

/**
 * 行政处罚Service接口
 * 
 * @author wucilong
 * @date 2023-02-09
 */
public interface ICusPersonaPunishmentAdministrativeService {
    /**
     * 通过天眼查获取企业行政处罚信息（非收费接口）
     * @param companyId 天眼查企业id
     * @param companyName 企业名称
     * @param userName 用户名称
     * @return 插入条数
     */
    int addPunishmentAdministrativeByTyc(String companyId,String companyName, String userName);
}
