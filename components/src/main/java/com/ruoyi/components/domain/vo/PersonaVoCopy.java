package com.ruoyi.components.domain.vo;

import lombok.Data;

@Data
public class PersonaVoCopy {
    /**
     * 客户主键
     */
    private Long id;



    /**
     * 客户名称
     */
    private String name;

    /**
     * 公司
     */
    private String company;

//    /**
//     * 职务
//     */
//    private String post;
//
//    /**
//     * 电话
//     */
//    private String phone;

    /**
     * 地址
     */
    private String regLocation;

    /**
     * 客户来源
     */
//    private Integer source;


    /**
     * 客户类型
     */
    private String companyOrgType;

    /**
     * 经营范围
     */
    private String businessScope;

    /** 是否关注(0未关注,1重点关注) */
    private String isAttent;

    /**
     * 记录人
     */
    private String userName;

    /**
     * 记录分公司
     */
    private String deptName;

    /**
     * 备注
     */
    private String remark;

    //天眼查公司ID
    private String companyId;

    //客户行业类型
    private String industryType;

    //天眼查获取的全称
    private String companyName;

    //合同金额
    private String amount;
}
