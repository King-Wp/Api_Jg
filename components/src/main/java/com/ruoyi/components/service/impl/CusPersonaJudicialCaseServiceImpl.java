package com.ruoyi.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.components.domain.CusPersonaJudicialCase;
import com.ruoyi.components.mapper.CusPersonaJudicialCaseMapper;
import com.ruoyi.components.service.ICusPersonaJudicialCaseService;
import com.ruoyi.components.utils.HttpApiUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.ruoyi.common.enums.UrlAddressEnum.TOKEN_API;

/**
 * 客户画像司法解析Service业务层处理
 * 
 * @author wucilong
 * @date 2022-09-08
 */
@Service
public class CusPersonaJudicialCaseServiceImpl implements ICusPersonaJudicialCaseService
{
    private static final Logger logger = LoggerFactory.getLogger(CusPersonaJudicialCaseServiceImpl.class);

    @Resource
    private CusPersonaJudicialCaseMapper cusPersonaJudicialCaseMapper;

    /**
     * 调用天眼查接口，将司法解析数据入库
     *
     * @param customerBusinessVo company 客户单位名称 companyId 天眼查公司ID
     */
    @Override
    public Integer addJudicialCaseByTyc(String companyId,String companyName, String userName) {
        //新增数量
        int num = 0;
        //请求页码
        int pageNum = 0;
        //是否终止循环变量
        boolean endLoop = false;
        //循环的次数
        int loopNum = 0;

        //重连次数
        int reconnect = 0;
        while (!endLoop) {
            pageNum++;
            String url = "http://open.api.tianyancha.com/services/open/jr/judicialCase/2.0?pageSize=20&pageNum=" + pageNum + "&id=" + companyId;
            try {
                String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());
                JSONObject resultObj = JSONObject.parseObject(result);
                String code = resultObj.getString("error_code");
                //判断异常信息
                if (!"0".equals(code)) {
                    break;
                }
                //获取不到数据，退出本次循环
                String totalString = resultObj.getJSONObject("result").getString("total");
                if (totalString == null) {
                    endLoop = true;
                }
                int total = Integer.parseInt(totalString);
                //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                loopNum = total / 20 + 1;
                //设置最多循环次数，避免出现过多请求接口导致收费过多
                if (pageNum > 100) {
                    endLoop = true;
                } else if (pageNum <= loopNum) {
                    //获取最后一页，获取完后结循环
                    if (pageNum == loopNum) {
                        endLoop = true;
                    }
                    JSONArray lists = resultObj.getJSONObject("result").getJSONArray("items");

                    if (ObjectUtils.isNotEmpty(lists)) {
                        for (Object object : lists) {
                            JSONObject temp = JSONObject.parseObject(object.toString());
                            CusPersonaJudicialCase judicial = new CusPersonaJudicialCase();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            //以逗号连接添加数组到case_identity字段中
                            JSONArray caseIdentity = temp.getJSONArray("caseIdentity");
                            StringBuilder identityString = new StringBuilder();
                            for (int j = 0 ; j < caseIdentity.size(); j++){
                                if (j == caseIdentity.size() - 1){
                                    identityString.append(caseIdentity.get(j).toString());
                                }
                                else {
                                    identityString.append(caseIdentity.get(j).toString()).append(",");
                                }
                            }
                            judicial.setCaseIdentity(identityString.toString());
                            judicial.setCaseCode(temp.getString("caseCode"));
                            judicial.setCaseReason(temp.getString("caseReason"));
                            judicial.setCaseTitle(temp.getString("caseTitle"));
                            judicial.setCaseType(temp.getString("caseType"));
                            judicial.setTrialProcedure(temp.getString("trialProcedure"));
                            judicial.setCompanyName(companyName);
                            judicial.setTrialTime(simpleDateFormat.parse(temp.getString("trialTime")));
                            judicial.setCreateBy(userName);
                            judicial.setCreateTime(DateUtils.getNowDate());
                            num = num + cusPersonaJudicialCaseMapper.insertCusPersonaJudicialCase(judicial);
                        }
                    }
                } else {
                    endLoop = true;
                }

            } catch (RuntimeException e) {
                //将pageNum 减一，继续访问该页面
                e.printStackTrace();
                if (reconnect < 2) {
                    pageNum = pageNum - 1;
                    endLoop = false;
                    reconnect++;
                } else {
                    endLoop = true;
                    reconnect = 0;
                }
            } catch (ParseException e) {
                endLoop = true;
                reconnect = 0;
                e.printStackTrace();
            }
        }
        logger.info("{}入库{}条司法解析记录", companyName, num);
        return num;
    }

    @Override
    public AjaxResult addJudicialCase(String companyName) {
        //调用天眼查-新增入库
        String url = "http://open.api.tianyancha.com/services/open/jr/judicialCase/2.0?pageSize=20&pageNum=1&keyword=" + companyName;

        //获取查询数据
        String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());

        logger.info("接口请求返回参数：{}", result);
        //处理返回参数
        JSONObject resultObj = JSONObject.parseObject(result);
        String code = resultObj.getString("error_code");

        if ("300000".equals(code)) {
            return AjaxResult.error(500, "无数据");
        } else if ("300001".equals(code)) {
            return AjaxResult.error(500, "请求失败");
        } else if ("300002".equals(code)) {
            return AjaxResult.error(500, "账号失效");
        } else if ("300003".equals(code)) {
            return AjaxResult.error(500, "账号过期");
        } else if ("300004".equals(code)) {
            return AjaxResult.error(500, "访问频率过快");
        } else if ("300005".equals(code)) {
            return AjaxResult.error(500, "无权限访问此api");
        } else if ("300006".equals(code)) {
            return AjaxResult.error(500, "余额不足");
        } else if ("300007".equals(code)) {
            return AjaxResult.error(500, "剩余次数不足");
        } else if ("300008".equals(code)) {
            return AjaxResult.error(500, "缺少必要参数");
        } else if ("300009".equals(code)) {
            return AjaxResult.error(500, "账号信息有误");
        } else if ("3000010".equals(code)) {
            return AjaxResult.error(500, "URL不存在");
        } else if ("3000011".equals(code)) {
            return AjaxResult.error(500, "此IP无权限访问此api");
        } else if ("3000012".equals(code)) {
            return AjaxResult.error(500, "报告生成中");
        }

        if (StringUtils.isEmpty(result)) {
            return AjaxResult.error(500, "请求用户信息接口为空，请联系系统管理员进行处理。");
        }
        //添加首次调用获取的数据
        List<CusPersonaJudicialCase> list = new ArrayList<>(insertFirstJudicialCase(resultObj, companyName));

        int num = 0;
        if (CollectionUtils.isNotEmpty(list)) {
            num = cusPersonaJudicialCaseMapper.saveBatchJudicialCase(list);
        }
        return AjaxResult.success(num);
    }


    private List<CusPersonaJudicialCase> insertFirstJudicialCase(JSONObject jsonObject, String companyName) {
        //司法解析列表
        ArrayList<CusPersonaJudicialCase> judicialCaseList = new ArrayList<>();
        JSONArray lists = jsonObject.getJSONObject("result").getJSONArray("items");
        if (ObjectUtils.isNotEmpty(lists)) {
            for (Object object : lists) {
                JSONObject temp = JSONObject.parseObject(object.toString());
                CusPersonaJudicialCase judicial = new CusPersonaJudicialCase();

                //以逗号连接添加数组到case_identity字段中
                if (temp.getJSONArray("caseIdentity").size() > 0) {
                    StringBuffer identityString = new StringBuffer();
                    for (Object s : temp.getJSONArray("caseIdentity")) {
                        identityString.append(s.toString());
                        identityString.append(",");
                    }
                    identityString.deleteCharAt(identityString.length() - 1);
                    judicial.setCaseIdentity(identityString.toString());
                }
                judicial.setCaseCode(temp.getString("caseCode"));
                judicial.setCaseReason(temp.getString("caseReason"));
                judicial.setCaseTitle(temp.getString("caseTitle"));
                judicial.setCaseType(temp.getString("caseType"));
                judicial.setTrialProcedure(temp.getString("trialProcedure"));
                judicial.setCompanyName(companyName);

                judicialCaseList.add(judicial);

            }
        }
        return judicialCaseList;
    }
}
