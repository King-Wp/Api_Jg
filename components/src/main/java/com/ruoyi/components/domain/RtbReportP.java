package com.ruoyi.components.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.Type;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 商机报备对象 rtb_report
 *
 * @author ruoyi
 * @date 2020-12-16
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class RtbReportP extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 商机报备id
     */
    private Long reportId;

    /**
     * 用户主键Id
     */
    private Long userId;

    /**
     * 部门ID
     */
//    @Excel(name = "部门编号", type = Type.IMPORT)
    private Long deptId;

    /**
     * 部门名称
     */
    @Excel(name = "记录部门", sort = 4, type = Type.EXPORT)
    private String deptName;

    /*short_dept_name*/
    private String shortDeptName;

    /**
     * 分公司名称
     */
//    @Excel(name = "分公司名称", type = Type.IMPORT)
    private String parentName;

    /**
     * 项目名称
     */
    @Excel(name = "项目名称", sort = 3, width = 50)
    private String itemName;

    /**
     * 项目编号
     */
    @Excel(name = "项目编号(若有)", sort = 3, width = 50)
    private String itemCode;

    /**
     * 项目性质
     */
    @Excel(name = "项目性质", sort = 9)
    private String itemProperty;

    /**
     * 预计合同规模（万元）
     */
    @Excel(name = "预计项目规模(万元)", sort = 6)
    private String itemValue;

    /**
     * 一级专业类型
     */
    @Excel(name = "一级专业类型", sort = 10)
    private String firstType;

    /**
     * 二级专业类型
     */
    //@ApiModelProperty("二级专业类型(新增必填)")
