package com.comp.components.service;


import com.comp.components.domain.CustomerPortraitParameter;
import com.comp.components.domain.TyCompany;
import com.comp.components.domain.vo.CustomerBusinessVo;

import java.util.List;

/**
 * @author: 11653
 * @createTime: 2024/03/21 15:04
 * @package: com.ruoyi.system.service
 * @description:
 */
public interface ApiReptileService {

    void addCustomerReportRemind(CustomerPortraitParameter testRequestDTO);


    int addAbnormalOperationByTyc(CustomerBusinessVo customerBusinessVo, String userName);

    List<TyCompany> selectCompanyListByTyc(String keyword, Integer pageNum);

}
