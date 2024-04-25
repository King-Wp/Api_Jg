package com.ruoyi.components.domain.ReceiveParameters;

import com.ruoyi.components.domain.RtbReportP;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: 11653
 * @createTime: 2024/04/10 16:14
 * @package: com.ruoyi.components.domain.ReceiveParameters
 * @description: 关键词获取参数
 */

@Data
public class KeyWordsParams implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 公司名称
     */
    private String company;
    /**
     * 商机ID 可以不传递
     */
    private Long baseId;
    /**
     * 单位经营范围
     */
    private String business;
    /**
     * 单位名称查询招投标标题金额top10
     */
    private List<String> bidTitles;
    /**
     * 单位名称查询业绩合同名称
     */
    private List<String>contracts;
    /**
     * 商机报备列表
     */
    private RtbReportP info;

}
