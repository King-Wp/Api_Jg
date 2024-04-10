package com.ruoyi.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 企业证书信息对象 cus_persona_company_cert
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public class CusPersonaCompanyCert extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 证书编号 */
    @Excel(name = "证书编号")
    private String certNo;

    /** 证书类型 */
    @Excel(name = "证书类型")
    private String certificateName;

    /** 发证时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发证时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 截止日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "截止日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 产品名称及单元（主） */
    @Excel(name = "产品名称及单元", readConverterExp = "主=")
    private String productUnit;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 企业ID */
    @Excel(name = "企业ID")
    private String companyId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCertNo(String certNo) 
    {
        this.certNo = certNo;
    }

    public String getCertNo() 
    {
        return certNo;
    }
    public void setCertificateName(String certificateName) 
    {
        this.certificateName = certificateName;
    }

    public String getCertificateName() 
    {
        return certificateName;
    }
    public void setStartDate(Date startDate) 
    {
        this.startDate = startDate;
    }

    public Date getStartDate() 
    {
        return startDate;
    }
    public void setEndDate(Date endDate) 
    {
        this.endDate = endDate;
    }

    public Date getEndDate() 
    {
        return endDate;
    }
    public void setProductUnit(String productUnit) 
    {
        this.productUnit = productUnit;
    }

    public String getProductUnit() 
    {
        return productUnit;
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
            .append("certNo", getCertNo())
            .append("certificateName", getCertificateName())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("productUnit", getProductUnit())
            .append("companyName", getCompanyName())
            .append("companyId", getCompanyId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
