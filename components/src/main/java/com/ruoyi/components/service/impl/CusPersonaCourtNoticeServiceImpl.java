package com.ruoyi.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.components.domain.CusPersonaCourtNotice;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import com.ruoyi.components.mapper.CusPersonaCourtNoticeMapper;
import com.ruoyi.components.service.ICusPersonaCourtNoticeService;
import com.ruoyi.components.utils.HttpApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.ruoyi.common.enums.UrlAddressEnum.TOKEN_API;

/**
 * 开庭公告Service业务层处理
 * 
 * @author wucilong
 * @date 2022-12-21
 */
@Service
public class CusPersonaCourtNoticeServiceImpl implements ICusPersonaCourtNoticeService
{
    @Resource
    private CusPersonaCourtNoticeMapper cusPersonaCourtNoticeMapper;

    private static final Logger logger = LoggerFactory.getLogger(CusPersonaCourtNoticeServiceImpl.class);

    @Async
    @Override
    public void addTycCourtNoticeByCompany(CustomerBusinessVo customerBusinessVo, String userName) {
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
            String url = "http://open.api.tianyancha.com/services/open/jr/ktannouncement/2.0?pageSize=20&pageNum=" + pageNum + "&keyword=" + companyId;
            try {
                String result = HttpApiUtils.executeGet(url, TOKEN_API.getVal());
                JSONObject resultObj = JSONObject.parseObject(result);
                String code = resultObj.getString("error_code");
                List<CusPersonaCourtNotice> courtNoticeList = new ArrayList<>();
                //判断异常信息
                if (!"0".equals(code)) {
                    break;
                }
                //获取不到数据，退出本次循环
                String totalString = resultObj.getJSONObject("result").getString("total");
                if (totalString == null) {
                    continue;
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
                            CusPersonaCourtNotice cusPersonaCourtNotice = new CusPersonaCourtNotice();
                            cusPersonaCourtNotice.setNoticeId(temp.getString("id"));
                            cusPersonaCourtNotice.setCompanyId(companyId);
                            cusPersonaCourtNotice.setCompanyName(companyName);
                            cusPersonaCourtNotice.setCourt(temp.getString("court"));
                            cusPersonaCourtNotice.setLitigant(temp.getString("litigant"));
                            cusPersonaCourtNotice.setCaseNo(temp.getString("caseNo"));
                            cusPersonaCourtNotice.setJudge(temp.getString("judge"));
                            cusPersonaCourtNotice.setContractors(temp.getString("contractors"));
                            cusPersonaCourtNotice.setCaseReason(temp.getString("caseReason"));
                            cusPersonaCourtNotice.setStartDate(DateUtils.dateToStamp(Long.valueOf(temp.getString("startDate"))));
                            JSONArray plaintiffs = temp.getJSONArray("plaintiff");
                            //拼接原告字符串
                            StringBuilder plaintiffString = new StringBuilder();
                            for (int i = 0; i < plaintiffs.size(); i++){
                                JSONObject plaintiffsJSONObject = plaintiffs.getJSONObject(i);
                                if (i == plaintiffs.size() - 1){
                                    plaintiffString.append(plaintiffsJSONObject.getString("name"));
                                }
                                else {
                                    plaintiffString.append(plaintiffsJSONObject.getString("name")).append(",");
                                }
//                                plaintiffString.append(plaintiffsJSONObject.getString("name"));
                            }
                            cusPersonaCourtNotice.setPlaintiff(plaintiffString.toString());

                            //拼接被告字符串
                            StringBuilder defendantString = new StringBuilder();
                            JSONArray defendants = temp.getJSONArray("defendant");
                            for (int j = 0 ; j < defendants.size(); j++){
                                JSONObject defendantsJSONObject = defendants.getJSONObject(j);
                                if (j == defendants.size() - 1){
                                    defendantString.append(defendantsJSONObject.getString("name"));
                                }
                                else {
                                    defendantString.append(defendantsJSONObject.getString("name")).append(",");
                                }
                            }
                            cusPersonaCourtNotice.setDefendant(defendantString.toString());
                            cusPersonaCourtNotice.setCourtroom(temp.getString("courtroom"));
                            cusPersonaCourtNotice.setCreateTime(DateUtils.getNowDate());
                            cusPersonaCourtNotice.setCreateBy(userName);
                            cusPersonaCourtNotice.setCompanyName(companyName);
                            courtNoticeList.add(cusPersonaCourtNotice);
//                            num = num + cusPersonaCourtNoticeMapper.insertCusPersonaCourtNotice(cusPersonaCourtNotice);
                        }
                    }
                } else {
                    endLoop = true;
                }
                for (CusPersonaCourtNotice courtNotice : courtNoticeList){
                    num = num + cusPersonaCourtNoticeMapper.insertCusPersonaCourtNotice(courtNotice);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                //将pageNum 减一，继续访问该页面
                if (reconnect < 2) {
                    pageNum = pageNum - 1;
                    endLoop = false;
                    reconnect++;
                } else {
                    endLoop = true;
                    reconnect = 0;
                }
            }
        }
        logger.info(companyName + "入库" + num + "条开庭报告记录");
    }

}
