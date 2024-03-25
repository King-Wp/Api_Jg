package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * areaInvest对象 cus_persona_area_invest
 * 
 * @author wucilong
 * @date 2022-11-04
 */

@EqualsAndHashCode(callSuper = false)
@Data
public class CusPersonaAreaInvest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 投资地区名称 */
    @Excel(name = "投资地区名称")
    private String areaName;

    /** 投资数量 */
    @Excel(name = "投资数量")
    private Long areaNum;

    /** 投资地区值 */
    @Excel(name = "投资地区值")
    private String areaKey;

    /** 企业ID */
    @Excel(name = "企业ID")
    private String companyId;

    /** 客户公司名称*/
    private String companyName;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("areaName", getAreaName())
            .append("areaNum", getAreaNum())
            .append("areaKey", getAreaKey())
            .append("companyId", getCompanyId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("companyName",getCompanyName())
            .toString();
    }
}
