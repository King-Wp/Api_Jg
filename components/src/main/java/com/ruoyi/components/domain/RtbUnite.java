package com.ruoyi.components.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 联动关键词对象 rtb_unite_keyword
 * 
 * @author ruoyi
 * @date 2023-12-20
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class RtbUnite extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 标识(report-商机, visit-拜访) */
    private String baseCode;

    /** 表id */
    private Long baseId;

    /** 单位名称 */
    @Excel(name = "单位名称")
    private String companyName;

    /** 项目名称 */
    @Excel(name = "项目名称")
    private String itemName;

    /** 项目关键词 */
    @Excel(name = "项目关键词")
    private String itemKeyword;

    /** 招投标标题 */
    @Excel(name = "招投标标题")
    private String bidTitle;

    /** 招投标关键词 */
    @Excel(name = "招投标关键词")
    private String bidKeyword;

    /** 业绩合同名称 */
    @Excel(name = "业绩合同名称")
    private String contractName;

    /** 业绩合同关键词 */
    @Excel(name = "业绩合同关键词")
    private String contractKeyword;

    /** 经营范围 */
    @Excel(name = "经营范围")
    private String busineScope;

    /** 经营关键词 */
    @Excel(name = "经营关键词")
    private String busineKeyword;

    /** 行业 */
    private String industry;
}
