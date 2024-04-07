package com.comp.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 招投标数据对象 cus_persona_bids
 * 
 * @author Leeyq
 * @date 2022-08-25
 */
@Data
public class PersonaBids extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** uuid */
    private String uuid;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishTime;

    /** 天眼查链接 */
    @Excel(name = "天眼查链接")
    private String bidUrl;

    /** 采购人 */
    @Excel(name = "采购人")
    private String purchaser;

    /** 详细信息链接 */
    @Excel(name = "详细信息链接")
    private String link;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 正文信息 */
    @Excel(name = "正文信息")
    private String content;

    /** 代理机构 */
    @Excel(name = "代理机构")
    private String proxy;

    /** 摘要信息 */
    @Excel(name = "摘要信息")
    private String abs;

    /** 正文简介 */
    @Excel(name = "正文简介")
    private String intro;

    /** 公告类型 */
    @Excel(name = "公告类型")
    private String type;

    /** 省份地区 */
    @Excel(name = "省份地区")
    private String province;

    /** 供应商 */
    @Excel(name = "供应商")
    private String bidWinner;

    /** 中标金额 */
    @Excel(name = "中标金额")
    private String bidAmount;

    private String purchaserCompanyId;

    /** 竞标公司*/
    private String competitiveCompany;

    private String source;

    private String keyword;

    private String industryType;

    private String year;

}
