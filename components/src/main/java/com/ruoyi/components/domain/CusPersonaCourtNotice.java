package com.ruoyi.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 开庭公告对象 cus_persona_court_notice
 * 
 * @author wucilong
 * @date 2022-12-21
 */
public class CusPersonaCourtNotice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 案件id */
    private Long id;

    /** 承办部门 */
    @Excel(name = "承办部门")
    private String contractors;

    /** 原告/上诉人名称 */
    @Excel(name = "原告/上诉人名称")
    private String plaintiff;

    /** 被告人名称 */
    @Excel(name = "被告人名称")
    private String defendantName;

    /** 法庭 */
    @Excel(name = "法庭")
    private String courtroom;

    /** 被告/被上诉人名称 */
    @Excel(name = "被告/被上诉人名称")
    private String defendant;

    /** 审判长 */
    @Excel(name = "审判长")
    private String judge;

    /** 法院 */
    @Excel(name = "法院")
    private String court;

    /** 案号 */
    @Excel(name = "案号")
    private String caseNo;

    /** 案由 */
    @Excel(name = "案由")
    private String caseReason;

    /** 天眼查公司ID（用这个公司ID搜索得到的开庭报告） */
    @Excel(name = "天眼查公司ID", readConverterExp = "用=这个公司ID搜索得到的开庭报告")
    private String companyId;

    /** 搜索的公司名称 */
    @Excel(name = "搜索的公司名称")
    private String companyName;

    /** 当事人 */
    @Excel(name = "当事人")
    private String litigant;

    /** 开庭日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开庭日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 天眼查开庭公告ID */
    private String noticeId;

    /** 查询关键字*/
    public String keyword;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setContractors(String contractors) 
    {
        this.contractors = contractors;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getContractors()
    {
        return contractors;
    }
    public void setPlaintiff(String plaintiff) 
    {
        this.plaintiff = plaintiff;
    }

    public String getPlaintiff() 
    {
        return plaintiff;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setDefendantName(String defendantName)
    {
        this.defendantName = defendantName;
    }

    public String getDefendantName() 
    {
        return defendantName;
    }
    public void setCourtroom(String courtroom) 
    {
        this.courtroom = courtroom;
    }

    public String getCourtroom() 
    {
        return courtroom;
    }
    public void setDefendant(String defendant) 
    {
        this.defendant = defendant;
    }

    public String getDefendant() 
    {
        return defendant;
    }
    public void setJudge(String judge) 
    {
        this.judge = judge;
    }

    public String getJudge() 
    {
        return judge;
    }
    public void setCourt(String court) 
    {
        this.court = court;
    }

    public String getCourt() 
    {
        return court;
    }
    public void setCaseNo(String caseNo) 
    {
        this.caseNo = caseNo;
    }

    public String getCaseNo() 
    {
        return caseNo;
    }
    public void setCaseReason(String caseReason) 
    {
        this.caseReason = caseReason;
    }

    public String getCaseReason() 
    {
        return caseReason;
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
    public void setLitigant(String litigant) 
    {
        this.litigant = litigant;
    }

    public String getLitigant() 
    {
        return litigant;
    }
    public void setStartDate(Date startDate) 
    {
        this.startDate = startDate;
    }

    public Date getStartDate() 
    {
        return startDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("contractors", getContractors())
            .append("plaintiff", getPlaintiff())
            .append("defendantName", getDefendantName())
            .append("courtroom", getCourtroom())
            .append("defendant", getDefendant())
            .append("judge", getJudge())
            .append("court", getCourt())
            .append("caseNo", getCaseNo())
            .append("caseReason", getCaseReason())
            .append("companyId", getCompanyId())
            .append("companyName", getCompanyName())
            .append("litigant", getLitigant())
            .append("startDate", getStartDate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
