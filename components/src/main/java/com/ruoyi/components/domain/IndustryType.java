package com.ruoyi.components.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Leeyq
 * @version 1.0
 * @date 2022年12月13日 14:55
 */

@Data
public class IndustryType implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	/*序号*/
	private Integer industryNum;
	/*行业类型*/
	private String industryType;
	/*客户类型描述*/
	private String cusTypeContent;
	/*客户名称描述*/
	private String cusNameContent;

	/* 招投标匹配行业类型关键字（优先级：1）*/
	private String bidKeywordFirst;

	/* 招投标匹配行业类型关键字（优先级：2）*/
	private String bidKeywordSecond;
}