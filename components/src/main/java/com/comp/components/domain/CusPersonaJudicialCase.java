package com.comp.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 客户画像司法解析对象 cus_persona_judicial_case
 * 
 * @author wucilong
 * @date 2022-09-08
 */
public class CusPersonaJudicialCase extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 案号 */
    @Excel(name = "案号")
    private String caseCode;

    /** 当前审理程序日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "当前审理程序日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date trialTime;

    /** 案件身份 */
    @Excel(name = "案件身份")
    private String caseIdentity;

    /** 当前审理程序 */
    @Excel(name = "当前审理程序")
    private String trialProcedure;

    /** 案件名称 */
    @Excel(name = "案件名称")
    private String caseTitle;

    /** 案由 */
    @Excel(name = "案由")
    private String caseReason;

    /** 案件类型 */
    @Excel(name = "案件类型")
    private String caseType;

    /** 业主单位名称 */
    @Excel(name = "业主单位名称")
    private String companyName;

    /** 查询关键字*/
    private String keyword;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCaseCode(String caseCode) 
    {
        this.caseCode = caseCode;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCaseCode()
    {
        return caseCode;
    }
    public void setTrialTime(Date trialTime) 
    {
        this.trialTime = trialTime;
    }

    public Date getTrialTime() 
    {
        return trialTime;
    }
    public void setCaseIdentity(String caseIdentity) 
    {
        this.caseIdentity = caseIdentity;
    }

    public String getCaseIdentity() 
    {
        return caseIdentity;
    }
    public void setTrialProcedure(String trialProcedure) 
    {
        this.trialProcedure = trialProcedure;
    }

    public String getTrialProcedure() 
    {
        return trialProcedure;
    }
    public void setCaseTitle(String caseTitle) 
    {
        this.caseTitle = caseTitle;
    }

    public String getCaseTitle() 
    {
        return caseTitle;
    }
    public void setCaseReason(String caseReason) 
    {
        this.caseReason = caseReason;
    }

    public String getCaseReason() 
    {
        return caseReason;
    }
    public void setCaseType(String caseType) 
    {
        this.caseType = caseType;
    }

    public String getCaseType() 
    {
        return caseType;
    }
    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("caseCode", getCaseCode())
            .append("trialTime", getTrialTime())
            .append("caseIdentity", getCaseIdentity())
            .append("trialProcedure", getTrialProcedure())
            .append("caseTitle", getCaseTitle())
            .append("caseReason", getCaseReason())
            .append("caseType", getCaseType())
            .append("companyName", getCompanyName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
