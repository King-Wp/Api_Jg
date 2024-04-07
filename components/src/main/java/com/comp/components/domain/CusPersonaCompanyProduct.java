package com.comp.components.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 企业产品信息对象 cus_persona_company_product
 * 
 * @author wucilong
 * @date 2022-09-05
 */
public class CusPersonaCompanyProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 领域 */
    @Excel(name = "领域")
    private String classes;

    /** 产品简称 */
    @Excel(name = "产品简称")
    private String filterName;

    /** 图标 */
    @Excel(name = "图标")
    private String icon;

    /** 产品分类 */
    @Excel(name = "产品分类")
    private String type;

    /** 描述 */
    @Excel(name = "描述")
    private String brief;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 公司ID */
    @Excel(name = "公司ID")
    private String companyId;

    /** 产品名称*/
    private String productName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getId()
    {
        return id;
    }
    public void setClasses(String classes) 
    {
        this.classes = classes;
    }

    public String getClasses() 
    {
        return classes;
    }
    public void setFilterName(String filterName) 
    {
        this.filterName = filterName;
    }

    public String getFilterName() 
    {
        return filterName;
    }
    public void setIcon(String icon) 
    {
        this.icon = icon;
    }

    public String getIcon() 
    {
        return icon;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setBrief(String brief) 
    {
        this.brief = brief;
    }

    public String getBrief() 
    {
        return brief;
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
            .append("classes", getClasses())
            .append("filterName", getFilterName())
            .append("icon", getIcon())
            .append("type", getType())
            .append("brief", getBrief())
            .append("companyName", getCompanyName())
            .append("companyId", getCompanyId())
            .append("productName", getProductName())
            .toString();
    }
}
