package com.ruoyi.components.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * categoryInvest对象 cus_persona_category_invest
 * 
 * @author ruoyi
 * @date 2022-11-04
 */

@EqualsAndHashCode(callSuper = false)
@Data
public class CusPersonaCategoryInvest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表主键 */
    private Long id;

    /** 投资行业名称 */
    @Excel(name = "投资行业名称")
    private String categoryName;

    /** 投资数量 */
    @Excel(name = "投资数量")
    private Long categoryNum;

    /** 对外投资行业值 */
    @Excel(name = "对外投资行业值")
    private String categoryKey;

    /** 企业id（天眼查唯一标识） */
    @Excel(name = "企业id", readConverterExp = "天=眼查唯一标识")
    private String companyId;

    /** 客户单位名称*/
    private String companyName;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("categoryName", getCategoryName())
            .append("categoryNum", getCategoryNum())
            .append("categoryKey", getCategoryKey())
            .append("companyId", getCompanyId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("companyName", getCompanyName())
            .toString();
    }
}
