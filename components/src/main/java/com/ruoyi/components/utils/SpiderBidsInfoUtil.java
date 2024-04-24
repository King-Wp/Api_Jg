package com.ruoyi.components.utils;

import com.ruoyi.common.utils.BidStringUtils;
import com.ruoyi.common.utils.SnowflakeIdWorker;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.UserAgentUtil;
import com.ruoyi.components.domain.IndustryType;
import com.ruoyi.components.domain.PersonaBids;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class SpiderBidsInfoUtil {

    //招投标网站详情地址（需爬取数据的地址）
    private static final String DETAIL_URL = "DOCPUBURL";
    //招投标公共类型
    private static final String ANNOUNCEMENT_TYPE = "chnldesc";

    /**
     * 处理页面数据，获取中标人和中标金额等信息
     * @param element 主页面html对象
     * @return
     */
    private static String bidDataProcess(Elements element){
        Elements tables = element.select("table");
        //确定中标金额和中标单位所在的表格
        confirmBidTable(tables);


        System.out.println(tables);
        return "";
    }

    /**
     * 确认中标信息所在的表格
     * @param tableData 表格数据
     * @return 是否存在结果
     */
    private static Element confirmBidTable(Elements tableData){
        //粗略筛选符合存在中标信息的表格数量
        int roughNum = 0;
        //详细筛选符合存在中标信息的表格数量
        int detailNum = 0;
        //需再次筛选的表格
        Elements screenAgainTable = new Elements();
        //存在中标金额及中标单位的表格
        Element existBidTable = null;

        for (int i = 0; i < tableData.size(); i++){
            String text = tableData.get(i).text();
            if(text.contains("中 标 人") || text.contains("中 标 价") ||
                    text.contains("中标人") || text.contains("金额") || text.contains("中标（成交）金额(元)") ||
                    text.contains("中标供应商名称") || text.contains("中标供应商")){
                roughNum++;
                screenAgainTable.add(tableData.get(i));
            }
        }
        if (roughNum > 1){

            //粗略筛选存在多个个表格数据，则需详细筛选（详细筛选命中率较低），筛选条件通过查找广西壮族自治区公告资源交易中心的中标数据分析得出
            for (int j = 0; j < screenAgainTable.size(); j++){
                String againText = tableData.get(j).text();
                if((againText.contains("中 标 人") && againText.contains("中 标 价")) ||
                        (againText.contains("中标人") && againText.contains("金额")) ||
                        (againText.contains("中标（成交）金额") || againText.contains("中标供应商名称")) ||
                        (againText.contains("中标供应商") && againText.contains("中标服务费金额"))){
                    detailNum++;
                }
            }
            if(detailNum > 1){
                //先获取第一个表格避免出现异常
                existBidTable = screenAgainTable.get(0);
                System.out.println("=========== 表格筛选条件出错，请查看该数据并更新筛选条件 ==========");
            }
        }

        return existBidTable;
    }

    //测试爬取一条广西公共资源交易中心招投标数据
    public static List<PersonaBids> addOneGXPublicResources(List<IndustryType> industryType) throws IOException {

        List<PersonaBids> personaBidsList = new ArrayList<>();

        try {
            //http://gxggzy.gxzf.gov.cn/jyfw/jyfw_zfcg/zfcg_gkzb/gkzb_zbgg/t12965484.shtml
            //http://gxggzy.gxzf.gov.cn/jyfw/jyfw_gcjs/jtgc/zbjggs_43228/t12909199.shtml
            //http://gxggzy.gxzf.gov.cn/jyfw/jyfw_zfcg/zfcg_gkzb/gkzb_zbgg/t10784671.shtml
            //http://gxggzy.gxzf.gov.cn/jyfw/jyfw_gcjs/fjsz/zbjggs_43211/t10383455.shtml
            //http://gxggzy.gxzf.gov.cn/jyfw/jyfw_gcjs/jtgc/zbjggs_43228/t13172526.shtml
            //http://gxggzy.gxzf.gov.cn/jyfw/jyfw_gcjs/tlgc/zbjggs/t7326940.shtml
            //http://gxggzy.gxzf.gov.cn/jyfw/jyfw_zfcg/zfcg_gkzb/gkzb_zbgg/t4631256.shtml
            //http://gxggzy.gxzf.gov.cn/jyfw/jyfw_zfcg/zfcg_gkzb/gkzb_zbgg/t4631286.shtml
            //http://gxggzy.gxzf.gov.cn/jyfw/jyfw_gcjs/jtgc/zbjggs_43228/t12976532.shtml
            String docPubUrl = "http://gxggzy.gxzf.gov.cn/jyfw/jyfw_gcjs/jtgc/zbjggs_43228/t12976532.shtml";
            Document doc = null;
            // 利用jsoup连接目标url网页获取整个html对象
            doc = Jsoup.connect(docPubUrl).userAgent(UserAgentUtil.getUserAgent()).get();
            //防止频繁访问（模拟网络延迟）
//                            Thread.sleep(500);
            if (doc != null) {

                //获取主页面的HTML对象
                Elements mainHtml = doc.select("div[class^=ewb-page-main]");
                //获取主页面的字符
                String mainContent = doc.select("div[class^=ewb-page-main]").text();
                //获取存在文本中的中标信息
                List<TableElement> tableElemts = DataTableUtil
                        .getFitElement(doc);
                List<PersonaBids> personaBids = null;
                //表中存在中标信息
                if (tableElemts != null && tableElemts.size() > 0) {
                    personaBids = TableUtil
                            .extractPropertyInfos(tableElemts);
                    if (personaBids != null && personaBids.size() > 0) {
                        for (PersonaBids personaBidsInfo : personaBids) {
                            //获取招标人名称
//                            personaBidsInfo.setPurchaser(DataTableUtil.bidPurchaserHandle(mainContent));
                            //获取招标人名称
                            if (!StringUtils.isNotEmpty(personaBidsInfo.getPurchaser())){
                                String purchaser = "";
                                for (int i = 0; i < BidConstants.PURCHASER_PROPERTIES.length; i++){

//                                    if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(mainContent, "受", "的委托"))){
//                                        purchaser = BidStringUtils.subStringBetween(mainContent, "受", "的委托");
//                                        personaBidsInfo.setPurchaser(purchaser);
//                                    } else if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(mainContent, "受", "委托"))){
//                                        purchaser = BidStringUtils.subStringBetween(mainContent, "受", "委托");
//                                        personaBidsInfo.setPurchaser(purchaser);
//                                    } else {
                                        //hasPurchaserLine：含有招标人的行
                                        String hasPurchaserLine = BidStringUtils.subStringBetween(mainContent, BidConstants.PURCHASER_PROPERTIES[i], " ");
                                        if(StringUtils.isNotEmpty(hasPurchaserLine) && hasPurchaserLine.contains("，")){
                                            purchaser = BidStringUtils.subEndString(hasPurchaserLine, "，");
                                            if (purchaser.contains("；")){
                                                purchaser = BidStringUtils.subEndString(purchaser,"；");
                                                personaBidsInfo.setPurchaser(purchaser.replace("（公章）","").replace("（盖章）","").replace("（盖章）", "").trim());
                                            }else {
                                                personaBidsInfo.setPurchaser(purchaser.replace("（公章）","").replace("（盖章）","").replace("（盖章）", "").trim());

                                            }

                                        }else if (StringUtils.isNotEmpty(hasPurchaserLine)){
                                            personaBidsInfo.setPurchaser(hasPurchaserLine.replace("（公章）","").replace("（盖章）","").replace("（盖章）", "").trim());
                                        }
//                                    }
                                }
                                if (StringUtils.isEmpty(personaBidsInfo.getPurchaser())){
                                    if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(mainContent, "受", "的委托"))){
                                        purchaser = BidStringUtils.subStringBetween(mainContent, "受", "的委托");
                                        personaBidsInfo.setPurchaser(purchaser);
                                    } else if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(mainContent, "受", "委托"))){
                                        purchaser = BidStringUtils.subStringBetween(mainContent, "受", "委托");
                                        personaBidsInfo.setPurchaser(purchaser);
                                    }
                                }

                            }
                            // 使用雪花算法生成id
                            long id = SnowflakeIdWorker.generateId();
                            personaBidsInfo.setUuid(String.valueOf(id));
//                            personaBidsInfo.setSource(doc.select("span[class ^= ewb-details-source]").text());
                            //爬取的网站为广西公共资源交易中心
                            personaBidsInfo.setProvince("广西");

                            personaBidsInfo.setIndustryType(IndustryTypeUtils.decideIndustryType(industryType,mainContent));
                            personaBidsInfo.setSource("http://gxggzy.gxzf.gov.cn");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
                        }
                        if (personaBids != null){
                            personaBidsList.addAll(personaBids);
                        }
                    }
                } else {
                    //表中不存在中标信息，则中标信息存在于文本中
                    List<PersonaBids> bidWinningInfoList = DataTableUtil.getBidWinningInfo(mainContent);
//                                    String s = BidStringUtils.subStringBetween(mainContent, "中标金额：", " ");
                    for (PersonaBids personaBidsInfo : bidWinningInfoList){
                        // 使用雪花算法生成id
                        long id = SnowflakeIdWorker.generateId();
                        personaBidsInfo.setUuid(String.valueOf(id));
                        //爬取的网站为广西公共资源交易中心
                        personaBidsInfo.setProvince("广西");
//                        personaBidsInfo.setSource(doc.select("span[class ^= ewb-details-source]").text());
                        personaBidsInfo.setSource("http://gxggzy.gxzf.gov.cn");
//                        personaBidsInfo.setContent(mainContent);
                        personaBidsInfo.setIndustryType(IndustryTypeUtils.decideIndustryType(industryType,mainContent));
                    }
                    if (bidWinningInfoList != null){
                        personaBidsList.addAll(bidWinningInfoList);
                    }
                }



            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        for (int i = 0; i < personaBidsList.size(); i++){
            System.out.println(personaBidsList.get(i));
        }
        return personaBidsList;
    }
}
