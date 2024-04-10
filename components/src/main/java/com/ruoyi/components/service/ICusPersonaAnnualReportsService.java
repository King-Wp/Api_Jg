package com.ruoyi.components.service;

import com.ruoyi.components.domain.vo.CustomerBusinessVo;

/**
 * 企业年报Service接口
 *
 * @author wucilong
 * @date 2022-12-29
 */
public interface ICusPersonaAnnualReportsService {
    public void addAnnualReportsByTyc(CustomerBusinessVo customerBusinessVo, String userName);
}
