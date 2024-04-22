package com.ruoyi.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 失信被执行人对象 cus_persona_dishonest_person
 * 
 * @author wucilong
 * @date 2023-02-08
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class CusPersonaDishonestPerson extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表ID */
    private Long id;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishDate;

    /** 立案时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "立案时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date regDate;

    /** 案号 */
    @Excel(name = "案号")
    private String caseCode;

    /** 法院 */
    @Excel(name = "法院")
    private String courtName;

    /** 履行情况 */
    @Excel(name = "履行情况")
    private String performance;

    /** 失信被执行人行为具体情形 */
    @Excel(name = "失信被执行人行为具体情形")
    private String disruptTypeName;

    /** 执行依据文号 */
    @Excel(name = "执行依据文号")
    private String gistId;

    /** 生效法律文书确定的义务 */
    @Excel(name = "生效法律文书确定的义务")
    private String duty;

    /** 做出执行的依据单位 */
    @Excel(name = "做出执行的依据单位")
    private String gistUnit;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 天眼查公司ID */
    @Excel(name = "天眼查公司ID")
    private String companyId;

}
