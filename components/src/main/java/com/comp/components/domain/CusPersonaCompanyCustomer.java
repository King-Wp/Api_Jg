package com.comp.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业客户信息对象 cus_persona_company_customer
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public class CusPersonaCompanyCustomer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 报告期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报告期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date announcementDate;

    /** 销售金额 */
    @Excel(name = "销售金额")
    private BigDecimal amt;

    /** logo */
//    @Excel(name = "logo")
    private String logo;

    /** 简称 */
//    @Excel(name = "简称")
    private String alias;

    /** 客户id */
//    @Excel(name = "客户id")
    private Long clientGraphId;

    /** 关联关系 */
//    @Excel(name = "关联关系")
    private String relationship;

    /** 客户名 */
    @Excel(name = "客户名称")
    private String clientName;

    /** 数据来源 */
    @Excel(name = "数据来源")
    private String dataSource;

    /** $column.columnComment */
//    @Excel(name = "采购占比")
    private String ratio;

    /** 公司名称 */
//    @Excel(name = "公司名称")
    private String companyName;

    /** 公司ID */
//    @Excel(name = "公司ID")
    private String companyId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setAnnouncementDate(Date announcementDate) 
    {
        this.announcementDate = announcementDate;
    }

    public Date getAnnouncementDate() 
    {
        return announcementDate;
    }
    public void setAmt(BigDecimal amt) 
    {
        this.amt = amt;
    }

    public BigDecimal getAmt() 
    {
        return amt;
    }
    public void setLogo(String logo) 
    {
        this.logo = logo;
    }

    public String getLogo() 
    {
        return logo;
    }
    public void setAlias(String alias) 
    {
        this.alias = alias;
    }

    public String getAlias() 
    {
        return alias;
    }
    public void setClientGraphId(Long clientGraphId) 
    {
        this.clientGraphId = clientGraphId;
    }

    public Long getClientGraphId() 
    {
        return clientGraphId;
    }
    public void setRelationship(String relationship) 
    {
        this.relationship = relationship;
    }

    public String getRelationship() 
    {
        return relationship;
    }
    public void setClientName(String clientName) 
    {
        this.clientName = clientName;
    }

    public String getClientName() 
    {
        return clientName;
    }
    public void setDataSource(String dataSource) 
    {
        this.dataSource = dataSource;
    }

    public String getDataSource() 
    {
        return dataSource;
    }
    public void setRatio(String ratio)
    {
        this.ratio = ratio;
    }

    public String getRatio()
    {
        return ratio;
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
            .append("announcementDate", getAnnouncementDate())
            .append("amt", getAmt())
            .append("logo", getLogo())
            .append("alias", getAlias())
            .append("clientGraphId", getClientGraphId())
            .append("relationship", getRelationship())
            .append("clientName", getClientName())
            .append("dataSource", getDataSource())
            .append("ratio", getRatio())
            .append("companyName", getCompanyName())
            .append("companyId", getCompanyId())
            .toString();
    }
}
