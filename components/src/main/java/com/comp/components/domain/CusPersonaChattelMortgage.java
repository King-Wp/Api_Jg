package com.comp.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 动产抵押信息对象 cus_persona_chattel_mortgage
 * 
 * @author wucilong
 * @date 2023-02-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CusPersonaChattelMortgage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表ID */
    private Long id;

    /** 被担保债权数额 */
    @Excel(name = "被担保债权数额")
    private String amount;

    /** 公示日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "公示日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishDate;

    /** 登记日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "登记日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date regDate;

    /** 登记编号 */
    @Excel(name = "登记编号")
    private String regNum;

    /** 被担保债权种类 */
    @Excel(name = "被担保债权种类")
    private String type;

    /** 登记机关 */
    @Excel(name = "登记机关")
    private String regDepartment;

    /** 债务人履行债务的期限 */
    @Excel(name = "债务人履行债务的期限")
    private String term;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 天眼查公司ID */
    @Excel(name = "天眼查公司ID")
    private String companyId;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("amount", getAmount())
            .append("publishDate", getPublishDate())
            .append("regDate", getRegDate())
            .append("regNum", getRegNum())
            .append("type", getType())
            .append("regDepartment", getRegDepartment())
            .append("term", getTerm())
            .append("companyName", getCompanyName())
            .append("companyId", getCompanyId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
