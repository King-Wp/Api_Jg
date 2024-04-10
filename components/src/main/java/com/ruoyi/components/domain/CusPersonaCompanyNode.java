package com.ruoyi.components.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业关系图谱节点对象 cus_persona_company_node
 *
 * @author wucilong
 * @date 2022-09-05
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class CusPersonaCompanyNode extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 节点id
     */
    @Excel(name = "节点id")
    private String nodeId;

    /**
     * 节点简称
     */
    @Excel(name = "节点简称")
    private String nodeAias;

    /**
     * 节点名称
     */
    @Excel(name = "节点名称")
    private String nodeName;

    /**
     * 节点logo地址
     */
    @Excel(name = "节点logo地址")
    private String nodeLogo;

    /**
     * 节点类型 s-人 f-公司
     */
    @Excel(name = "节点类型 s-人 f-公司")
    private String ntype;

    /**
     * 节点标签
     */
    @Excel(name = "节点标签")
    private String labels;

    /**
     * 公司名称
     */
    @Excel(name = "公司名称")
    private String companyName;

    /**
     * 公司ID
     */
    @Excel(name = "公司ID")
    private String companyId;
}
