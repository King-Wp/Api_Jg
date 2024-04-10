package com.ruoyi.components.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Leeyq
 * @version 1.0
 * @date 2022年09月16日 10:41
 */

@Data
public class CustomersVo extends BaseEntity {
	/**
	 * 客户主键
	 */
	@Excel(name = "客户主键", cellType = Excel.ColumnType.NUMERIC)
	private Long id;

	/**
	 * 客户姓名
	 */
	@Excel(name = "客户姓名")
	private String name;

	/**
	 * 单位全称
	 */
	@Excel(name = "单位全称")
	private String company;

	/**
	 * 客户职位
	 */
	@Excel(name = "客户职位")
	private String post;

	/**
	 * 照片
	 */
//    @Excel(name = "照片")
	private String photo;

	/**
	 * 客户电话
	 */
	@Excel(name = "客户电话")
	private String phone;

	/**
	 * 客户类型
	 */
	@Excel(name = "客户类型", readConverterExp = "1=运营商客户,2=渠道,3=党政军,4=金融,5=能源,6=交通,7=商贸流通,8=互联网与IT传媒,9=制造业,10=中小聚类,11=水利,12=广电,13=建筑与房地产,14=军工,15=农业,16=公众用户,17=电力,18=教育")
	private String clientType;

	/**
	 * 生日
	 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Excel(name = "生日")
	private String birthday;

	/**
	 * 单位地址
	 */
	@Excel(name = "单位地址")
	private String address;

	/**
	 * 客户来源
	 */
	@Excel(name = "客户来源", readConverterExp = "8=自有客户,9=合作伙伴,7=其他")
	private Integer source;

	/**
	 * 跟进状态（1已解决 0未解决）
	 */
//    @Excel(name = "跟进状态", readConverterExp = "1=已解决,0=未解决")
	private Integer state;

	/**
	 * 备注
	 */
	@Excel(name = "备注")
	private String remark;

	private String keyword;

	private Long deptId;

	private Long userId;

	//客户资源ID(客户管理和资源平台的客户资源存在两张表中，合并两个表时可能出现两个cusId一样的情况，故需要对Id进行处理)
	private String cusId;

	/**
	 * 记录人
	 */
	private String userName;

	/**
	 * 记录分公司
	 */
	private String deptName;

	/**
	 * 记录母公司
	 */
	private String parentName;

	private String checkStatus;

	private List<CustomersVo> children;

	private String isAgree;

	/** 是否关注(0未关注,1重点关注) */
	private Long isAttent;

	/**
	 * 客户层级:  1-高层，2-中层，3-普通员工
	 */
	private String cusRelation;

	/**
	 * 开始时间
	 */
	private String beginTime;

	/**
	 * 结束时间
	 */
	private String endTime;
	/** 天眼查公司ID*/
	private String companyId;
	/** 天眼查公司name*/
	private String companyName;
	/** 天眼查公司相似度*/
	private float sameRatio;

	private String industryType;

}
