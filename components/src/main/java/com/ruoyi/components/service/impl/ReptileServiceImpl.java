package com.ruoyi.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.components.domain.CustomerPortraitParameter;
import com.ruoyi.components.domain.ReceiveParameters.KeyWordsParams;
import com.ruoyi.components.domain.RtbReport;
import com.ruoyi.components.service.*;
import com.ruoyi.components.utils.PutBidUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
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
        //获取客户单位的工商信息
        iCusPersonaCompanyBusinessService.addCompanyBusinessByTycCompanyId(
                customerPortraitParameter.getTycCompanyInfo(), customerPortraitParameter.getUserName(),
                customerPortraitParameter.getCompany(),
                customerPortraitParameter.getEnterpriseType());
        //调用天眼查获取客户单位关系信息
        iCusPersonaCompanyNodeService.addCorporateRelationsByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位对外投资信息（处理好的数据，含有对外地区投资和行业投资）
        iCusPersonaAreaInvestService.addInvestByTycCompany(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位产品信息
        iCusPersonaCompanyProductService.addProductByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位证书信息
        iCusPersonaCompanyCertService.addCertByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位的客户数据
        iCusPersonaCompanyCustomerService.addCompanyCustomerByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位的供应商数据
        iCusPersonaCompanySupplierService.addSupplierByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位司法解析数据
        iCusPersonaJudicialCaseService.addJudicialCaseByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位开庭公告数据
        iCusPersonaCourtNoticeService.addTycCourtNoticeByCompany(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取客户单位法律诉讼数据
        iCusPersonaLegalService.addTycLegalByCompany(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //调用天眼查获取企业年报信息
        iCusPersonaAnnualReportsService.addAnnualReportsByTyc(
                customerPortraitParameter.getTycCompanyInfo(),
                customerPortraitParameter.getUserName());
        //获取招投标数据
        iPersonaBidsService.savePurchaserPersonaBidsByCompany(customerPortraitParameter.getUserName());
    }

    @Override
    public String[] keyToLevel(KeyWordsParams keyWordsParams) {
        if (ObjectUtils.isEmpty(keyWordsParams)) {
            return null;
        }
        String[] keyword = null;
        if (CollectionUtils.isNotEmpty(keyWordsParams.getBidTitles())
                && CollectionUtils.isNotEmpty(keyWordsParams.getContracts())
                && StringUtils.isNotEmpty(keyWordsParams.getBusiness())) {
            //封装数据
            Map<String, Object> map = new HashMap<>();
            map.put("id", 99999);
            map.put("level-p1", "");
            map.put("level-p2", keyWordsParams.getBidTitles().toArray(new String[0]));
            map.put("level-p3", keyWordsParams.getContracts().toArray(new String[0]));
            map.put("level-p4", StringUtils.nvl(keyWordsParams.getBusiness(), ""));
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
    public String[] getVisitKeyWord(KeyWordsParams keyWordsParams) {
        JSONObject data = new JSONObject();
        if (CollectionUtils.isNotEmpty(keyWordsParams.getBidTitles())
                && CollectionUtils.isNotEmpty(keyWordsParams.getContracts())
                && StringUtils.isNotEmpty(keyWordsParams.getBusiness())) {
            //封装数据
            /**
             * "level-p2":"招投标标题",
             * "level-p3":"业绩合同名称",
             * "level-p4":"经营范围" ,  */
            Map<String, Object> map = new HashMap<>();
            map.put("id", keyWordsParams.getBaseId());
            map.put("level-p1", "");
            map.put("level-p2", keyWordsParams.getBidTitles().toArray(new String[0]));
            map.put("level-p3", keyWordsParams.getContracts().toArray(new String[0]));
            map.put("level-p4", StringUtils.nvl(keyWordsParams.getBusiness(), ""));
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
        if (ObjectUtils.isNotEmpty(data))
            if (CollectionUtils.isNotEmpty(data.getJSONArray("level-p2"))) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p2"));
            } else if (CollectionUtils.isNotEmpty(data.getJSONArray("level-p3"))) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p3"));
            } else if (CollectionUtils.isNotEmpty(data.getJSONArray("level-p4"))) {
                keyword = StringUtils.JsonToArray(data.getJSONArray("level-p4"));
            }
        return keyword;
    }

    @Override
    public JSONObject getReportKeyword(KeyWordsParams keyWordsParams) {
        JSONObject data = new JSONObject();
        RtbReport info = keyWordsParams.getInfo();
        if (StringUtils.isNotNull(info)) {
            String itemName = info.getItemName();//项目名称
            String content = info.getContent();//建设内容

            //封装数据
            Map<String, Object> map = new HashMap<>();
            map.put("id", keyWordsParams.getBaseId());
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
            if ("200".equals(code) && CollectionUtils.isNotEmpty(lists)) {
                for (int i = 0; i < lists.size(); i++) {
                    data = (JSONObject) lists.get(i);
                }
            }
        }
        return data;
    }
}
