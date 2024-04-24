package com.ruoyi.components.utils;

import com.ruoyi.common.utils.BidStringUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.components.domain.PersonaBids;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//获取数据
public class DataTableUtil {
    private final static int NUM = 2;
    private final static char NO_SUB_BID_TITLE = '0';

    //要抓取的表格可能出现的属性名
    static String[] Propertys = {"中标价","中标人","金额","中标成交金额","中标金额","成交金额","中标供应商名称","中标供应商","包件号","包件名称","标段","包件","标段号","分标号", "中标单位", "合同号", "中标单位名称"};

    private static final String SERVICE_CHARGE = "服务费";
    /**
     * 找到适合的装数据的TABle（取最里面的table进入isValueElement方法检测是不是我们需要的table）
     *
     * @param document
     * @return
     */
    public static List<TableElement> getFitElement(Document document) {
        if (Propertys != null) {
            Element element = document.getElementsByTag("body").get(0);
            List<TableElement> fitElments = new ArrayList<TableElement>();
            Elements tableElements = element.getElementsByTag("table");
            if (tableElements != null && tableElements.size() > 0) {
                for (int i = 0; i < tableElements.size(); i++) {
                    Element tableElement = tableElements.get(i);
                    //判断表格中是否仍有表格，若有，则忽略最外层的表格
                    Elements ces = tableElement.getElementsByTag("table");
                    if (ces != null && ces.size() > 1) {
                    } else {
                        TableElement te;
                        if ((te = isValueElement(Propertys,tableElement)) != null) {
                            fitElments.add(te);
                        }
                    }
                }
            } else {
                return null;
            }
            return fitElments;
        }
        return null;
    }

    //找出属性名所在的行,把属性名所在的行之上的信息去掉
    //ri为属性名所在行的结尾索引
    //row为属性名所在行占的行数
    //ri-row为属性名所在行的开始索引,这里取属性名所在行到表格的结尾行
    private static Element removeRedundance(String[] Propertys,
                                            Element element) {
        Elements tres = element.getElementsByTag("tr");
        Element trElement = tres.get(0);
        Elements tde = trElement.getElementsByTag("td");
        int row = 1;
        for (Element tdElement : tde) {
            String attribute = tdElement.attr("rowspan");
            if (attribute != null && !attribute.equals("")) {
                int rowSpan = Integer.valueOf(attribute);
                if (rowSpan > row) {
                    row = rowSpan;
                }
            }
        }
        List<Element> elements = new ArrayList<Element>();
        for (int i = 0; i < row; i++) {
            elements.add(tres.get(i));
        }
        int ri = 0;
        while (!isValueElements(Propertys, elements)) {
            elements = new ArrayList<Element>();
            row = 1;
            Elements tdes = tres.get(ri).getElementsByTag("td");
            for (Element tdElement : tdes) {
                String attribute = tdElement.attr("rowspan");
                if (attribute != null && !attribute.equals("")) {
                    int rowSpan = Integer.valueOf(attribute);
                    if (rowSpan > row) {
                        row = rowSpan;
                    }
                }
            }
            for (int i = 0; i < row; i++) {
                elements.add(tres.get(ri + i));
            }
            ri = ri + row;
        }
        if (ri > 0) {
            Elements trs = element.getElementsByTag("tr");
            int size = trs.size();
            //获取属性所在行的列数量，若下一行的列数量同属性所在行的列数量不同，则可判断为该行也不是所需属性下的值
            int attributeSize = trs.get(ri-row).childrenSize();
            Element newElement = new Element(Tag.valueOf("table"), "table");
            for (int i = ri-row; i < size; i++) {
                if (attributeSize == trs.get(i).childrenSize()){
                    newElement.appendChild(trs.get(i));
                }else {
                    //不符合条件则退出循环
                    break;
                }
            }
            return newElement;
        }
        return element;
    }

