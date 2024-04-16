package com.ruoyi.components.domain;

import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class CustomerPortraitParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    //公司简称
    private String company;
    //用户名
    private String userName;
    //企业类型
    private String enterpriseType;
    private CustomerBusinessVo tycCompanyInfo;
}