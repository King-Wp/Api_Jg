package com.ruoyi.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.components.domain.CustomerPortraitParameter;
import com.ruoyi.components.domain.RtbReportP;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import com.ruoyi.components.service.*;
import com.ruoyi.components.utils.PutBidUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: 11653
 * @createTime: 2024/03/21 15:05
 * @package: com.ruoyi.system.service.impl
 * @description:
 */
@Service
public class ReptileServiceImpl implements IReptileService {

    private static final String KEYWORD_URL = "http://10.203.7.251:19002/CKE_out_results";

    @Resource
    private ICusPersonaCompanyBusinessService iCusPersonaCompanyBusinessService;
    @Resource
    private ICusPersonaCompanyNodeService iCusPersonaCompanyNodeService;
    @Resource
    private ICusPersonaAreaInvestService iCusPersonaAreaInvestService;
    @Resource
    private ICusPersonaCompanyProductService iCusPersonaCompanyProductService;
    @Resource
    private ICusPersonaCompanyCertService iCusPersonaCompanyCertService;
    @Resource
    private ICusPersonaCompanyCustomerService iCusPersonaCompanyCustomerService;
    @Resource
    private ICusPersonaCompanySupplierService iCusPersonaCompanySupplierService;
    @Resource
    private ICusPersonaJudicialCaseService iCusPersonaJudicialCaseService;
    @Resource
    private ICusPersonaCourtNoticeService iCusPersonaCourtNoticeService;
    @Resource
    private ICusPersonaLegalService iCusPersonaLegalService;
    @Resource
    private ICusPersonaAnnualReportsService iCusPersonaAnnualReportsService;
    @Resource
    private IPersonaBidsService iPersonaBidsService;

