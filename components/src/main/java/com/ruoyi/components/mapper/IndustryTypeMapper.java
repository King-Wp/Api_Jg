package com.ruoyi.components.mapper;

import com.ruoyi.components.domain.IndustryType;

import java.util.List;

public interface IndustryTypeMapper {

	List<IndustryType> selectIndustryTypeList(IndustryType industryType);
	IndustryType selectIndustry(IndustryType industryType);

	List<IndustryType> selectIndustryTypeByIds(String[] ids);
}
