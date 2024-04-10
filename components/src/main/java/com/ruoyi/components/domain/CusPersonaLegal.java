package com.ruoyi.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 司法解析对象 cus_persona_legal
 * 
 * @author wucilong
 * @date 2022-12-21
 */
public class CusPersonaLegal extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 对应表ID */
    private Long id;

    /** 文书类型 */
    @Excel(name = "文书类型")
    private String docType;

    /** 天眼查url(web) */
    @Excel(name = "天眼查url(web)")
    private String lawsuitUrl;

    /** 天眼查url(h5) */
    @Excel(name = "天眼查url(h5)")
    private String lawsuitH5Url;

    /** 案件名称 */
    @Excel(name = "案件名称")
    private String title;

    /** 审理法院 */
    @Excel(name = "审理法院")
    private String court;

    /** 裁判日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "裁判日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date judgeTime;


    /** 案号 */
    @Excel(name = "案号")
    private String caseNo;

    /** 案件类型 */
    @Excel(name = "案件类型")
    private String caseType;

    /** 案由 */
    @Excel(name = "案由")
    private String caseReason;

    /** 结果标签 第一位起诉方 其余被起诉方 */
    @Excel(name = "结果标签 第一位起诉方 其余被起诉方")
    private String caseResult;

    /** 涉案方身份 第一位原告 后面都是被告 */
    @Excel(name = "涉案方身份 第一位原告 后面都是被告")
    private String caseRole;

    /** 裁判结果对应的情感倾向（1=正面；0=中性；-1=负面） */
    @Excel(name = "裁判结果对应的情感倾向", readConverterExp = "1==正面；0=中性；-1=负面")
    private String caseEmotion;

    /** 按键金额 */
    @Excel(name = "按键金额")
    private String caseMoney;

    /** 天眼查公司ID（搜索的ID） */
    @Excel(name = "天眼查公司ID", readConverterExp = "搜=索的ID")
    private String companyId;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;
    /** 法律诉讼唯一ID */
    private String courtUuid;
    /** 查询关键字 */
    private String keyword;
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDocType(String docType) 
    {
        this.docType = docType;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCourtUuid() {
        return courtUuid;
    }

    public void setCourtUuid(String courtUuid) {
        this.courtUuid = courtUuid;
    }

    public String getDocType()
    {
        return docType;
    }
    public void setLawsuitUrl(String lawsuitUrl) 
    {
        this.lawsuitUrl = lawsuitUrl;
    }

    public String getLawsuitUrl() 
    {
        return lawsuitUrl;
    }
    public void setLawsuitH5Url(String lawsuitH5Url) 
    {
        this.lawsuitH5Url = lawsuitH5Url;
    }

    public String getLawsuitH5Url() 
    {
        return lawsuitH5Url;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setCourt(String court) 
    {
        this.court = court;
    }

    public String getCourt() 
    {
        return court;
    }
    public void setJudgeTime(Date judgeTime) 
    {
        this.judgeTime = judgeTime;
    }

    public Date getJudgeTime() 
    {
        return judgeTime;
    }

    public void setCaseNo(String caseNo) 
    {
        this.caseNo = caseNo;
    }

    public String getCaseNo() 
    {
        return caseNo;
    }
    public void setCaseType(String caseType) 
    {
        this.caseType = caseType;
    }

    public String getCaseType() 
    {
        return caseType;
    }
    public void setCaseReason(String caseReason) 
    {
        this.caseReason = caseReason;
    }

    public String getCaseReason() 
    {
        return caseReason;
    }
    public void setCaseResult(String caseResult) 
    {
        this.caseResult = caseResult;
    }

    public String getCaseResult() 
    {
        return caseResult;
    }
    public void setCaseRole(String caseRole) 
    {
        this.caseRole = caseRole;
    }

    public String getCaseRole() 
    {
        return caseRole;
    }
    public void setCaseEmotion(String caseEmotion) 
    {
        this.caseEmotion = caseEmotion;
    }

    public String getCaseEmotion() 
    {
        return caseEmotion;
    }
    public void setCaseMoney(String caseMoney) 
    {
        this.caseMoney = caseMoney;
    }

    public String getCaseMoney() 
    {
        return caseMoney;
    }
    public void setCompanyId(String companyId) 
    {
        this.companyId = companyId;
    }

    public String getCompanyId() 
    {
        return companyId;
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
            .append("docType", getDocType())
            .append("lawsuitUrl", getLawsuitUrl())
            .append("lawsuitH5Url", getLawsuitH5Url())
            .append("title", getTitle())
            .append("court", getCourt())
            .append("judgeTime", getJudgeTime())
            .append("caseNo", getCaseNo())
            .append("caseType", getCaseType())
            .append("caseReason", getCaseReason())
            .append("caseResult", getCaseResult())
            .append("caseRole", getCaseRole())
            .append("caseEmotion", getCaseEmotion())
            .append("caseMoney", getCaseMoney())
            .append("companyId", getCompanyId())
            .append("companyName", getCompanyName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
