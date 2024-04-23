package com.ruoyi.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 行政处罚对象 cus_persona_punishment_administrative
 * 
 * @author wucilong
 * @date 2023-02-09
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class CusPersonaPunishmentAdministrative extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 处罚日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "处罚日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date decisionDate;

    /** 决定文书号 */
    @Excel(name = "决定文书号")
    private String punishNumber;

    /** 处罚事由/违法行为类型 */
    @Excel(name = "处罚事由/违法行为类型")
    private String reason;

    /** 处罚结果/内容 */
    @Excel(name = "处罚结果/内容")
    private String content;

    /** 处罚单位 */
    @Excel(name = "处罚单位")
    private String departmentName;

    /** 数据来源 */
    @Excel(name = "数据来源")
    private String source;

    /** 处罚状态 */
    @Excel(name = "处罚状态")
    private String punishStatus;

    /** 处罚金额 */
    @Excel(name = "处罚金额")
    private String punishAmount;

    /** 处罚依据 */
    @Excel(name = "处罚依据")
    private String evidence;

    /** 处罚名称 */
    @Excel(name = "处罚名称")
    private String punishName;

    /** 天眼查公司ID */
    @Excel(name = "天眼查公司ID")
    private String companyId;

    /** 查询公司名称 */
    @Excel(name = "查询公司名称")
    private String companyName;
}
