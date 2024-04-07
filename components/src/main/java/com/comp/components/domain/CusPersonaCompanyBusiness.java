package com.comp.components.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 企业工商信息对象 cus_persona_company_business
 *
 * @author wucilong
 * @date 2022-09-05
 */

@EqualsAndHashCode(callSuper = false)
@Data
public class CusPersonaCompanyBusiness extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 业主单位名称
     */
    @Excel(name = "业主单位名称")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @Excel(name = "统一社会信用代码")
    private String creditCode;

    /**
     * 企业类型
     */
    @Excel(name = "企业类型")
    private String companyOrgType;

    /**
     * 参保人数
     */
    @Excel(name = "参保人数")
    private String socialStaffNum;

    /**
     * 曾用名
     */
    @Excel(name = "曾用名")
    private String historyNames;

    /**
     * 注册地址
     */
    @Excel(name = "注册地址")
    private String regLocation;

    /**
     * 企业状态
     */
    @Excel(name = "企业状态")
    private String regStatus;

    /**
     * 成立时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "成立时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date estiblishTime;

    /**
     * 经营结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "经营结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date toTime;

    /**
     * 注册资本
     */
    @Excel(name = "注册资本")
    private String regCapital;

    /**
     * 实收注册资金
     */
    @Excel(name = "实收注册资金")
    private String actualCapital;

    /**
     * 纳税人识别号
     */
    @Excel(name = "纳税人识别号")
    private String taxNumber;

    /**
     * 纳税人资质
     */
    private String taxQualification;


    /**
     * 营业期限
     */
    private String businessTerm;

    /**
     * 行业
     */
    @Excel(name = "行业")
    private String industry;

    /**
     * 行业类型
     */
    private String industryType;

    /**
     * 登记机关
     */
    @Excel(name = "登记机关")
    private String regInstitute;

    /**
     * 英文名(天眼查属性为property3)
     */
    @Excel(name = "英文名(天眼查属性为property3)")
    private String englishName;

    /**
     * 企业评分
     */
    @Excel(name = "企业评分")
    private String percentileScore;

    /**
     * 工商注册号
     */
    @Excel(name = "工商注册号")
    private String regNumber;

    /**
     * 组织机构代码
     */
    @Excel(name = "组织机构代码")
    private String orgNumber;

    //祥云企业性质(查询时若祥云企业性质不为空则显示祥云企业性质，否则显示天眼查企业性质)
    private String enterpriseType;

    /**
     * 核准时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "核准时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date approvedTime;

    /**
     * 人员规模
     */
    @Excel(name = "人员规模")
    private String staffNumRange;

    /**
     * 经营范围
     */
    @Excel(name = "经营范围")
    private String businessScope;

    /**
     * 省份简称
     */
    @Excel(name = "省份简称")
    private String base;

    /**
     * 企业联系方式
     */
    @Excel(name = "企业联系方式")
    private String phoneNumber;

    /**
     * 注册资本币种 人民币 美元 欧元 等
     */
    @Excel(name = "注册资本币种 人民币 美元 欧元 等")
    private String regCapitalCurrency;

    /**
     * 实收注册资本币种 人民币 美元 欧元 等
     */
    @Excel(name = "实收注册资本币种 人民币 美元 欧元 等")
    private String actualCapitalCurrency;

    /**
     * 法人
     */
    @Excel(name = "法人")
    private String legalPersonName;

    /**
     * 企业id
     */
    @Excel(name = "企业id")
    private Long companyId;

    /**
     * 企业邮箱
     */
    @Excel(name = "企业邮箱")
    private String companyEmail;

    /**
     * 网址
     */
    @Excel(name = "网址")
    private String websiteList;

    /**
     * 企业logo（天眼查）
     */
    private String logo;

    /**
     * 企业简称
     */
    private String alias;

    /**
     * 企业简介
     */
    private String companyProfile;

    private String queryKeyword;

    /**
     * 是否是小微企业 0不是 1是
     */
    private int isMicroEnt;

    /**
     * 国民经济行业分类门类
     */
    private String category;

    /**
     * 国民经济行业分类大类
     */
    private String categoryBig;

    /**
     * 国民经济行业分类中类
     */
    private String categoryMiddle;

    /**
     * 国民经济行业分类小类
     */
    private String categorySmall;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("companyName", getCompanyName())
                .append("creditCode", getCreditCode())
                .append("companyOrgType", getCompanyOrgType())
                .append("socialStaffNum", getSocialStaffNum())
                .append("historyNames", getHistoryNames())
                .append("regLocation", getRegLocation())
                .append("regStatus", getRegStatus())
                .append("estiblishTime", getEstiblishTime())
                .append("toTime", getToTime())
                .append("regCapital", getRegCapital())
                .append("actualCapital", getActualCapital())
                .append("taxNumber", getTaxNumber())
                .append("industry", getIndustry())
                .append("regInstitute", getRegInstitute())
                .append("englishName", getEnglishName())
                .append("percentileScore", getPercentileScore())
                .append("regNumber", getRegNumber())
                .append("orgNumber", getOrgNumber())
                .append("approvedTime", getApprovedTime())
                .append("staffNumRange", getStaffNumRange())
                .append("businessScope", getBusinessScope())
                .append("base", getBase())
                .append("phoneNumber", getPhoneNumber())
                .append("regCapitalCurrency", getRegCapitalCurrency())
                .append("actualCapitalCurrency", getActualCapitalCurrency())
                .append("legalPersonName", getLegalPersonName())
                .append("companyId", getCompanyId())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("category", getCategory())
                .append("categoryBig", getCategoryBig())
                .append("categoryMiddle", getCategoryMiddle())
                .append("categorySmall", getCategorySmall())
                .append("companyProfile", getCompanyProfile())
                .append("queryKeyword", getQueryKeyword())
                .append("logo", getLogo())
                .append("alias", getAlias())
                .toString();
    }
}