    @Override
    public void addCustomerReportRemind(CustomerPortraitParameter customerPortraitParameter) {
        try {

            //TODO: 2024/3/21 根据名称获取天眼查公司名称、公司ID

            //更新客户单位画像信息
            CustomerBusinessVo tycCompanyInfo = new CustomerBusinessVo();
            tycCompanyInfo.setCompany(customerPortraitParameter.getFullCompany());
            tycCompanyInfo.setCompanyId(customerPortraitParameter.getCompanyId());

            //获取客户单位的工商信息
            iCusPersonaCompanyBusinessService.addCompanyBusinessByTycCompanyId(
                    tycCompanyInfo, customerPortraitParameter.getUserName(),
                    customerPortraitParameter.getCompany(),
                    customerPortraitParameter.getEnterpriseType());
            //调用天眼查获取客户单位关系信息
            iCusPersonaCompanyNodeService.addCorporateRelationsByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位对外投资信息（处理好的数据，含有对外地区投资和行业投资）
            iCusPersonaAreaInvestService.addInvestByTycCompany(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位产品信息
            iCusPersonaCompanyProductService.addProductByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位证书信息
            iCusPersonaCompanyCertService.addCertByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位的客户数据
            iCusPersonaCompanyCustomerService.addCompanyCustomerByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位的供应商数据
            iCusPersonaCompanySupplierService.addSupplierByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位司法解析数据
            iCusPersonaJudicialCaseService.addJudicialCaseByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位开庭公告数据
            iCusPersonaCourtNoticeService.addTycCourtNoticeByCompany(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取客户单位法律诉讼数据
            iCusPersonaLegalService.addTycLegalByCompany(tycCompanyInfo, customerPortraitParameter.getUserName());
            //调用天眼查获取企业年报信息
            iCusPersonaAnnualReportsService.addAnnualReportsByTyc(tycCompanyInfo, customerPortraitParameter.getUserName());
            //获取招投标数据
            iPersonaBidsService.savePurchaserPersonaBidsByCompany(customerPortraitParameter.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] keyToLevel(String company,List<String> bidTitles,List<String>contracts,String business) {
        String[] keyword = null;
        if (!(bidTitles.size() == 0 && contracts.size() == 0 && business == null)) {
            //封装数据
            Map<String, Object> map = new HashMap<>();
            map.put("id", 99999);
            map.put("level-p1", "");
            map.put("level-p2", bidTitles.toArray(new String[bidTitles.size()]));
            map.put("level-p3", contracts.toArray(new String[contracts.size()]));
            map.put("level-p4", com.ruoyi.common.utils.StringUtils.nvl(business, ""));
            map.put("level-p5", "");
            JSONArray arrs = new JSONArray();
            arrs.add(map);

            //提取关键词
            String result = PutBidUtils.doPost(KEYWORD_URL, arrs.toJSONString());
            JSONObject resultObj = JSONObject.parseObject(result);
            String code = (String) resultObj.get("code");
            JSONArray lists = resultObj.getJSONArray("data");
            JSONObject data = new JSONObject();
            if ("200".equals(code) && lists.size() > 0) {
                data = (JSONObject) lists.get(0);
            }
            if (data.getJSONArray("level-p2").size() > 0) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p2"));
            } else if (data.getJSONArray("level-p3").size() > 0) {
                keyword = com.ruoyi.common.utils.StringUtils.JsonToArray(data.getJSONArray("level-p3"));
            } else if (data.getJSONArray("level-p4").size() > 0) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p4"));
            }
        }
        return keyword;
    }

    @Override
    public String[] getVisitKeyword(Long baseId, String company, List<String> bidTitles, List<String> contracts, String business) {
        JSONObject data = new JSONObject();
        if (!(bidTitles.size() == 0 && contracts.size() == 0 && business == null)) {
            //封装数据
            /**
             * "level-p2":"招投标标题",
             * "level-p3":"业绩合同名称",
             * "level-p4":"经营范围" ,  */
            Map<String, Object> map = new HashMap<>();
            map.put("id", baseId);
            map.put("level-p1", "");
            map.put("level-p2", bidTitles.toArray(new String[bidTitles.size()]));
            map.put("level-p3", contracts.toArray(new String[contracts.size()]));
            map.put("level-p4", StringUtils.nvl(business, ""));
            map.put("level-p5", "");
            JSONArray arrs = new JSONArray();
            arrs.add(map);

            //提取关键词
            String result = PutBidUtils.doPost(KEYWORD_URL, arrs.toJSONString());
            JSONObject resultObj = JSONObject.parseObject(result);
            String code = (String) resultObj.get("code");
            JSONArray lists = resultObj.getJSONArray("data");
            if ("200".equals(code) && lists.size() > 0) {
                for (int i = 0; i < lists.size(); i++) {
                    data = (JSONObject) lists.get(i);
                }
            }
        }
        String[] keyword = null;
        if (data.size() > 0)
            if (data.getJSONArray("level-p2").size() > 0) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p2"));
            } else if (data.getJSONArray("level-p3").size() > 0) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p3"));
            } else if (data.getJSONArray("level-p4").size() > 0) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p4"));
            }
        return keyword;
    }

    @Override
    public JSONObject getReportKeyword(Long baseId, RtbReportP info) {
        JSONObject data = new JSONObject();
        if (StringUtils.isNotNull(info)) {
            String itemName = info.getItemName();//项目名称
            String content = info.getContent();//建设内容

            //封装数据
            Map<String, Object> map = new HashMap<>();
            map.put("id", baseId);
            map.put("level-p1", StringUtils.nvl(itemName, ""));
            map.put("level-p2", StringUtils.nvl(content, ""));
            map.put("level-p3", "");
            map.put("level-p4", "");
            map.put("level-p5", "");
            JSONArray arrs = new JSONArray();
            arrs.add(map);

            //提取关键词
            String result = PutBidUtils.doPost(KEYWORD_URL, arrs.toJSONString());
            JSONObject resultObj = JSONObject.parseObject(result);
            String code = (String) resultObj.get("code");
            JSONArray lists = resultObj.getJSONArray("data");
            if ("200".equals(code) && lists.size() > 0) {
                for (int i = 0; i < lists.size(); i++) {
                    data = (JSONObject) lists.get(i);
                }
            }
        }
        return data;
    }
}
