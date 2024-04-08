package com.comp.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.comp.components.domain.CusPersonaAbnormalOperation;
import com.comp.components.domain.CusPersonaChattelMortgage;
import com.comp.components.domain.TyCompany;
import com.comp.components.domain.vo.CustomerBusinessVo;
import com.comp.components.exception.CustomException;
import com.comp.components.mapper.CusPersonaAbnormalOperationMapper;
import com.comp.components.mapper.CusPersonaChattelMortgageMapper;
import com.comp.components.service.ApiThirdPartyService;
import com.comp.components.utils.PutBidUtils;
import com.comp.components.utils.compareUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.HttpApiUtils;
import com.ruoyi.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author: 11653
 * @createTime: 2024/04/07 16:58
 * @package: com.comp.components.service.impl
 * @description:
 */
@Slf4j
@Service
public class ApiThirdPartyServiceImpl implements ApiThirdPartyService {

    @Resource
    private CusPersonaAbnormalOperationMapper cusPersonaAbnormalOperationMapper;
    @Resource
    private CusPersonaChattelMortgageMapper cusPersonaChattelMortgageMapper;

    @Override
    public List<TyCompany> selectCompanyListByTyc(String keyword, Integer pageNum) {
        List<TyCompany> companies = new ArrayList<>();
        //关键词查询天眼查公司列表(单位全称)
        String token ="6999f69e-60ab-4d88-8867-c5554915d36e"; //单价token
        String url ="http://open.api.tianyancha.com/services/open/search/2.0?word="+keyword+"&pageSize=20&pageNum="+pageNum;
        String result = HttpApiUtils.executeGet(url, token);
        //处理数据
        JSONObject resultObj = JSONObject.parseObject(result);
        if ("0".equals(resultObj.getString("error_code"))){
            //查询企业列表
            JSONArray lists=resultObj.getJSONObject("result").getJSONArray("items");
            if (lists.size()>0) {
                for (Object object : lists) {
                    JSONObject temp = JSONObject.parseObject(object.toString());
                    String matchtype =temp.getString("matchType");
                    String regstatus =temp.getString("regStatus");
                    String name =temp.getString("name");

                    //匹配度筛选
                    String[] strs = PutBidUtils.doKeyPost(keyword);
                    Boolean isMatch = StringUtils.containsWordsWithAC(name,strs);
                    if("公司名称匹配".equals(matchtype) && !"注销".equals(regstatus))  {
                        TyCompany cy = new TyCompany();
                        cy.setRegnumber(temp.getString("regnumber"));//注册号
                        cy.setRegstatus(regstatus);//经营状态
                        cy.setCreditcode(temp.getString("creditCode"));//统一社会信用代码
                        cy.setEstiblishtime(temp.getString("estiblishTime"));//成立日期
                        cy.setRegcapital(temp.getString("regCapital"));//注册资本
                        cy.setCompanytype(Long.valueOf(temp.getString("companyType")));//机构类型-1：公司；2：香港企业；3：社会组织；4：律所；5：事业单位；6：基金会；7-不存在法人、注册资本、统一社会信用代码、经营状态;8：台湾企业；9-新机构
                        cy.setName(name);//公司名称
                        cy.setCompanyId(Long.valueOf(temp.getString("id")));//公司id
                        cy.setOrgnumber(temp.getString("orgNumber"));//组织机构代码
                        cy.setType(Long.valueOf(temp.getString("type")));//1-公司 2-人
                        cy.setBase(temp.getString("base"));//省份
                        cy.setLegalpersonname(temp.getString("legalPersonName"));//法人
                        cy.setMatchtype(matchtype);//匹配原因
                        cy.setSameRatio(compareUtil.getSimilarityRatio(keyword,name));//匹配相似度
                        cy.setIsMatch(isMatch);//是否匹配关键词
                        companies.add(cy);
                    }
                }
                //排序
                companies.sort(Comparator.comparing(TyCompany::getSameRatio).reversed());//倒序
            }
        }else{
            throw new CustomException("第三方查询服务异常，请联系系统管理员。");
        }

        return companies;
    }

