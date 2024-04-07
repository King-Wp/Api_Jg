package com.comp.components.service;


import com.comp.components.domain.CustomerPortraitParameter;

import java.sql.SQLException;

/**
 * @author: 11653
 * @createTime: 2024/03/21 15:04
 * @package: com.ruoyi.system.service
 * @description:
 */
public interface ApiReptileService {

    /**
     * 自动客户单位画像
     *
     * company        简称
     * companyId      天眼查ID
     * fullCompany    公司全称
     * userName       用户名称
     * enterpriseType 祥云企业类型
     * @throws SQLException
     */
    void addCustomerReportRemind(CustomerPortraitParameter testRequestDTO);

}
