package com.ruoyi.components.service;

import com.ruoyi.components.domain.CusPersonaCompanyBusiness;

import java.util.List;

/**
 * 公司详情工商信息Service接口
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public interface ICusPersonaCompanyBusinessService {

    /**
     *  客户单位的工商信息
     * @param customerBusinessVo 天眼查对象
     * @param userName 用户名称
     * @param queryKeyword 查询关键字
     * @param enterpriseType 获取祥云中的企业性质
     * @return 入库结果
     */
    int addCompanyBusinessByTycCompanyId(String companyId, String userName, String queryKeyword, String enterpriseType);


    /**
     * 通过天眼查获取客户单位基本信息
     * @return 入库结果
     */
    int addCompanyBusinessByTyc(List<String> customers);

    /**
     * 入库中标单位及客户表的工商信息
     * @param companyName 企业名称
     * @param userName 用户名称
     * @param id id
     * @param enterpriseType 祥云行业
     * @return 结果
     */
    int cusPersonaBidsBusiness(String companyName, String userName,String id,String enterpriseType);

    CusPersonaCompanyBusiness getBusinessLogoByTyc(String companyId);

    String getBusinessAliasByTyc(String companyId);

}
