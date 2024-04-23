package com.ruoyi.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.components.domain.CusPersonaPunishmentAdministrative;
import com.ruoyi.components.mapper.CusPersonaPunishmentAdministrativeMapper;
import com.ruoyi.components.service.ICusPersonaPunishmentAdministrativeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 行政处罚Service业务层处理
 * 
 * @author wucilong
 * @date 2023-02-09
 */
@Service
public class CusPersonaPunishmentAdministrativeServiceImpl implements ICusPersonaPunishmentAdministrativeService
{
    @Resource
    private CusPersonaPunishmentAdministrativeMapper cusPersonaPunishmentAdministrativeMapper;

    /**
     * 通过天眼查获取企业行政处罚信息（非收费接口）
     * @param customerBusinessVo
     * @param userName
     * @return
     */
    @Override
    public int addPunishmentAdministrativeByTyc(String companyId,String companyName, String userName) {
        //新增数量
        int num = 0;
        //调用天眼查-新增入库
        //请求页码
        int pageNum = 0;
        //是否终止循环变量
        boolean endLoop = false;
        //循环的次数
        int loopNum = 0;

        //重连次数
        int reconnect = 0;

        ArrayList<CusPersonaPunishmentAdministrative> punishmentAdministrativeList = new ArrayList<>();

        while (!endLoop) {
            pageNum++;
            try {
                long timeFromDate = new Date().getTime();
                String url = "https://capi.tianyancha.com/cloud-operating-risk/operating/punishment/punishIndexList?withOwner=0&_="+timeFromDate+"&pageSize=20&pageNum="+pageNum+"&gid="+companyId;
                String s = sendTycGet(url);
                JSONObject jsonObject = JSONObject.parseObject(s);
                if (jsonObject != null) {
                    //获取数据总数
                    String realTotal = jsonObject.getJSONObject("data").getString("totalCount");
                    if (realTotal == null) {
                        endLoop = true;
                    }
                    int total = Integer.parseInt(realTotal);
                    //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                    loopNum = total / 20 + 1;
                    //设置最多循环次数，避免出现过多请求接口导致收费过多
                    if (pageNum > 1000) {
                        endLoop = true;
                    } else if (pageNum <= loopNum) {
                        //如果是最后一页，获取完后结循环
                        if (pageNum == loopNum) {
                            endLoop = true;
                        }
                        JSONArray courtAnnouncements = jsonObject.getJSONObject("data").getJSONArray("list");
                        JSONObject temp = new JSONObject();
                        if (courtAnnouncements.size() > 0) {
                            for (Object object : courtAnnouncements) {
                                try {
                                    temp = JSONObject.parseObject(object.toString());
                                    CusPersonaPunishmentAdministrative punishmentAdministrative = new CusPersonaPunishmentAdministrative();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    punishmentAdministrative.setCompanyName(companyName);
                                    punishmentAdministrative.setCompanyId(companyId);
                                    punishmentAdministrative.setPunishNumber(temp.getString("punishNumber"));
                                    punishmentAdministrative.setPunishStatus(temp.getString(""));
                                    String punishContent = temp.getString("punishContent");
                                    //如果内容长度过长则不进行入库
                                    if (punishContent.length() < 500){
                                        punishmentAdministrative.setContent(temp.getString("punishContent"));
                                    }
                                    if (!(temp.getString("punishDate") == null || "".equals(temp.getString("punishDate")))){
                                        punishmentAdministrative.setDecisionDate(format.parse(temp.getString("punishDate")));
                                    }
                                    punishmentAdministrative.setReason(temp.getString("punishReason"));
                                    punishmentAdministrative.setSource(temp.getString("showTypeName"));
                                    punishmentAdministrative.setPunishAmount(temp.getString("punishAmount"));
                                    punishmentAdministrative.setCreateBy(userName);
                                    punishmentAdministrative.setCreateTime(DateUtils.getNowDate());
                                    punishmentAdministrativeList.add(punishmentAdministrative);

                                } catch (RuntimeException | ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                    } else {
                        endLoop = true;
                    }
                }
            } catch (IllegalArgumentException e) {
                //连接超时时重新爬取
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
            } catch (RuntimeException | IOException e) {
                e.printStackTrace();
            }

        }
        try {
            for (CusPersonaPunishmentAdministrative cusPersonaExecutedPerson : punishmentAdministrativeList) {
                num = num + cusPersonaPunishmentAdministrativeMapper.insertCusPersonaPunishmentAdministrative(cusPersonaExecutedPerson);
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }

        return num;
    }

    /**
     * 发送get请求获取天眼查网页信息
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String sendTycGet(String url) throws IOException {
        String result = "";
//        String urlName = url + "?" + "key=" +param + "&sessionNo=1669270915.92363742";

        URL realURL = new URL(url);
        URLConnection conn = realURL.openConnection();
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
        //TYCID（需要获取天眼查ID，可能存在访问次数限制，限制手段——汉字输入）
        //TYCID需要定时更新，目前的有效期是两年
        conn.setRequestProperty("cookie", "HWWAFSESTIME=;csrfToken=;HWWAFSESID=;TYCID=6a30f5c0664a11ed8026153652183d0b");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(5000);
        conn.connect();
        Map<String, List<String>> map = conn.getHeaderFields();
        for (String s : map.keySet()) {
            System.out.println(s + "-->" + map.get(s));
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        String line;
        while ((line = in.readLine()) != null) {
            result += "\n" + line;
        }

        return result;
    }
}
