package com.comp.components.mapper;


import com.comp.components.domain.CusPersonaCompanyBusiness;
import com.comp.components.domain.vo.PersonaVoCopy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公司详情工商信息Mapper接口
 *
 * @author wucilong
 * @date 2022-09-05
 */
public interface CusPersonaCompanyBusinessMapper
{
    /**
     * 查询公司详情工商信息
     *
     * @param id 公司详情工商信息ID
     * @return 公司详情工商信息
     */
    public CusPersonaCompanyBusiness selectCusPersonaCompanyBusinessById(Long id);

    /**
     * 查询公司详情工商信息列表
     *
     * @param cusPersonaCompanyBusiness 公司详情工商信息
     * @return 公司详情工商信息集合
     */
    public List<CusPersonaCompanyBusiness> selectCusPersonaCompanyBusinessList(CusPersonaCompanyBusiness cusPersonaCompanyBusiness);

    /**
     * 通过公司名称查询公司详情工商信息列表
     *
     * @param companyName 公司详情工商信息
     * @return 公司详情工商信息集合
     */
    public CusPersonaCompanyBusiness selectBusinessByCompanyName(String companyName);

    /**
     * 新增公司详情工商信息
     *
     * @param cusPersonaCompanyBusiness 公司详情工商信息
     * @return 结果
     */
    public int insertCusPersonaCompanyBusiness(CusPersonaCompanyBusiness cusPersonaCompanyBusiness);

    /**
     * 新增中标供应商的工商信息
     *
     * @param cusPersonaCompanyBusiness 公司详情工商信息
     * @return 结果
     */
    public int insertCusPersonaBidsBusiness(CusPersonaCompanyBusiness cusPersonaCompanyBusiness);

    /**
     * 修改公司详情工商信息
     *
     * @param cusPersonaCompanyBusiness 公司详情工商信息
     * @return 结果
     */
    public int updateCusPersonaCompanyBusiness(CusPersonaCompanyBusiness cusPersonaCompanyBusiness);

    /**
     * 删除公司详情工商信息
     *
     * @param id 公司详情工商信息ID
     * @return 结果
     */
    public int deleteCusPersonaCompanyBusinessById(Long id);

    /**
     * 批量删除公司详情工商信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCusPersonaCompanyBusinessByIds(Long[] ids);

    public int updateCompanyProfile(@Param("companyId") Long companyId, @Param("companyProfile") String companyProfile);

    /**
     * 判断是否已调用天眼查入库该CompanyId的工商信息
     * @param companyName 天眼查中的公司名称
     * @return 公司工商信息
     */
    public CusPersonaCompanyBusiness existBusinessInfo(@Param("companyName") String companyName);

    /**
     * 判断是否已调用天眼查入库该CompanyId的中标供应商的工商信息
     * @param companyName 天眼查中的公司名称
     * @return 公司工商信息
     */
    public CusPersonaCompanyBusiness existBidWinnerBusinessInfo(@Param("companyName") String companyName);

    /**
     * 查看该公司ID是否存在客户画像数据，判断依据：若客户工商信息表中存在该公司的记录则说明存在该公司的客户画像
     * @param companyId 公司名称
     * @return 客户画像对象（和客户画像列表一致）
     */
    public PersonaVoCopy existPersonaInfo(@Param("companyId") String companyId);
    /**
     * 通过祥云获取企业性质
     * @param companyName 客户单位名称
     * @return 企业性质
     */
    public String getEnterpriseTypeByXY(@Param("companyName") String companyName);

	List<CusPersonaCompanyBusiness> selectCompanyBusinessNullList();

    void completeCompanyProfile(CusPersonaCompanyBusiness info);
}
