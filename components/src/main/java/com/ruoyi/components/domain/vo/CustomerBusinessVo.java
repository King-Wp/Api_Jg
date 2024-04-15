package com.ruoyi.components.domain.vo;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class CustomerBusinessVo extends BaseEntity {
    //客户单位ID
    private Long id;
    //客户单位全称
    private String company;
    //天眼查公司ID
    private String companyId;
    private String companyName;
    private String sameRatio;

}
