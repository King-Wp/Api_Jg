package com.comp.components.service;

import com.comp.components.domain.vo.CustomerBusinessVo;

/**
 * 司法解析Service接口
 *
 * @author wucilong
 * @date 2022-12-21
 */
public interface ICusPersonaLegalService {


    /**
     * 入库天眼查法律诉讼信息
     *
     * @param customerBusinessVo 天眼查公司ID
     * @return 插入结果
     */
    public void addTycLegalByCompany(CustomerBusinessVo customerBusinessVo, String userName);
}
