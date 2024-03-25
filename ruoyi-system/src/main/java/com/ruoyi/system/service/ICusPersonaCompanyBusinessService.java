package com.ruoyi.system.service;


import com.ruoyi.system.domain.vo.CustomerBusinessVo;

import java.util.List;

/**
 * 公司详情工商信息Service接口
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public interface ICusPersonaCompanyBusinessService 
{

    /**
     *  客户单位的工商信息
     * @param customerBusinessVo 天眼查对象
     * @param userName 用户名称
     * @param queryKeyword 查询关键字
     * @param enterpriseType 获取祥云中的企业性质
     * @return 入库结果
     */
    public int addCompanyBusinessByTycCompanyId(CustomerBusinessVo customerBusinessVo, String userName, String queryKeyword,String enterpriseType);
}
