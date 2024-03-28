package com.ruoyi.system.service;


import com.ruoyi.system.domain.vo.CustomerBusinessVo;

/**
 * 开庭公告Service接口
 * 
 * @author wucilong
 * @date 2022-12-21
 */
public interface ICusPersonaCourtNoticeService 
{

    public void addTycCourtNoticeByCompany(CustomerBusinessVo customerBusinessVo, String userName);
}
