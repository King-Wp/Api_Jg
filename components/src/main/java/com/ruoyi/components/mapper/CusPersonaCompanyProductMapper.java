package com.ruoyi.components.mapper;

import com.ruoyi.components.domain.CusPersonaCompanyProduct;

/**
 * 公司详情产品信息Mapper接口
 *
 * @author wucilong
 * @date 2022-09-05
 */
public interface CusPersonaCompanyProductMapper {

    /**
     * 新增公司详情产品信息
     *
     * @param cusPersonaCompanyProduct 公司详情产品信息
     * @return 结果
     */
    int insertCusPersonaCompanyProduct(CusPersonaCompanyProduct cusPersonaCompanyProduct);

}
