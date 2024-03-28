package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.HttpApiUtils;
import com.ruoyi.system.domain.CusPersonaLegal;
import com.ruoyi.system.domain.vo.CustomerBusinessVo;
import com.ruoyi.system.mapper.CusPersonaLegalMapper;
import com.ruoyi.system.service.ICusPersonaLegalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 司法解析Service业务层处理
 * 
 * @author wucilong
 * @date 2022-12-21
 */
@Service
public class CusPersonaLegalServiceImpl implements ICusPersonaLegalService
{
    @Autowired
    private CusPersonaLegalMapper cusPersonaLegalMapper;

    private static final Logger logger = LoggerFactory.getLogger(CusPersonaLegalServiceImpl.class);

    /**
     * 入库天眼查法律诉讼信息
     * @param customerBusinessVo 需入库的公司信息
     * @return 插入结果
     */
    @Async
    @Override
    public void addTycLegalByCompany(CustomerBusinessVo customerBusinessVo, String userName) {
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
            String url = "http://open.api.tianyancha.com/services/open/jr/lawSuit/3.0?pageSize=20&pageNum=" + pageNum + "&keyword=" + companyId;
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
                if (pageNum > 30) {
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
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            CusPersonaLegal cusPersonaLegal = new CusPersonaLegal();
                            cusPersonaLegal.setCompanyId(companyId);
                            cusPersonaLegal.setCompanyName(companyName);
                            cusPersonaLegal.setCaseMoney(temp.getString("caseMoney"));
                            cusPersonaLegal.setCaseReason(temp.getString("caseReason"));
                            cusPersonaLegal.setCaseNo(temp.getString("caseNo"));
                            cusPersonaLegal.setCaseType(temp.getString("caseType"));
                            cusPersonaLegal.setJudgeTime(simpleDateFormat.parse(temp.getString("judgeTime")));
                            cusPersonaLegal.setCourt(temp.getString("court"));
                            cusPersonaLegal.setDocType(temp.getString("docType"));
                            cusPersonaLegal.setTitle(temp.getString("title"));
                            cusPersonaLegal.setCourtUuid(temp.getString("uuid"));
                            cusPersonaLegal.setLawsuitUrl(temp.getString("lawsuitUrl"));
                            cusPersonaLegal.setLawsuitH5Url(temp.getString("lawsuitH5Url"));
                            JSONArray casePersons = temp.getJSONArray("casePersons");
                            for (int i = 0; i < casePersons.size(); i++){
                                JSONObject casePerson = casePersons.getJSONObject(i);
                                //获取涉案方为查询公司的信息
                                if(companyId.equals(casePerson.getString("gid"))){
                                    cusPersonaLegal.setCaseResult(casePerson.getString("result"));
                                    cusPersonaLegal.setCaseRole(casePerson.getString("role"));
                                    cusPersonaLegal.setCaseEmotion(casePerson.getString("emotion"));
                                }
                            }
                            cusPersonaLegal.setCreateTime(DateUtils.getNowDate());
                            cusPersonaLegal.setCreateBy(userName);
                            num = num + cusPersonaLegalMapper.insertCusPersonaLegal(cusPersonaLegal);
                        }
                    }
                } else {
                    endLoop = true;
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                //将pageNum 减一，继续访问该页面
                if (reconnect < 5) {
                    pageNum = pageNum - 1;
                    endLoop = false;
                    reconnect++;
                } else {
                    endLoop = true;
                    reconnect = 0;
                }
            }
            catch (ParseException e) {
                endLoop = true;
                reconnect = 0;
                e.printStackTrace();
            }
        }
        logger.info(companyName + "入库" + num + "条法律诉讼记录");
    }
}