//    @Excel(name = "二级专业类型",sort = 11)
    private String secondType;

    @Excel(name = "关键赛道类型", sort = 12)
    private String trackType;

    /**
     * 省份
     */
    @Excel(name = "省份", sort = 13)
    private String province;

    /**
     * 城市
     */
    @Excel(name = "城市", sort = 14)
    private String city;

    /**
     * 客户类型
     */
    @Excel(name = "客户类型", sort = 8, readConverterExp = "1=政务,2=政法,3=应急住建,4=工业,5=JD（军队）,6=交通物流,7=金融,8=文旅,9=互联网,10=电力,11=卫健,12=教育,13=电信,14=电信外运营商")
    private String clientType;

    /**
     * 投标时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预计投标时间", width = 15, dateFormat = "yyyy年MM月dd日", sort = 15)
    private Date bidTime;

    /**
     * 主要工作内容
     */
    @Excel(name = "项目建设内容", sort = 16)
    private String content;

    /**
     * 审批状态
     */
    @Excel(name = "审批状态", sort = 1, readConverterExp = "0=待审批,1=初审通过,2=审批通过,3=未通过,4=已过期,5=商机结束", type = Type.EXPORT)
    private String checkStatus;

    /**
     * 审批人
     */
    @Excel(name = "审批人", type = Type.EXPORT)
    private String checkBy;

    /**
     * 审批意见
     */
    @Excel(name = "审批意见", type = Type.EXPORT)
    private String checkOpinions;

    /**
     * 审批时间
     */
    @Excel(name = "审批时间", type = Type.EXPORT)
    private String checkTime;

    /**
     * 商机状态
     */
    @Excel(name = "商机状态", sort = 17)
    private String businessStaus;

    /**
     * 投标金额
     */
    @Excel(name = "投标金额(万元)", sort = 18)
    private String bidAmount;

    /**
     * 项目负责人
     */
    @Excel(name = "项目负责人", sort = 19)
    private String itemLeader;

    /**
     * 联系方式
     */
    @Excel(name = "联系方式", sort = 20)
    private String contact;

    /**
     * 是否对其他专业公司公示(1是，0否)
     */
    private String isPublicity;

    /**
     * 是否中标
     */
    private String isWinbid;

    /**
     * 是否冲突（1黄色为部门内部冲突，2红色为跨公司冲突）
     */
    private String isConflict;

    /**
     * 冲突id
     */
    private String conflictId;

    /**
     * 中标公司
     */
    @Excel(name = "中标公司", sort = 21)
    private String winbidOrg;

    /**
     * 中标金额
     */
    @Excel(name = "中标金额(万元)", sort = 22)
    private String winbidAmount;

    /**
     * 业主单位名称
     */
    @Excel(name = "业主单位名称", sort = 7)
    private String ownerOrg;


    /**
     * 业主客户源公司
     */
    private String initialDept;

    //@Excel(name = "商机编号", type = Type.EXPORT)
    private String reportCode;

    /**
     * 创建者
     */
    @Excel(name = "记录人", sort = 5, type = Type.EXPORT)
    private String createBy;

    /**
     * 创建时间
     */
    @Excel(name = "记录时间", sort = 2, dateFormat = "yyyy年MM月dd日", type = Type.EXPORT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 备注
     */
    @Excel(name = "商机跟进记录")
    private String remark;

    /**
     * 查询参数
     */
    private Integer itemValueBefor;

    private Integer itemValueAfter;

    private String keyword;

    // 祖级列表
    private String ancestors;

    // 是否是部门
    private int isDept;

    // 商机数量
    private int count;

    // 预计合同规模总数
    private double sum;

    private Long[] deptIds;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 排序
     */
    private String order;

    private String prop;

    private List<RtbReportP> children = new ArrayList<>();

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setProp(String prop) {
        if ("checkStatus".equals(prop)) {
            prop = "check_status";
        }
        if ("itemName".equals(prop)) {
            prop = "CONVERT(item_name using gbk)";
        }
        if ("itemProperty".equals(prop)) {
            prop = "CONVERT(item_property using gbk)";
        }
        if ("itemValue".equals(prop)) {
            prop = "item_value-0";
        }
        if ("firstType".equals(prop)) {
            prop = "CONVERT(first_type using gbk)";
        }
        if ("secondType".equals(prop)) {
            prop = "CONVERT(second_type using gbk)";
        }
        if ("bidTime".equals(prop)) {
            prop = "bid_time";
        }
        if ("businessStaus".equals(prop)) {
            prop = "business_staus";
        }
        if ("bidAmount".equals(prop)) {
            prop = "bid_amount-0";
        }
        if ("itemLeader".equals(prop)) {
            prop = "CONVERT(item_leader using gbk)";
        }
        if ("winbidOrg".equals(prop)) {
            prop = "CONVERT(winbid_org using gbk)";
        }
        if ("winbidAmount".equals(prop)) {
            prop = "winbid_amount-0";
        }
        if ("ownerOrg".equals(prop)) {
            prop = "CONVERT(owner_org using gbk)";
        }
        if ("createBy".equals(prop)) {
            prop = "CONVERT(u.nick_name using gbk)";
        }
        if ("clientType".equals(prop)) {
            prop = "CONVERT(client_type using gbk)";
        }
        if ("deptName".equals(prop)) {
            prop = "CONVERT(d.dept_name using gbk)";
        }
        if ("createTime".equals(prop)) {
            prop = "r.create_time";
        }
        if ("reportCode".equals(prop)) {
            prop = "CONVERT(report_code using gbk)";
        }

        this.prop = prop;
    }

    @Override
    public String toString() {
        return "RtbReport{" + "reportId=" + reportId + ", userId=" + userId + ", deptId=" + deptId + ", deptName='" + deptName + '\'' + ", shortDeptName='" + shortDeptName + '\'' + ", parentName='" + parentName + '\'' + ", itemName='" + itemName + '\'' + ", itemCode='" + itemCode + '\'' + ", itemProperty='" + itemProperty + '\'' + ", itemValue='" + itemValue + '\'' + ", firstType='" + firstType + '\'' + ", secondType='" + secondType + '\'' + ", province='" + province + '\'' + ", city='" + city + '\'' + ", clientType='" + clientType + '\'' + ", bidTime=" + bidTime + ", content='" + content + '\'' + ", checkStatus='" + checkStatus + '\'' + ", checkBy='" + checkBy + '\'' + ", checkOpinions='" + checkOpinions + '\'' + ", checkTime='" + checkTime + '\'' + ", businessStaus='" + businessStaus + '\'' + ", bidAmount='" + bidAmount + '\'' + ", itemLeader='" + itemLeader + '\'' + ", contact='" + contact + '\'' + ", isPublicity='" + isPublicity + '\'' + ", isWinbid='" + isWinbid + '\'' + ", isConflict='" + isConflict + '\'' + ", conflictId='" + conflictId + '\'' + ", winbidOrg='" + winbidOrg + '\'' + ", winbidAmount='" + winbidAmount + '\'' + ", ownerOrg='" + ownerOrg + '\'' + ", initialDept='" + initialDept + '\'' + ", reportCode='" + reportCode + '\'' + ", createBy='" + createBy + '\'' + ", createTime=" + createTime + ", remark='" + remark + '\'' + ", itemValueBefor=" + itemValueBefor + ", itemValueAfter=" + itemValueAfter + ", keyword='" + keyword + '\'' + ", ancestors='" + ancestors + '\'' + ", isDept=" + isDept + ", count=" + count + ", sum=" + sum + ", deptIds=" + Arrays.toString(deptIds) + ", beginTime='" + beginTime + '\'' + ", endTime='" + endTime + '\'' + ", order='" + order + '\'' + ", prop='" + prop + '\'' + ", children=" + children + '}';
    }
}
