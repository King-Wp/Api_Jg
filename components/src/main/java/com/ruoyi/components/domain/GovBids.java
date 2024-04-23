package com.ruoyi.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 招投标数据对象 cus_gov_bids
 * 
 * @author Leeyq
 * @date 2022-08-25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GovBids extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 合同id */
    private Long articleId;

    /** 站点id */
    private Long siteId;

    /** 模块码 */
    private String firstCode;

    /** 类型码 */
    private String secondCode;

    /** 详细信息链接 */
    @Excel(name = "详细信息链接")
    private String url;

    /** 来源 */
    @Excel(name = "来源")
    private String author;

    /** 类型码 */
    private String path;

    /** 投标类型 */
    @Excel(name = "投标类型")
    private String pathName;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 正文信息 */
    @Excel(name = "正文信息")
    private String content;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishDate;

    /** 区划 */
    @Excel(name = "区划")
    private String districtCode;

    /** 0有效 */
    private Long invalid;

    private String source;

    private String keyword;

}
