package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 企业年报对象 cus_persona_annual_reports
 * 
 * @author wucilong
 * @date 2022-12-29
 */
public class CusPersonaAnnualReports extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 从业人数 */
    @Excel(name = "从业人数")
    private String employeeNum;

    /** 资产总额 */
    @Excel(name = "资产总额")
    private String totalAssets;

    /** 利润总额 */
    @Excel(name = "利润总额")
    private String totalProfit;

    /** 负债总额 */
    @Excel(name = "负债总额")
    private String totalLiability;

    /** 企业名称 */
    @Excel(name = "企业名称")
    private String companyName;

    /** 邮政编码 */
    @Excel(name = "邮政编码")
    private String postcode;

    /** 销售总额(营业总收入) */
    @Excel(name = "销售总额(营业总收入)")
    private String totalSales;

    /** 净利润 */
    @Excel(name = "净利润")
    private String retainedProfit;

    /** 纳税总额 */
    @Excel(name = "纳税总额")
    private String totalTax;

    /** 所有者权益合计 */
    @Excel(name = "所有者权益合计")
    private String totalEquity;

    /** 统一社会信用代码 */
    @Excel(name = "统一社会信用代码")
    private String creditCode;

    /** 企业联系电话 */
    @Excel(name = "企业联系电话")
    private String phoneNumber;

    /** 企业通信地址 */
    @Excel(name = "企业通信地址")
    private String postalAddress;

    /** 主营业务收入 */
    @Excel(name = "主营业务收入")
    private String primeBusProfit;

    /** 企业经营状态 */
    @Excel(name = "企业经营状态")
    private String manageState;

    /** 网址 */
    @Excel(name = "网址")
    private String website;

    /** 网站名称 */
    @Excel(name = "网站名称")
    private String webName;

    /** 电子邮箱 */
    @Excel(name = "电子邮箱")
    private String email;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date releaseTime;

    /** 参加职工基本医疗保险本期实际缴费金额 */
    @Excel(name = "参加职工基本医疗保险本期实际缴费金额")
    private String medicalInsurancePayAmount;

    /** 单位参加职工基本医疗保险累计欠缴金额 */
    @Excel(name = "单位参加职工基本医疗保险累计欠缴金额")
    private String medicalInsuranceOweAmount;

    /** 单位参加失业保险缴费基数 */
    @Excel(name = "单位参加失业保险缴费基数")
    private String unemploymentInsuranceBase;

    /** 单位参加失业保险累计欠缴金额 */
    @Excel(name = "单位参加失业保险累计欠缴金额")
    private String unemploymentInsuranceOweAmount;

    /** 参加失业保险本期实际缴费金额 */
    @Excel(name = "参加失业保险本期实际缴费金额")
    private String unemploymentInsurancePayAmount;

    /** 城镇职工基本养老保险 */
    @Excel(name = "城镇职工基本养老保险")
    private String endowmentInsurance;

    /** 参加生育保险本期实际缴费金额 */
    @Excel(name = "参加生育保险本期实际缴费金额")
    private String maternityInsurancePayAmount;

    /** 参加工伤保险本期实际缴费金额 */
    @Excel(name = "参加工伤保险本期实际缴费金额")
    private String employmentInjuryInsurancePayAmount;

    /** 职工基本医疗保险 */
    @Excel(name = "职工基本医疗保险")
    private String medicalInsurance;

    /** 单位参加职工基本医疗保险缴费基数 */
    @Excel(name = "单位参加职工基本医疗保险缴费基数")
    private String medicalInsuranceBase;

    /** 失业保险 */
    @Excel(name = "失业保险")
    private String unemploymentInsurance;

    /** 单位参加城镇职工基本养老保险缴费基数 */
    @Excel(name = "单位参加城镇职工基本养老保险缴费基数")
    private String endowmentInsuranceBase;

    /** 单位参加生育保险缴费基数 */
    @Excel(name = "单位参加生育保险缴费基数")
    private String maternityInsuranceBase;

    /** 参加城镇职工基本养老保险本期实际缴费金额 */
    @Excel(name = "参加城镇职工基本养老保险本期实际缴费金额")
    private String endowmentInsurancePayAmount;

    /** 工伤保险 */
    @Excel(name = "工伤保险")
    private String employmentInjuryInsurance;

    /** 单位参加城镇职工基本养老保险累计欠缴金额 */
    @Excel(name = "单位参加城镇职工基本养老保险累计欠缴金额")
    private String endowmentInsuranceOweAmount;

    /** 生育保险 */
    @Excel(name = "生育保险")
    private String maternityInsurance;

    /** 单位参加生育保险累计欠缴金额 */
    @Excel(name = "单位参加生育保险累计欠缴金额")
    private String maternityInsuranceOweAmount;

    /** 单位参加工伤保险累计欠缴金额 */
    @Excel(name = "单位参加工伤保险累计欠缴金额")
    private String employmentInjuryInsuranceOweAmount;

    /** 天眼查公司ID */
    @Excel(name = "天眼查公司ID")
    private String companyId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setEmployeeNum(String employeeNum) 
    {
        this.employeeNum = employeeNum;
    }

    public String getEmployeeNum() 
    {
        return employeeNum;
    }
    public void setTotalAssets(String totalAssets) 
    {
        this.totalAssets = totalAssets;
    }

    public String getTotalAssets() 
    {
        return totalAssets;
    }
    public void setTotalProfit(String totalProfit) 
    {
        this.totalProfit = totalProfit;
    }

    public String getTotalProfit() 
    {
        return totalProfit;
    }
    public void setTotalLiability(String totalLiability) 
    {
        this.totalLiability = totalLiability;
    }

    public String getTotalLiability() 
    {
        return totalLiability;
    }
    public void setCompanyName(String companyName) 
    {
        this.companyName = companyName;
    }

    public String getCompanyName() 
    {
        return companyName;
    }
    public void setPostcode(String postcode) 
    {
        this.postcode = postcode;
    }

    public String getPostcode() 
    {
        return postcode;
    }
    public void setTotalSales(String totalSales) 
    {
        this.totalSales = totalSales;
    }

    public String getTotalSales() 
    {
        return totalSales;
    }
    public void setRetainedProfit(String retainedProfit) 
    {
        this.retainedProfit = retainedProfit;
    }

    public String getRetainedProfit() 
    {
        return retainedProfit;
    }
    public void setTotalTax(String totalTax) 
    {
        this.totalTax = totalTax;
    }

    public String getTotalTax() 
    {
        return totalTax;
    }
    public void setTotalEquity(String totalEquity) 
    {
        this.totalEquity = totalEquity;
    }

    public String getTotalEquity() 
    {
        return totalEquity;
    }
    public void setCreditCode(String creditCode) 
    {
        this.creditCode = creditCode;
    }

    public String getCreditCode() 
    {
        return creditCode;
    }
    public void setPhoneNumber(String phoneNumber) 
    {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() 
    {
        return phoneNumber;
    }
    public void setPostalAddress(String postalAddress) 
    {
        this.postalAddress = postalAddress;
    }

    public String getPostalAddress() 
    {
        return postalAddress;
    }
    public void setPrimeBusProfit(String primeBusProfit) 
    {
        this.primeBusProfit = primeBusProfit;
    }

    public String getPrimeBusProfit() 
    {
        return primeBusProfit;
    }
    public void setManageState(String manageState) 
    {
        this.manageState = manageState;
    }

    public String getManageState() 
    {
        return manageState;
    }
    public void setWebsite(String website) 
    {
        this.website = website;
    }

    public String getWebsite() 
    {
        return website;
    }
    public void setWebName(String webName) 
    {
        this.webName = webName;
    }

    public String getWebName() 
    {
        return webName;
    }
    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }
    public void setReleaseTime(Date releaseTime) 
    {
        this.releaseTime = releaseTime;
    }

    public Date getReleaseTime() 
    {
        return releaseTime;
    }
    public void setMedicalInsurancePayAmount(String medicalInsurancePayAmount) 
    {
        this.medicalInsurancePayAmount = medicalInsurancePayAmount;
    }

    public String getMedicalInsurancePayAmount() 
    {
        return medicalInsurancePayAmount;
    }
    public void setMedicalInsuranceOweAmount(String medicalInsuranceOweAmount) 
    {
        this.medicalInsuranceOweAmount = medicalInsuranceOweAmount;
    }

    public String getMedicalInsuranceOweAmount() 
    {
        return medicalInsuranceOweAmount;
    }
    public void setUnemploymentInsuranceBase(String unemploymentInsuranceBase) 
    {
        this.unemploymentInsuranceBase = unemploymentInsuranceBase;
    }

    public String getUnemploymentInsuranceBase() 
    {
        return unemploymentInsuranceBase;
    }
    public void setUnemploymentInsuranceOweAmount(String unemploymentInsuranceOweAmount) 
    {
        this.unemploymentInsuranceOweAmount = unemploymentInsuranceOweAmount;
    }

    public String getUnemploymentInsuranceOweAmount() 
    {
        return unemploymentInsuranceOweAmount;
    }
    public void setUnemploymentInsurancePayAmount(String unemploymentInsurancePayAmount) 
    {
        this.unemploymentInsurancePayAmount = unemploymentInsurancePayAmount;
    }

    public String getUnemploymentInsurancePayAmount() 
    {
        return unemploymentInsurancePayAmount;
    }
    public void setEndowmentInsurance(String endowmentInsurance) 
    {
        this.endowmentInsurance = endowmentInsurance;
    }

    public String getEndowmentInsurance() 
    {
        return endowmentInsurance;
    }
    public void setMaternityInsurancePayAmount(String maternityInsurancePayAmount) 
    {
        this.maternityInsurancePayAmount = maternityInsurancePayAmount;
    }

    public String getMaternityInsurancePayAmount() 
    {
        return maternityInsurancePayAmount;
    }
    public void setEmploymentInjuryInsurancePayAmount(String employmentInjuryInsurancePayAmount) 
    {
        this.employmentInjuryInsurancePayAmount = employmentInjuryInsurancePayAmount;
    }

    public String getEmploymentInjuryInsurancePayAmount() 
    {
        return employmentInjuryInsurancePayAmount;
    }
    public void setMedicalInsurance(String medicalInsurance) 
    {
        this.medicalInsurance = medicalInsurance;
    }

    public String getMedicalInsurance() 
    {
        return medicalInsurance;
    }
    public void setMedicalInsuranceBase(String medicalInsuranceBase) 
    {
        this.medicalInsuranceBase = medicalInsuranceBase;
    }

    public String getMedicalInsuranceBase() 
    {
        return medicalInsuranceBase;
    }
    public void setUnemploymentInsurance(String unemploymentInsurance) 
    {
        this.unemploymentInsurance = unemploymentInsurance;
    }

    public String getUnemploymentInsurance() 
    {
        return unemploymentInsurance;
    }
    public void setEndowmentInsuranceBase(String endowmentInsuranceBase) 
    {
        this.endowmentInsuranceBase = endowmentInsuranceBase;
    }

    public String getEndowmentInsuranceBase() 
    {
        return endowmentInsuranceBase;
    }
    public void setMaternityInsuranceBase(String maternityInsuranceBase) 
    {
        this.maternityInsuranceBase = maternityInsuranceBase;
    }

    public String getMaternityInsuranceBase() 
    {
        return maternityInsuranceBase;
    }
    public void setEndowmentInsurancePayAmount(String endowmentInsurancePayAmount) 
    {
        this.endowmentInsurancePayAmount = endowmentInsurancePayAmount;
    }

    public String getEndowmentInsurancePayAmount() 
    {
        return endowmentInsurancePayAmount;
    }
    public void setEmploymentInjuryInsurance(String employmentInjuryInsurance) 
    {
        this.employmentInjuryInsurance = employmentInjuryInsurance;
    }

    public String getEmploymentInjuryInsurance() 
    {
        return employmentInjuryInsurance;
    }
    public void setEndowmentInsuranceOweAmount(String endowmentInsuranceOweAmount) 
    {
        this.endowmentInsuranceOweAmount = endowmentInsuranceOweAmount;
    }

    public String getEndowmentInsuranceOweAmount() 
    {
        return endowmentInsuranceOweAmount;
    }
    public void setMaternityInsurance(String maternityInsurance) 
    {
        this.maternityInsurance = maternityInsurance;
    }

    public String getMaternityInsurance() 
    {
        return maternityInsurance;
    }
    public void setMaternityInsuranceOweAmount(String maternityInsuranceOweAmount) 
    {
        this.maternityInsuranceOweAmount = maternityInsuranceOweAmount;
    }

    public String getMaternityInsuranceOweAmount() 
    {
        return maternityInsuranceOweAmount;
    }
    public void setEmploymentInjuryInsuranceOweAmount(String employmentInjuryInsuranceOweAmount) 
    {
        this.employmentInjuryInsuranceOweAmount = employmentInjuryInsuranceOweAmount;
    }

    public String getEmploymentInjuryInsuranceOweAmount() 
    {
        return employmentInjuryInsuranceOweAmount;
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
            .append("employeeNum", getEmployeeNum())
            .append("totalAssets", getTotalAssets())
            .append("totalProfit", getTotalProfit())
            .append("totalLiability", getTotalLiability())
            .append("companyName", getCompanyName())
            .append("postcode", getPostcode())
            .append("totalSales", getTotalSales())
            .append("retainedProfit", getRetainedProfit())
            .append("totalTax", getTotalTax())
            .append("totalEquity", getTotalEquity())
            .append("creditCode", getCreditCode())
            .append("phoneNumber", getPhoneNumber())
            .append("postalAddress", getPostalAddress())
            .append("primeBusProfit", getPrimeBusProfit())
            .append("manageState", getManageState())
            .append("website", getWebsite())
            .append("webName", getWebName())
            .append("email", getEmail())
            .append("releaseTime", getReleaseTime())
            .append("medicalInsurancePayAmount", getMedicalInsurancePayAmount())
            .append("medicalInsuranceOweAmount", getMedicalInsuranceOweAmount())
            .append("unemploymentInsuranceBase", getUnemploymentInsuranceBase())
            .append("unemploymentInsuranceOweAmount", getUnemploymentInsuranceOweAmount())
            .append("unemploymentInsurancePayAmount", getUnemploymentInsurancePayAmount())
            .append("endowmentInsurance", getEndowmentInsurance())
            .append("maternityInsurancePayAmount", getMaternityInsurancePayAmount())
            .append("employmentInjuryInsurancePayAmount", getEmploymentInjuryInsurancePayAmount())
            .append("medicalInsurance", getMedicalInsurance())
            .append("medicalInsuranceBase", getMedicalInsuranceBase())
            .append("unemploymentInsurance", getUnemploymentInsurance())
            .append("endowmentInsuranceBase", getEndowmentInsuranceBase())
            .append("maternityInsuranceBase", getMaternityInsuranceBase())
            .append("endowmentInsurancePayAmount", getEndowmentInsurancePayAmount())
            .append("employmentInjuryInsurance", getEmploymentInjuryInsurance())
            .append("endowmentInsuranceOweAmount", getEndowmentInsuranceOweAmount())
            .append("maternityInsurance", getMaternityInsurance())
            .append("maternityInsuranceOweAmount", getMaternityInsuranceOweAmount())
            .append("employmentInjuryInsuranceOweAmount", getEmploymentInjuryInsuranceOweAmount())
            .append("companyId", getCompanyId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
