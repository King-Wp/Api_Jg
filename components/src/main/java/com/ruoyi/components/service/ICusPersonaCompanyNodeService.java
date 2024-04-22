package com.ruoyi.components.service;

import com.ruoyi.components.domain.vo.CustomerBusinessVo;

import java.util.List;

/**
 * @author: 11653
 * @createTime: 2024/03/25 9:08
 * @package: com.ruoyi.system.service
 * @description:
 */
public interface ICusPersonaCompanyNodeService {

    /**
     * 根据公司名称通过天眼查入库企业关系图谱
     * @return 入库结果
     */
    Integer addCorporateRelationsByTyc(String companyId,String companyName, String userName);

    /**
     * 通过天眼查入库企业关系图谱 (批量)
     * @return
     */
    Integer addCorporateRelationsByTyc(List<CustomerBusinessVo> customerList , String userName);

}
