package com.ruoyi.system.service;


import com.ruoyi.system.domain.vo.CustomerBusinessVo;

/**
 * areaInvestService接口
 *
 * @author wucilong
 * @date 2022-11-04
 */
public interface ICusPersonaAreaInvestService {
    public void addInvestByTycCompany(CustomerBusinessVo customerBusinessVo, String userName);
}
