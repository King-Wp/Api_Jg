package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.HttpApiUtils;
import com.ruoyi.system.domain.CusPersonaJudicialCase;
import com.ruoyi.system.domain.vo.CustomerBusinessVo;
import com.ruoyi.system.mapper.CusPersonaJudicialCaseMapper;
import com.ruoyi.system.service.ICusPersonaJudicialCaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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

    @Autowired
    private CusPersonaJudicialCaseMapper cusPersonaJudicialCaseMapper;

    /**
     * 调用天眼查接口，将司法解析数据入库
     *
     * @param customerBusinessVo company 客户单位名称 companyId 天眼查公司ID
     */
    @Async
    @Override
    public void addJudicialCaseByTyc(CustomerBusinessVo customerBusinessVo, String userName) {
        //新增数量
        int num = 0;

        //调用天眼查-新增入库
        String companyId = customerBusinessVo.getCompanyId();
        String companyName = customerBusinessVo.getCompany();

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
                String result = HttpApiUtils.executeGet(url);
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

                    JSONObject temp = new JSONObject();
                    if (lists.size() > 0) {
                        for (Object object : lists) {
                            temp = JSONObject.parseObject(object.toString());
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
//                            if(temp.getJSONArray("caseIdentity").size() > 0){
//                                StringBuilder identityString = new StringBuilder();
//
//                                for (Object s : temp.getJSONArray("caseIdentity")){
//                                    identityString.append(s.toString());
//                                    identityString.append(",");
//                                }
//                                //删除字符串多余的逗号
//                                identityString.deleteCharAt(identityString.length() - 1);
//                                judicial.setCaseIdentity(identityString.toString());
//                            }
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
        logger.info(companyName + "入库" + num + "条司法解析记录");
    }


}
