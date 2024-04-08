package com.comp.components.domain.vo;

import lombok.Data;

@Data
public class CustomerBusinessVo {
    //客户单位ID
    private Long id;
    //客户单位全称
    private String company;
    //天眼查公司ID
    private String companyId;
    private String companyName;
    private String sameRatio;

}
