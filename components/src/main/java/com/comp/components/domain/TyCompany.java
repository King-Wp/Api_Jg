package com.comp.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 天眼查企业对象 rtb_tyc_company
 *
 * @author Leeyq
 * @date 2022-07-13
 */
@Data
public class TyCompany
{
    private static final long serialVersionUID = 1L;

    /** 公司id */
    private Long id;

    /** 公司id */
    private Long companyId;

    /** 公司名 */
    @Excel(name = "公司名")
    private String name;

    /** 统一社会信用代码 */
    @Excel(name = "统一社会信用代码")
    private String creditcode;

    /** 注册号 */
    @Excel(name = "注册号")
    private String regnumber;

    /** 法人 */
    @Excel(name = "法人")
    private String legalpersonname;

    /** 1-公司 2-人 */
    @Excel(name = "1-公司 2-人")
    private Long type;

    /** 匹配原因 */
    @Excel(name = "匹配原因")
    private String matchtype;

    /** 机构类型-1：公司；2：香港企业；3：社会组织；4：律所；5：事业单位；6：基金会；7-不存在法人、注册资本、统一社会信用代码、经营状态;8：台湾企业；9-新机构 */
    @Excel(name = "机构类型-1：公司；2：香港企业；3：社会组织；4：律所；5：事业单位；6：基金会；7-不存在法人、注册资本、统一社会信用代码、经营状态;8：台湾企业；9-新机构")
    private Long companytype;

    /** 注册资本 */
    @Excel(name = "注册资本")
    private String regcapital;

    /** 成立日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "成立日期", width = 30, dateFormat = "yyyy-MM-dd")
    private String estiblishtime;

    /** 经营状态 */
    @Excel(name = "经营状态")
    private String regstatus;

    /** 省份 */
    @Excel(name = "省份")
    private String base;

    /** 组织机构代码 */
    @Excel(name = "组织机构代码")
    private String orgnumber;

    /**匹配相似度*/
    private Float sameRatio;

    /**是否匹配关键词*/
    private Boolean isMatch;

    /**简称*/
    private String keyword;

}
