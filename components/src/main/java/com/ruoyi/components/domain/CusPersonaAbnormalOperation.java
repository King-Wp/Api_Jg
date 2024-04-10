package com.ruoyi.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 经营异常信息对象 cus_persona_abnormal_operation
 * 
 * @author persona
 * @date 2023-02-08
 */
public class CusPersonaAbnormalOperation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表ID */
    private Long id;

    /** 移出日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "移出日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date removeDate;

    /** 列入异常名录原因 */
    @Excel(name = "列入异常名录原因")
    private String putReason;

    /** 决定列入异常名录部门(作出决定机关) */
    @Excel(name = "决定列入异常名录部门(作出决定机关)")
    private String putDepartment;

    /** 移出部门 */
    @Excel(name = "移出部门")
    private String removeDepartment;

    /** 移除异常名录原因 */
    @Excel(name = "移除异常名录原因")
    private String removeReason;

    /** 列入日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "列入日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date putDate;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 天眼查公司ID */
    @Excel(name = "天眼查公司ID")
    private String companyId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setRemoveDate(Date removeDate) 
    {
        this.removeDate = removeDate;
    }

    public Date getRemoveDate() 
    {
        return removeDate;
    }
    public void setPutReason(String putReason) 
    {
        this.putReason = putReason;
    }

    public String getPutReason() 
    {
        return putReason;
    }
    public void setPutDepartment(String putDepartment) 
    {
        this.putDepartment = putDepartment;
    }

    public String getPutDepartment() 
    {
        return putDepartment;
    }
    public void setRemoveDepartment(String removeDepartment) 
    {
        this.removeDepartment = removeDepartment;
    }

    public String getRemoveDepartment() 
    {
        return removeDepartment;
    }
    public void setRemoveReason(String removeReason) 
    {
        this.removeReason = removeReason;
    }

    public String getRemoveReason() 
    {
        return removeReason;
    }
    public void setPutDate(Date putDate) 
    {
        this.putDate = putDate;
    }

    public Date getPutDate() 
    {
        return putDate;
    }
    public void setCompanyName(String companyName) 
    {
        this.companyName = companyName;
    }

    public String getCompanyName() 
    {
        return companyName;
    }
    public void setCompanyId(String companyId) 
    {
        this.companyId = companyId;
    }

    public String getCompanyId() 
    {
        return companyId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("removeDate", getRemoveDate())
            .append("putReason", getPutReason())
            .append("putDepartment", getPutDepartment())
            .append("removeDepartment", getRemoveDepartment())
            .append("removeReason", getRemoveReason())
            .append("putDate", getPutDate())
            .append("companyName", getCompanyName())
            .append("companyId", getCompanyId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
