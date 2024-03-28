package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 公司详情供应商信息对象 cus_persona_company_supplier
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public class CusPersonaCompanySupplier extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 供应商id */
    private Long supplierGraphId;

    /** 报告期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报告期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date announcementDate;

    /** 采购金额（万元） */
    @Excel(name = "采购金额（万元）")
    private BigDecimal amt;

    /** logo */
//    @Excel(name = "logo")
    private String logo;

    /** 简称 */
//    @Excel(name = "简称")
    private String alias;

    /** 供应商名称 */
    @Excel(name = "供应商名称")
    private String supplierName;

    /** 关联关系 */
//    @Excel(name = "关联关系")
    private String relationship;

    /** 数据来源 */
    @Excel(name = "数据来源")
    private String dataSource;

    /** 采购占比 */
//    @Excel(name = "采购占比")
    private String ratio;

    /** 公司名称 */
//    @Excel(name = "公司名称")
    private String companyName;

    /** 天眼查公司ID*/
    private String companyId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSupplierGraphId(Long supplierGraphId) 
    {
        this.supplierGraphId = supplierGraphId;
    }

    public Long getSupplierGraphId() 
    {
        return supplierGraphId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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
    public void setSupplierName(String supplierName) 
    {
        this.supplierName = supplierName;
    }

    public String getSupplierName() 
    {
        return supplierName;
    }
    public void setRelationship(String relationship) 
    {
        this.relationship = relationship;
    }

    public String getRelationship() 
    {
        return relationship;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("supplierGraphId", getSupplierGraphId())
            .append("announcementDate", getAnnouncementDate())
            .append("amt", getAmt())
            .append("logo", getLogo())
            .append("alias", getAlias())
            .append("supplierName", getSupplierName())
            .append("relationship", getRelationship())
            .append("dataSource", getDataSource())
            .append("ratio", getRatio())
            .append("companyName", getCompanyName())
            .append("companyId", getCompanyId())
            .toString();
    }
}