    private static boolean isValueElements(
            String[] Propertys, List<Element> trElements) {
        int index = 0;
        int size = trElements.size();
        for (int i = 0; i < size; i++) {
            List<Element> propertyElements = new ArrayList<Element>();
            Element element = trElements.get(i);
            Elements tdElements = element.getElementsByTag("td");
            Elements thElements = element.getElementsByTag("th");
            if(thElements != null && thElements.size() > 0){
                for(Element thelement : thElements){
                    propertyElements.add(thelement);
                }
            }
            if(tdElements != null && tdElements.size() > 0){
                for(Element tdelement : tdElements){
                    propertyElements.add(tdelement);
                }
            }
            for (Element tdElement : propertyElements) {
                String text = tdElement.text();
                if (!text.trim().equals("")) {
                    String value = adjuestmentParm(text);
                    if (value != null) {
                        value = BidStringUtils.parseString(value);
                        double max = 0.0d;
                        for (int j = 0; j < Propertys.length; j++) {
                            double temp = SimFeatureUtil.sim(Propertys[j], value);
                            if (temp > max) {
                                max = temp;
                            }
                        }
                        if (max >= 0.6) {
                            index++;
                        }
                    }
                }
            }
        }
        if (index >= NUM) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean regual(String reg, String string) {
        Pattern pattern = Pattern.compile(reg);
        Matcher ma = pattern.matcher(string);
        if (ma.find()) {
            return true;
        }
        return false;
    }

    //清除获取的信息标题上的单位及一些字符提高匹配的相似度
    public static String adjuestmentParm(String parm) {
        if (regual("\\(", parm) && regual("\\)",parm)) {
            parm = BidStringUtils.parseString(parm);
        }
        if (regual("（", parm) && regual("）", parm)) {
            parm = BidStringUtils.parseString(parm);
        }
        if (regual("万元", parm)) {
            parm = parm.substring(0, parm.indexOf("万元"));
        }
        if (regual("元", parm)) {
            parm = parm.substring(0, parm.indexOf("元"));
        }
        return parm;
    }

    //取出表格中的td或者th单元格的值进行相似度比较
    //index记录相似值大于0.6的值个数
    //consist记录连续不相似个数
    //当连续不相似的个数达到10个时 就break 跳出循环（这一策略不适用）
    //consistFlag记录连续相似个数
    //当index个数大于等于我们设定的个数时说明该table是包含我们要获取数据的table
    //当consistFlag大于2时说明表格内容是竖向的 setCross为false
    //进入removeRedundance方法检查是否table中的前几行(tr)不是以标题行开始,把这几行去掉
    //例如:http://kmland.km.gov.cn/view.asp?id=7089&frist_lanmu=173
    private static TableElement isValueElement(
            String[] Propertys, Element element)
    {
        TableElement tableElement = new TableElement();
        List<Element> propertyElements = new ArrayList<Element>();
        Elements tdElements = element.getElementsByTag("td");
        Elements thElements = element.getElementsByTag("th");
        if(thElements != null && thElements.size() > 0){
            for(Element thelement : thElements){
                propertyElements.add(thelement);
            }
        }
        if(tdElements != null && tdElements.size() > 0){
            for(Element tdelement : tdElements){
                propertyElements.add(tdelement);
            }
        }
        int index = 0;
        int consist = 0;
        int size = propertyElements.size();
        int consistFlag = 0;
        //是否是代理商的中标表格
        boolean isProxy = false;
        for (int i = 0; i < size; i++) {
            consist++;
            Element tdElement = propertyElements.get(i);
            //去除所有空格
            String text = tdElement.text().replace(" ", "");
            if (!text.trim().equals("")) {
                String value = adjuestmentParm(text);
                if (value != null && !value.contains(SERVICE_CHARGE)) {
                    value = BidStringUtils.parseString(value);
                    double max = 0.0d;
                    for (int j = 0; j < Propertys.length; j++) {
                        double temp = SimFeatureUtil.sim(
                                Propertys[j], value);
                        if (temp > max) {
                            max = temp;
                        }
                    }
                    if (max >= 0.6) {
                        index++;
                        if (consist == 1) {
                            consist = 0;
                            consistFlag++;

                        } else {
                            consist = 0;
                        }
                    }
//                    else {
//                        if (consist >= 10) {
//                            break;
//                        }
//                    }
                }
                //若表中含有 中标服务费金额，则是代理商中标的表格，目前不需要获取代理商的中标数据
                if (value != null && value.contains(SERVICE_CHARGE)){
                    isProxy = true;
                }
            }
        }
        //关键词命中超过两个即可
        if (index >= NUM) {
            tableElement.setWordNum(index);
            if (isProxy){
                //舍弃代理商的中标数据
                return  null;
            }
            //当consistFlag大于2时说明表格内容是竖向的
            if (consistFlag >= 2) {
                //竖向表格
                tableElement.setElement(removeRedundance(Propertys, element));
                tableElement.setCross(false);
            } else {
                tableElement.setElement(element);
            }
            return tableElement;
        } else {
            return null;
        }
    }

    /**
     * 获取存在于文本中的中标信息
     * @param docText 主页面数据
     * @return 中标实体信息列表
     */
    //subStringNumber() :只获取带有数字和小数点的数据（获取字符串中的金额）
    //subStringBetween（）: 获取两个指定字符串中间的字符
    public static List<PersonaBids> getBidWinningInfoOld(String docText){
        PersonaBids personaBids = new PersonaBids();
        //获取中标金额
        for (int i = 0; i < BidConstants.AMOUNT_PROPERTIES.length; i++){
            String bidAmount = BidStringUtils.subStringBetween(docText, BidConstants.AMOUNT_PROPERTIES[i], " ");
            if(bidAmount != null && bidAmount.contains("；")){
                bidAmount = BidStringUtils.subEndString(bidAmount,"；");
            }
            //bidAmount包含百分比则是招标金额经过打折后的金额，不是中标金额的数据。 bidAmount包含两个"."则说明中标金额中有多个金额，此时很难获取真正的中标金额
            if(bidAmount != null && !bidAmount.contains("%") && BidStringUtils.countSearchChar(bidAmount,".") < 2){
                if (BidConstants.AMOUNT_PROPERTIES[i].contains("万元") || ((bidAmount.contains("万元")) && !(bidAmount.contains("万元整")))){
                    try {
                        BigDecimal amount = null;
                        String s = BidStringUtils.subStringNumber(bidAmount);
                        if (BidStringUtils.hasNumber(s)){
                            amount = new BigDecimal(s);
                        }else {
                            amount = new BigDecimal(ChineseToNumberUtil.ChineseToNumber(s).toString());
                        }
                        BigDecimal wan = new BigDecimal("10000.00");
                        BigDecimal multiply = amount.multiply(wan);
                        personaBids.setBidAmount(String.valueOf(multiply));
                    }catch (NumberFormatException e){
                        System.out.println("======报错的中标金额："+bidAmount+"============");
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }else {
                    personaBids.setBidAmount(BidStringUtils.subStringNumber(bidAmount));
                }
            }

        }

//        personaBids.setBidAmount(bidAmountHandle(docText));
//        personaBids.setBidWinner(bidWinnerHandle(docText));
//        personaBids.setPurchaser(bidPurchaserHandle(docText));

        //获取中标人名称
        for (int i = 0; i < BidConstants.COMPANY_PROPERTIES.length; i++){
            String winner = BidStringUtils.subStringBetween(docText, BidConstants.COMPANY_PROPERTIES[i], " ");
            if (StringUtils.isNotEmpty(winner) && winner.contains("；")){
                winner = BidStringUtils.subEndString(winner,"；");
                personaBids.setBidWinner(winner.trim());
            }
            else if(StringUtils.isNotEmpty(winner)){
                personaBids.setBidWinner(winner.trim());
            }

        }
        //获取招标人名称
        String purchaser = "";
        for (int i = 0; i < BidConstants.PURCHASER_PROPERTIES.length; i++){

//            if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(docText, "受", "的委托"))){
//                purchaser = BidStringUtils.subStringBetween(docText, "受", "的委托");
//                personaBids.setPurchaser(purchaser);
//            } else if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(docText, "受", "委托"))){
//                purchaser = BidStringUtils.subStringBetween(docText, "受", "委托");
//                personaBids.setPurchaser(purchaser);
//            } else {
                purchaser = BidStringUtils.subStringBetween(docText, BidConstants.PURCHASER_PROPERTIES[i], " ");
//                if (StringUtils.isNotEmpty(purchaser) && purchaser.contains("；")){
//                    purchaser = BidStringUtils.subEndString(purchaser,"；");
//                    personaBids.setPurchaser(purchaser.trim().replace("（公章）",""));
//                }
//                else if(StringUtils.isNotEmpty(purchaser)){
//                    personaBids.setPurchaser(purchaser.trim().replace("（公章）",""));
//                }
                String hasPurchaserLine = BidStringUtils.subStringBetween(docText, BidConstants.PURCHASER_PROPERTIES[i], " ");
                if(StringUtils.isNotEmpty(hasPurchaserLine) && hasPurchaserLine.contains("，")){
                    purchaser = BidStringUtils.subEndString(hasPurchaserLine, "，");
                    if (purchaser.contains("；")){
                        purchaser = BidStringUtils.subEndString(purchaser,"；");
                        personaBids.setPurchaser(purchaser.trim().replace("（公章）","").replace("（盖章）","").replace("（章）",""));
                    }else {
                        personaBids.setPurchaser(purchaser.trim().replace("（公章）","").replace("（盖章）","").replace("（章）",""));
                    }

                }
//                else if (StringUtils.isNotEmpty(hasPurchaserLine)){
//                    personaBids.setPurchaser(hasPurchaserLine.trim().replace("（公章）",""));
//                }
                else if (StringUtils.isNotEmpty(purchaser)){
                    personaBids.setPurchaser(BidStringUtils.purchaserStringHandle(purchaser));
                }

//            }
        }
        if (StringUtils.isEmpty(personaBids.getPurchaser())){
            if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(docText, "受", "的委托"))){
                purchaser = BidStringUtils.subStringBetween(docText, "受", "的委托");
                personaBids.setPurchaser(purchaser);
            } else if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(docText, "受", "委托"))){
                purchaser = BidStringUtils.subStringBetween(docText, "受", "委托");
                personaBids.setPurchaser(purchaser);
            }
        }
        List<PersonaBids> personaBidsList = new ArrayList<>();
        if (StringUtils.isNotEmpty(personaBids.getBidWinner()) || StringUtils.isNotEmpty(personaBids.getBidAmount()) ||
                StringUtils.isNotEmpty(personaBids.getPurchaser())){
            personaBidsList.add(personaBids);
        }
        return personaBidsList;
    }

    /**
     * 判断表格的文本中是否存在中标金额
     * @param text 输入文本内容
     * @return 布尔值
     */
    public static boolean existBidAmount(String text){
        for (String amountProperties : BidConstants.AMOUNT_PROPERTIES_TABLE){
            if(amountProperties.equals(text)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断表格的文本中是否存在中标供应商
     * @param text 输入文本内容
     * @return 布尔值
     */
    public static boolean existBidWinningCompany(String text){
        for (String amountProperties : BidConstants.COMPANY_PROPERTIES_TABLE){
            if(amountProperties.equals(text)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断表格的文本中是否存在招标人（采购人）
     * @param text 输入文本内容
     * @return 布尔值
     */
    public static boolean existBidder(String text){
        for (String amountProperties : BidConstants.PURCHASER_PROPERTIES){
            if(amountProperties.equals(text)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在招标人关键字
     * @param text
     * @return
     */
    public static boolean existPurchaser(String text){
        for (String purchaserProperties : BidConstants.PURCHASER_PROPERTIES_TABLE){
            if (purchaserProperties.equals(text)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取中标数据并进行相应的处理
     * @param docText
     * @return
     */
    public static String bidAmountHandle(String docText){
        String bidAmount = "";
        for (int i = 0; i < BidConstants.AMOUNT_PROPERTIES.length; i++){
            bidAmount = BidStringUtils.subStringBetween(docText, BidConstants.AMOUNT_PROPERTIES[i], " ");
            if(bidAmount != null && bidAmount.contains("；")){
                bidAmount = BidStringUtils.subEndString(bidAmount,"；");
            }
            //bidAmount包含百分比则是招标金额经过打折后的金额，不是中标金额的数据。 bidAmount包含两个"."则说明中标金额中有多个金额，此时很难获取真正的中标金额
            if(bidAmount != null && !bidAmount.contains("%") && BidStringUtils.countSearchChar(bidAmount,".") < 2){
                if (BidConstants.AMOUNT_PROPERTIES[i].contains("万元") || ((bidAmount.contains("万元")) && !(bidAmount.contains("万元整")))){
                    BigDecimal amount = new BigDecimal(BidStringUtils.subStringNumber(bidAmount));
                    BigDecimal wan = new BigDecimal("10000.00");
                    try {
                        BigDecimal multiply = amount.multiply(wan);
                        bidAmount = String.valueOf(multiply);
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }else {
                    bidAmount = BidStringUtils.subStringNumber(bidAmount);
                }
            }

        }
        return bidAmount;
    }

    /**
     * 获取并处理中标人数据
     * @param docText html文本数据
     * @return
     */
    public static String bidWinnerHandle(String docText){
        //获取中标人名称
        String winner = "";
        for (int i = 0; i < BidConstants.COMPANY_PROPERTIES.length; i++){
            winner = BidStringUtils.subStringBetween(docText, BidConstants.COMPANY_PROPERTIES[i], " ");
            if (StringUtils.isNotEmpty(winner) && winner.contains("；")){
                winner = BidStringUtils.subEndString(winner,"；");
            }
            else if(StringUtils.isNotEmpty(winner)){
                winner = winner.trim();
            }

        }
        return winner;
    }

    /**
     *获取并处理招标人数据
     * @param docText
     * @return 招标人字符串
     */
    public static String bidPurchaserHandle(String docText){
        //获取招标人名称
        String purchaser = "";
        for (int i = 0; i < BidConstants.PURCHASER_PROPERTIES.length; i++){
            if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(docText, "受", "委托"))){
                purchaser = BidStringUtils.subStringBetween(docText, "受", "委托");
//                personaBids.setPurchaser(purchaser);
            }else {
                purchaser = BidStringUtils.subStringBetween(docText, BidConstants.PURCHASER_PROPERTIES[i], " ");
                if (StringUtils.isNotEmpty(purchaser) && purchaser.contains("；")){
                    purchaser = BidStringUtils.subEndString(purchaser,"；").trim().replace("（公章）","");
//                    personaBids.setPurchaser(purchaser.trim());
                }
                else if(StringUtils.isNotEmpty(purchaser)){
                    purchaser = purchaser.trim().replace("（公章）","");
                }
            }
        }
        return purchaser;

    }

    /**
     * 获取存在于文本中的中标信息
     * @param docText 主页面数据
     * @return 中标实体信息列表
     */
    //subStringNumber() :只获取带有数字和小数点的数据（获取字符串中的金额）
    //subStringBetween（）: 获取两个指定字符串中间的字符
    public static List<PersonaBids> getBidWinningInfo(String docText){

        String bidWinContent = getBidString(docText);
//        String s = BidStringUtils.subStringBetween(docText, "中标信息", "主要标的信息");
        //subBidTitle：分标标题的类型
        List<PersonaBids> personaBidsList = new ArrayList<>();
        //判断是否存在分标
        if (!("".equals(bidWinContent) || bidWinContent == null)){
            char subBidTitle = SubBidTitleEnum.hasDesc(bidWinContent);

            //*是默认值，为什么选π？ suBidTitle是一般招标文本中不包含π
            if (subBidTitle != 'π'){
                //分标标题为A分标
//                if (subBidTitle == SubBidTitleEnum.getDescByValue(1)){
                    boolean hasBidInfo = true;
                    //标记当前是第几个分标
                    int starNumber = 0;
                    //标记下一个分标
                    int nextNumber = 1;
                    while (hasBidInfo){
//                        String bidString = BidStringUtils.subStringBetween(bidWinContent, (char)(subBidTitle + starNumber )+ "分标", (char)(subBidTitle + nextNumber) + "分标");
                        //分标中的字符串（含有中标金额和中标供应商）
                        String bidString = null;
                        for (String subBidProperty : BidConstants.SUB_BID_KEYWORD){
                            //截取分标间的字符串，分标间的字符串为中标数据
                            String subBidString = BidStringUtils.subStringBetween(bidWinContent, (char)(subBidTitle + starNumber )+ subBidProperty, (char)(subBidTitle + nextNumber) + subBidProperty);
                            if (subBidString != null){
                                bidString = subBidString;
                                break;
                            }
                        }
                        if (bidString == null){
                            //获取最后一个分标的中标数据
                            String lastBidInfo = null;
//                            lastBidInfo = BidStringUtils.subStringByAfter(bidWinContent, (char)(subBidTitle + starNumber) + "分标");
                            for (String subBidProperty : BidConstants.SUB_BID_KEYWORD){
                                String subBidString = BidStringUtils.subStringByAfter(bidWinContent, (char)(subBidTitle + starNumber )+ subBidProperty);
                                if (subBidString != null){
                                    lastBidInfo = subBidString;
                                    break;
                                }
                            }
                            if (lastBidInfo == null || "".equals(lastBidInfo)){
                                hasBidInfo = false;
                            }else {
                                PersonaBids lastPersonaBids = getBidInfo(lastBidInfo, docText);
                                personaBidsList.add(lastPersonaBids);
                                starNumber++;
                                nextNumber++;
                            }

                        }else {
                            PersonaBids bidInfo = getBidInfo(bidString, docText);
                            personaBidsList.add(bidInfo);
                            starNumber++;
                            nextNumber++;
                        }
//                    }
                }

//                else if (subBidTitle == NO_SUB_BID_TITLE ){
//                    //若无分标，则通过获取一个中标信息的方式拿到中标信息
//                    PersonaBids bidInfo = getBidInfo(docText, docText);
//                    personaBidsList.add(bidInfo);
//                }
            }
        }else {
            //匹配不到分标，则通过获取一个中标信息的方式拿到中标信息
            PersonaBids bidInfo = getBidInfo(docText, docText);
            personaBidsList.add(bidInfo);
        }
        return personaBidsList;
    }

    //获取中标数据在文本中的位置
    private static String getBidString(String str){
        String bidString = null;
        if (BidStringUtils.subStringBetween(str, "中标信息及主要标的信息", "其他补充事宜") != null || !"".equals(BidStringUtils.subStringBetween(str, "中标信息及主要标的信息", "其他补充事宜"))){
            bidString = BidStringUtils.subStringBetween(str, "中标信息及主要标的信息", "其他补充事宜");
        }
        else if (BidStringUtils.subStringBetween(str, "中标信息", "主要标的信息") != null){
            bidString = BidStringUtils.subStringBetween(str, "中标信息", "主要标的信息");
        }else if (BidStringUtils.subStringBetween(str, "中标结果：", "联系事项：") != null || !"".equals(BidStringUtils.subStringBetween(str, "中标结果", "联系事项："))){
            bidString = BidStringUtils.subStringBetween(str, "中标结果：", "联系事项");
        }

        return bidString;
    }

    /**
     *  获取中标信息
     * @param bidString 含有中标信息的文本数据
     * @param docText 整个页面的文本数据
     * @return 招投标对象
     */
    private static PersonaBids getBidInfo(String bidString, String docText){
        PersonaBids personaBids = new PersonaBids();
        //获取中标金额
        for (int i = 0; i < BidConstants.AMOUNT_PROPERTIES.length; i++){
            String bidAmount = BidStringUtils.subStringBetween(bidString, BidConstants.AMOUNT_PROPERTIES[i], " ");
            if(bidAmount != null && bidAmount.contains("；")){
                bidAmount = BidStringUtils.subEndString(bidAmount,"；");
            }
            //bidAmount包含百分比则是招标金额经过打折后的金额，不是中标金额的数据。 bidAmount包含两个"."则说明中标金额中有多个金额，此时很难获取真正的中标金额
            if(bidAmount != null && !bidAmount.contains("%") && BidStringUtils.countSearchChar(bidAmount,".") < 2){
                //判断中标金额关键字中是否存在万元
                if (BidConstants.AMOUNT_PROPERTIES[i].contains("万元")){
                    try {
                        BigDecimal amount = null;
//                      String amountString = BidStringUtils.subStringNumber(bidAmount);
                        //获取中标金额中的数字部分。若出现多个数字，很难判断哪个是真实的中标金额，故需舍弃该中标金额，
                        String[] amountStrings = BidStringUtils.subNumbers(bidAmount);
                        if (amountStrings.length == 1){
                            //bidAmounts中标金额数组中只存在1条数据则说明中标金额中没有其他脏数据
                            amount = new BigDecimal(amountStrings[0]);
                        }
                        else if (amountStrings.length == 0){
                            //获取不到中标金额，有可能为中文大写的中标金额
                            amount = new BigDecimal(ChineseToNumberUtil.ChineseToNumber(bidAmount).toString());
                        }

                        BigDecimal wan = new BigDecimal("10000.00");
                        //存在多个数字，难以分辨真正的中标数据，舍弃该金额(此时amount为null)
                        if (amount != null){
                            BigDecimal multiply = amount.multiply(wan);
                            personaBids.setBidAmount(String.valueOf(multiply));
                        }
                    }catch (NumberFormatException e){
                        System.out.println("======报错的中标金额："+bidAmount+"============");
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }else {
                    //如果中标金额字符串中只含有一个数字则获取改数字，若无数字，则有可能是大写中文数字，需对大写中文数字进行转化。若存在多个数字则不获取
                    if (BidStringUtils.hasNumber(bidAmount)){

                        personaBids.setBidAmount(BidStringUtils.subStringNumber(bidAmount));
                    }else {
                        personaBids.setBidAmount(BidStringUtils.subStringNumber(ChineseToNumberUtil.ChineseToNumber(bidAmount).toString()));
                    }

                    String[] bidAmounts = BidStringUtils.subNumbers(bidAmount);
                    if (bidAmounts.length == 1){
                        //bidAmounts中标金额数组中只存在1条数据则说明中标金额中没有其他脏数据
                        personaBids.setBidAmount(bidAmounts[0]);
                    }
                    else if (bidAmounts.length == 0){
                        //获取不到中标金额，有可能为中文大写的中标金额
                        String s = BidStringUtils.subStringNumber(ChineseToNumberUtil.ChineseToNumber(bidAmount).toString());
                        if (StringUtils.isNotEmpty(s)){
                            personaBids.setBidAmount(s);
                        }
                    }
                    //存在多个中标金额时不获取数据（难以判断选择哪个数字）
                }
            }

        }

        //        personaBids.setBidAmount(bidAmountHandle(docText));
        //        personaBids.setBidWinner(bidWinnerHandle(docText));
        //        personaBids.setPurchaser(bidPurchaserHandle(docText));

        //获取中标人名称
        for (int i = 0; i < BidConstants.COMPANY_PROPERTIES.length; i++){
            String winner = BidStringUtils.subStringBetween(bidString, BidConstants.COMPANY_PROPERTIES[i], " ");
            if (StringUtils.isNotEmpty(winner) && winner.contains("；")){
                winner = BidStringUtils.subEndString(winner,"；");
                personaBids.setBidWinner(winner.trim());
            }
            else if(StringUtils.isNotEmpty(winner)){
                personaBids.setBidWinner(winner.trim());
            }

        }
        //获取招标人名称
        String purchaser = "";
        for (int i = 0; i < BidConstants.PURCHASER_PROPERTIES.length; i++){

            //            if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(docText, "受", "的委托"))){
            //                purchaser = BidStringUtils.subStringBetween(docText, "受", "的委托");
            //                personaBids.setPurchaser(purchaser);
            //            } else if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(docText, "受", "委托"))){
            //                purchaser = BidStringUtils.subStringBetween(docText, "受", "委托");
            //                personaBids.setPurchaser(purchaser);
            //            } else {
            purchaser = BidStringUtils.subStringBetween(docText, BidConstants.PURCHASER_PROPERTIES[i], " ");
            //                if (StringUtils.isNotEmpty(purchaser) && purchaser.contains("；")){
            //                    purchaser = BidStringUtils.subEndString(purchaser,"；");
            //                    personaBids.setPurchaser(purchaser.trim().replace("（公章）",""));
            //                }
            //                else if(StringUtils.isNotEmpty(purchaser)){
            //                    personaBids.setPurchaser(purchaser.trim().replace("（公章）",""));
            //                }
            String hasPurchaserLine = BidStringUtils.subStringBetween(docText, BidConstants.PURCHASER_PROPERTIES[i], " ");
            if(StringUtils.isNotEmpty(hasPurchaserLine) && hasPurchaserLine.contains("，")){
                purchaser = BidStringUtils.subEndString(hasPurchaserLine, "，");
                if (purchaser.contains("；")){
                    purchaser = BidStringUtils.subEndString(purchaser,"；");
                    personaBids.setPurchaser(purchaser.replace("（公章）","").trim());
                }else {
                    personaBids.setPurchaser(purchaser.replace("（公章）","").trim());
                }

            }
            //                else if (StringUtils.isNotEmpty(hasPurchaserLine)){
            //                    personaBids.setPurchaser(hasPurchaserLine.trim().replace("（公章）",""));
            //                }
            else if (StringUtils.isNotEmpty(purchaser)){
                personaBids.setPurchaser(BidStringUtils.purchaserStringHandle(purchaser));
            }

            //            }
        }
        if (StringUtils.isEmpty(personaBids.getPurchaser())){
            if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(docText, "受", "的委托"))){
                purchaser = BidStringUtils.subStringBetween(docText, "受", "的委托");
                personaBids.setPurchaser(purchaser);
            } else if (StringUtils.isNotEmpty(BidStringUtils.subStringBetween(docText, "受", "委托"))){
                purchaser = BidStringUtils.subStringBetween(docText, "受", "委托");
                personaBids.setPurchaser(purchaser);
            }
        }
        if (StringUtils.isNotEmpty(personaBids.getBidWinner()) || StringUtils.isNotEmpty(personaBids.getBidAmount()) ||
                StringUtils.isNotEmpty(personaBids.getPurchaser())) {
//            personaBidsList.add(personaBids);
            return personaBids;
        }
        else {
            return new PersonaBids();
        }
    }


}