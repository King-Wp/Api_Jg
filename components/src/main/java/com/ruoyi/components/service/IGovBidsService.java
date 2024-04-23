package com.ruoyi.components.service;

import com.ruoyi.components.domain.GovBids;

/**
 * 招投标数据Service接口
 * 
 * @author Leeyq
 * @date 2022-08-25
 */
public interface IGovBidsService 
{
    /**
     * 新增招投标数据
     * 
     * @param govBids 招投标数据
     * @return 结果
     */
    int insertGovBids(GovBids govBids);

    int insertGovCallBids(GovBids govBids);

    int insertGovPlanBids(GovBids govBids);

	int insertGovPurposeBids(GovBids govBids);
}
