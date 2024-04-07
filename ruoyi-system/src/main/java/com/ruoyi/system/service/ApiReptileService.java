package com.ruoyi.system.service;

import com.ruoyi.system.domain.CustomerPortraitParameter;
import com.ruoyi.system.domain.vo.CustomerBusinessVo;

/**
 * @author: 11653
 * @createTime: 2024/03/21 15:04
 * @package: com.ruoyi.system.service
 * @description:
 */
public interface ApiReptileService {

    void aotoCustomerReportRemind(CustomerPortraitParameter testRequestDTO);


    int addAbnormalOperationByTyc(CustomerBusinessVo customerBusinessVo, String userName);

}
