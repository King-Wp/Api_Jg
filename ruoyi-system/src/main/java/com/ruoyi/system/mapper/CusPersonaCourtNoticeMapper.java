package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.CusPersonaCourtNotice;

/**
 * 开庭公告Mapper接口
 * 
 * @author wucilong
 * @date 2022-12-21
 */
public interface CusPersonaCourtNoticeMapper 
{

    /**
     * 新增开庭公告
     * 
     * @param cusPersonaCourtNotice 开庭公告
     * @return 结果
     */
    public int insertCusPersonaCourtNotice(CusPersonaCourtNotice cusPersonaCourtNotice);
}
