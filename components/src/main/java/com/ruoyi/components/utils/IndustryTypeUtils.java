package com.ruoyi.components.utils;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.components.domain.IndustryType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ruoyi.components.utils.compareUtil.getSimilarityRatio;

/**
 * @author Leeyq
 * @version 1.0
 * @date 2022年12月13日 15:20
 */


public class IndustryTypeUtils {

	/**
	 * 根据客户分类、客户名称判断合同归属的行业信息
	 *
	 * @param industryTypeList   行业信息集合
	 * @param cusName            客户名称
	 * @param cusClassification1 客户分类1级
	 * @param cusClassification2 客户分类2级
	 * @param cusClassification3 客户分类3级
	 * @return 归属行业
	 */
	public static String decideIndustryType(List<IndustryType> industryTypeList, String cusName, String cusClassification1,
											String cusClassification2, String cusClassification3) {
		String result = "";
		List<String> tempIndustryType = new ArrayList<>();

		for (IndustryType it : industryTypeList) {
			String industryType = it.getIndustryType();
			String[] split = it.getCusTypeContent().split("、");
			for (String s : split) {
				// 判断客户分类3级
				if (cusClassification3.equals(s) || cusClassification2.equals(s) || cusClassification1.equals(s)) {
					tempIndustryType.add(industryType);
				}
//				// 判断客户分类2级
//				if (cusClassification2.equals(s)) {
//					tempIndustryType.add(industryType);
//				}
//				// 判断客户分类1级
//				if (cusClassification1.equals(s)) {
//					tempIndustryType.add(industryType);
//				}
			}
		}
		// 除去重复的行业
		List<String> newList = tempIndustryType.stream().distinct().collect(Collectors.toList());
		//处理返回结果
		if (newList.size() == 0) {
			//客户类型匹配不上行业, 用客户名称匹配
			newList = CusIndustryType(industryTypeList, cusName);
//			return result;
		}
		if (newList.size() == 1) {
			return newList.get(0);
		}

		return compareIndustryTypeSimilarity(industryTypeList, newList, cusName);
	}


	/**
	 * 客户类型匹配不上行业类型, 用客户名称匹配
	 *
	 * @param industryTypeList 行业类型集合
	 * @param cusName          客户名称
	 * @return 行业类型
	 */
	public static List<String> CusIndustryType(List<IndustryType> industryTypeList, String cusName) {
		// 存放行业类型中对应的关键词字符串
		List<String> temp = new ArrayList<>();
		for (IndustryType it : industryTypeList) {
			String industryType = it.getIndustryType();
			String[] split = it.getCusNameContent().split("、");
			for (String s : split) {
				if (cusName.contains(s)) {
					temp.add(industryType);
				}
			}
		}
		// 除去重复的行业
		List<String> newList = temp.stream().distinct().collect(Collectors.toList());
		return newList;
	}

	/**
	 * 根据客户名称中的关键字比较与行业类型的相似度并返回相似度最高的行业类型
	 *
	 * @param industryTypeList 行业类型集合
	 * @param list 初步命中的行业类型集合
	 * @param cusName 客户名称
	 * @return 行业类型
	 */
	public static String compareIndustryTypeSimilarity(List<IndustryType> industryTypeList, List<String> list, String cusName) {
		// 存放行业类型中对应的关键词字符串
		Map<String, String> temp = new HashMap<>();
		for (IndustryType it : industryTypeList) {
			for (String str : list) {
				if (str.equals(it.getIndustryType())) {
					temp.put(it.getIndustryType(), it.getCusNameContent());
				}
			}
		}

		String result = "";
		// 记录最大相似度
		float max = 0;
		for (String str : temp.keySet()) {
			String[] split = temp.get(str).split("、");
			for (String s : split) {
				float similarityRatio = getSimilarityRatio(cusName, s);
				if (similarityRatio > max) {
					result = str;
					max = similarityRatio;
				}
			}
		}

		// 特殊
		if (result.equals("")) {
			for (IndustryType it : industryTypeList) {
				String industryType = it.getIndustryType();
				String[] split = it.getCusNameContent().split("、");
				for (String str : split) {
					if (cusName.equals(str)) {
						result = industryType;
					}
				}
			}
		}
//		if (cusName.equals("九〇八六单位")) {
//			result = "JD（军队）";
//		}
		return result;
	}

	/**
	 * 根据行业类型名称获取行业类型编号
	 * @param industryTypeList 行业类型集合
	 * @param industryType 行业类型名称
	 * @return 行业类型编号
	 */
	public static String getIndustryNum(List<IndustryType> industryTypeList, String industryType) {
		String industryNum = "";
		for (IndustryType it : industryTypeList) {
			if (it.getIndustryType().equals(industryType)) {
				return it.getIndustryNum().toString();
			}
		}
		return industryNum;
	}

	//根据客户分类, 客户名称判断, 返回包含行业类型---招投标匹配行业类型关键字（bidKeywordFirst优先级：1）
	public static String decideIndustryType(List<IndustryType> lists, String content){
		String result ="";
		List<String> industrys =  new ArrayList<>();
		//查询行业类型
		lists.forEach(industryType -> {
			String type = industryType.getIndustryType();
			//拆分关键词
			String[] split =industryType.getBidKeywordFirst().split("、");
			int sum =0;
			for (String str : split){
				//判断是否包含
				if (content.contains(str)){
					sum++;
				}
			}
			//包含关键词, 则添加
			if (sum>0){
				industrys.add(type);
			}
		});

		//处理返回结果
		if (industrys.size()>0){
			String[] types = industrys.toArray(new String[industrys.size()]);
			for (String string : types) {
				result +=string+",";
			}
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}

	//根据客户分类, 客户名称判断, 返回包含行业类型---招投标匹配行业类型关键字（bidKeywordSecond优先级：2）
	public static String judgeIndustryType(List<IndustryType> lists, String content){
		String result ="";
		List<String> industrys =  new ArrayList<>();
		//查询行业类型
		List<IndustryType> filters = lists.stream().filter(list-> StringUtils.isNotEmpty(list.getBidKeywordSecond())).collect(Collectors.toList());
		filters.forEach(industryType -> {
			String type = industryType.getIndustryType();
			//拆分关键词
			String[] split =industryType.getBidKeywordSecond().split("、");
			int sum =0;
			for (String str : split){
				//判断是否包含
				if (content.contains(str)){
					sum++;
				}
			}
			//包含关键词, 则添加
			if (sum>0){
				industrys.add(type);
			}
		});

		//处理返回结果
		if (industrys.size()>0){
			String[] types = industrys.toArray(new String[industrys.size()]);
			for (String string : types) {
				result +=string+",";
			}
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}
}