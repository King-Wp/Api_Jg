package com.comp.components.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 企业关系图谱对象 cus_persona_company_relationship
 *
 * @author wucilong
 * @date 2022-09-05
 */

@EqualsAndHashCode(callSuper = false)
@Data
public class CusPersonaCompanyRelationship extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 开始节点 */
    @Excel(name = "开始节点")
    private String startNode;

    /** 关系ID */
    @Excel(name = "关系ID")
    private String relationshipId;

    /** 尾部节点 */
    @Excel(name = "尾部节点")
    private String endNode;

    /** 关系标签 */
    @Excel(name = "关系标签")
    private String labels;

    /** 占比 */
    @Excel(name = "占比")
    private String percent;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 公司ID */
    @Excel(name = "公司ID")
    private String companyId;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("startNode", getStartNode())
            .append("relationshipId", getRelationshipId())
            .append("endNode", getEndNode())
            .append("labels", getLabels())
            .append("percent", getPercent())
            .append("companyName", getCompanyName())
            .append("companyId", getCompanyId())
            .toString();
    }
}