    @Override
    public int addAbnormalOperationByTyc(CustomerBusinessVo customerBusinessVo, String userName) {
        //新增数量
        int num = 0;
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
            //将访问的页数加一
            pageNum++;
            String token = "7b1f73a2-3709-4be1-ae59-6536d47aae1b";
            String url = "http://open.api.tianyancha.com/services/open/mr/abnormal/2.0?pageSize=20&keyword="+companyId+"&pageNum="+pageNum;
            JSONObject resultObj = null;
            try {
                String result = HttpApiUtils.executeGet(url, token);
                resultObj = JSONObject.parseObject(result);
            } catch (RuntimeException e) {
                //返回空时解析Json会出现异常，则将pageNum 减一，继续访问该页面
                e.printStackTrace();
                if (reconnect < 2) {
                    pageNum = pageNum - 1;
                    reconnect++;
                } else {
                    //重新访问超过一定次数则退出循环
                    endLoop = true;
                    reconnect = 0;
                }
            }
            if (resultObj != null){
                String code = resultObj.getString("error_code");
                //判断异常信息
                if ("0".equals(code)) {
                    String totalString = resultObj.getJSONObject("result").getString("total");
                    if (totalString == null) {
                        //获取不到数据，退出本次循环
                        break;
                    }
                    int total = Integer.parseInt(totalString);
                    //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                    loopNum = total / 20 + 1;
                    //设置最多循环次数，避免出现过多请求接口导致收费过多
                    if (pageNum > 5) {
                        endLoop = true;
                    } else if (pageNum <= loopNum) {
                        //获取最后一页，获取完后结束循环
                        if (pageNum == loopNum) {
                            endLoop = true;
                        }
                        JSONArray lists = resultObj.getJSONObject("result").getJSONArray("items");

                        JSONObject temp = new JSONObject();
                        if (lists.size() > 0) {
                            for (Object object : lists) {
                                try {
                                    temp = JSONObject.parseObject(object.toString());
                                    CusPersonaAbnormalOperation abnormalOperation = new CusPersonaAbnormalOperation();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    abnormalOperation.setCompanyName(companyName);
                                    abnormalOperation.setCompanyId(companyId);
                                    if (!(temp.getString("putDate") == null || "".equals(temp.getString("putDate")))){
                                        abnormalOperation.setPutDate(format.parse(temp.getString("putDate")));
                                    }

                                    abnormalOperation.setPutDepartment(temp.getString("putDepartment"));
                                    abnormalOperation.setPutReason(temp.getString("putReason"));
                                    if (!(temp.getString("removeDate") == null || "".equals(temp.getString("removeDate")))){
                                        abnormalOperation.setRemoveDate(format.parse(temp.getString("removeDate")));
                                    }
                                    abnormalOperation.setRemoveReason(temp.getString("removeReason"));
                                    abnormalOperation.setCreateBy(userName);
                                    abnormalOperation.setCreateTime(DateUtils.getNowDate());
                                    num = num + cusPersonaAbnormalOperationMapper.insertCusPersonaAbnormalOperation(abnormalOperation);

                                }catch (RuntimeException | ParseException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        endLoop = true;
                    }
                }else if ("300004".equals(code) || "300001".equals(code)){
                    //若频繁访问或访问失败，则重新访问该页面数据
                    if (reconnect < 2) {
                        pageNum = pageNum - 1;
                        reconnect++;
                    } else {
                        //重新访问超过一定次数则退出循环
                        endLoop = true;
                        reconnect = 0;
                    }
                }else {
                    //其他原因则退出循环
                    break;
                }
            }
        }
        return num;
    }

    @Override
    public int addChattelMortgageByTyc(CustomerBusinessVo customerBusinessVo, String userName) {
        //新增数量
        int num = 0;
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
            //将访问的页数加一
            pageNum++;
            String token = "7b1f73a2-3709-4be1-ae59-6536d47aae1b";
            String url = "http://open.api.tianyancha.com/services/open/mr/mortgageInfo/2.0?pageSize=20&keyword="+companyId+"&pageNum="+pageNum;
            JSONObject resultObj = null;
            try {
                String result = HttpApiUtils.executeGet(url, token);
                resultObj = JSONObject.parseObject(result);
            } catch (RuntimeException e) {
                //返回空时解析Json会出现异常，则将pageNum 减一，继续访问该页面
                e.printStackTrace();
                if (reconnect < 2) {
                    pageNum = pageNum - 1;
                    reconnect++;
                } else {
                    //重新访问超过一定次数则退出循环
                    endLoop = true;
                    reconnect = 0;
                }
            }
            if (resultObj != null){
                String code = resultObj.getString("error_code");
                //判断异常信息
                if ("0".equals(code)) {
                    String totalString = resultObj.getJSONObject("result").getString("total");
                    if (totalString == null) {
                        //获取不到数据，退出本次循环
                        break;
                    }
                    int total = Integer.parseInt(totalString);
                    //total/20后得到整数部分，小数部分不会四舍五入，需+1以获取最后一页的数据
                    loopNum = total / 20 + 1;
                    //设置最多循环次数，避免出现过多请求接口导致收费过多
                    if (pageNum > 5) {
                        endLoop = true;
                    } else if (pageNum <= loopNum) {
                        //获取最后一页，获取完后结束循环
                        if (pageNum == loopNum) {
                            endLoop = true;
                        }
                        JSONArray lists = resultObj.getJSONObject("result").getJSONArray("items");

                        JSONObject temp = new JSONObject();
                        if (lists.size() > 0) {
                            for (Object object : lists) {
                                try {
                                    temp = JSONObject.parseObject(object.toString()).getJSONObject("baseInfo");
                                    CusPersonaChattelMortgage chattelMortgage = new CusPersonaChattelMortgage();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    chattelMortgage.setCompanyId(companyId);
                                    chattelMortgage.setCompanyName(companyName);
                                    if (!(temp.getString("regDate") == null || "".equals(temp.getString("regDate")))){
                                        chattelMortgage.setRegDate(format.parse(temp.getString("regDate")));
                                    }
                                    chattelMortgage.setAmount(temp.getString("amount"));
                                    if (!(temp.getString("publishDate") == null || "".equals(temp.getString("publishDate")))){
                                        chattelMortgage.setPublishDate(DateUtils.dateToStamp(Long.valueOf(temp.getString("publishDate"))));
                                    }
                                    chattelMortgage.setRegDepartment(temp.getString("regDepartment"));
                                    chattelMortgage.setTerm(temp.getString("term"));
                                    chattelMortgage.setRegNum(temp.getString("regNum"));
                                    chattelMortgage.setType(temp.getString("type"));
                                    chattelMortgage.setCreateBy(userName);
                                    chattelMortgage.setCreateTime(DateUtils.getNowDate());

                                    num = num + cusPersonaChattelMortgageMapper.insertCusPersonaChattelMortgage(chattelMortgage);

                                }catch (RuntimeException | ParseException e){
                                    e.printStackTrace();
                                }

                            }
                        }
                    } else {
                        endLoop = true;
                    }
                }else if ("300004".equals(code) || "300001".equals(code)){
                    //若频繁访问或访问失败，则重新访问该页面数据
                    if (reconnect < 2) {
                        pageNum = pageNum - 1;
                        reconnect++;
                    } else {
                        //重新访问超过一定次数则退出循环
                        endLoop = true;
                        reconnect = 0;
                    }
                }else {
                    //其他原因则退出循环
                    break;
                }

            }

        }
        return num;
    }
}
