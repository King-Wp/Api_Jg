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

    /**
     * 关键词查询-第三方单位全称查询接口(天眼查) http://open.api.tianyancha
     * @param keyword 关键字
     * @param pageNum 页数
     * @return 返回 列表
     */
    List<TyCompany> selectCompanyListByTyc(String keyword, Integer pageNum);

}
