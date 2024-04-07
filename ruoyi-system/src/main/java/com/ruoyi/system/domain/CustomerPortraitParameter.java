package com.ruoyi.system.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class CustomerPortraitParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    //公司简称
    private String company;
    //天眼查ID
    private String companyId;
    //天眼查全称
    private String fullCompany;
    //用户名
    private String userName;
    //企业类型
    private String enterpriseType;
}