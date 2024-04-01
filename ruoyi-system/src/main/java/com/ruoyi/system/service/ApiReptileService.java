package com.ruoyi.system.service;

import com.ruoyi.system.domain.vo.CustomerBusinessVo;

import java.sql.SQLException;

/**
 * @author: 11653
 * @createTime: 2024/03/21 15:04
 * @package: com.ruoyi.system.service
 * @description:
 */
public interface ApiReptileService {

    void aotoCustomerReportRemind(String company, String companyId, String fullCompany, String userName,String enterpriseType) throws SQLException;


    int addAbnormalOperationByTyc(CustomerBusinessVo customerBusinessVo, String userName);

}
