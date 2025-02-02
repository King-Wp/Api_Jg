package com.ruoyi.components.service;

import com.ruoyi.components.domain.CusPersonaCompanyBusiness;
import com.ruoyi.components.domain.TyCompany;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;

import java.util.List;

/**
 * @author: 11653
 * @createTime: 2024/04/07 16:57
 * @package: com.comp.components.service
 * @description: 付费接口
 */
public interface ApiThirdPartyService {

    /**
     * 关键词查询-第三方单位全称查询接口(天眼查) http://open.api.tianyancha
     * @param keyword 关键字
     * @param pageNum 页数
     * @return 返回 列表
     */
    List<TyCompany> selectCompanyListByTyc(String keyword, Integer pageNum);

    /**
     * 调用天眼查收费接口获取并入库企业经营异常信息
     */
    int addAbnormalOperationByTyc(CustomerBusinessVo customerBusinessVo);


    /**
     * 通过天眼查收费接口调用企业动产抵押信息
     */
    int addChattelMortgageByTyc(CustomerBusinessVo customerBusinessVo);

    /**
     * 通过天眼查获取客户单位基本信息
     * @param customers 客户单位列表
     * @return 入库结果
     */
    int addCompanyBusinessByTyc(List<String> customers);

    /**
     *  获取客户单位的工商信息
     * @param companyId 企业id
     * @param companyName 企业名称
     * @param userName 用户名
     * @param queryKeyword 查询关键词
     * @param enterpriseType 祥云字段
     * @return 工商信息对象
     */
    CusPersonaCompanyBusiness getNewBusinessInformation(String companyId , String companyName,
                                         String userName, String queryKeyword,String enterpriseType);

}
