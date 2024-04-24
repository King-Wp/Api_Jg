package com.ruoyi.components.utils;

/**
 * 解析表格
 */

import com.ruoyi.common.utils.BidStringUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.components.domain.PersonaBids;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class TableUtil {



    /**
     * 解析表格内容，获取招投标信息 (横向的表格进入getAcrossTableValue方法解析 竖向的表格进入getTableValue方法解析)
     * @param tableElemts
     * @return
     */
    public static List<PersonaBids> extractPropertyInfos(List<TableElement> tableElemts)  {
        List<PersonaBids> personaBids = new ArrayList<PersonaBids>();
        if(tableElemts != null && tableElemts.size() > 0){
            for (TableElement element : tableElemts) {
                if(element.isCross()){
                    personaBids.addAll(TableUtil.getAcrossTableValue(element.getElement()));
                }else{
                    personaBids.addAll(TableUtil.getTableValue(element.getElement()));
                }
            }
            return personaBids;
        }
        return null;
    }


    //对于竖向内容的表格先取出属性名占的最大行数存为row
    public static List<PersonaBids> getTableValue(Element tableElement) {
        Elements trElements = tableElement.getElementsByTag("tr");
        int row = getRowElement(trElements.get(0));
        //获取属性名所在的列（获取表头）
        List<String> propertys = parseTablePropertys(row,trElements);
        return  parsePropertyValue(row, propertys, trElements);
    }

    /**
     * 提取TABLE属性
     *
     * param tableElement
     */
    //把每行的元素存入ptdELementsList列表中,把每行的列数存入length 然后进入parsePropertyString函数对第一行内容提取得到属性名
    private static List<String> parseTablePropertys(int row,Elements trElements) {
        List<String> propertys = new ArrayList<String>();
        List<Elements> ptdELementsList = new ArrayList<Elements>();
        int[] lengths = new int[row]; // 储存每行的长度(列数-单元格个数)
        int[] index = new int[row]; // 每行位置
        for (int i = 0; i < row; i++) {
            Element trElement = trElements.get(i);
            Elements elements = new Elements();
            Elements tdElements = trElement.getElementsByTag("td");
            Elements thElements = trElement.getElementsByTag("th");
            if(tdElements != null && tdElements.size()>0){
                elements.addAll(tdElements);
            }
            if(thElements != null && thElements.size()>0){
                elements.addAll(thElements);
            }
            ptdELementsList.add(elements);
            lengths[i] = elements.size();
        }
        parsePropertyString(propertys, index, lengths, 0, ptdELementsList);
        return propertys;
    }

    //提取内容的值---遍历除属性行之外的行 依次提取td元素进入parseValues提取内容的值
    //flags 记录行的状态
    //valueFlags 记录每个属性一个值对多个单元格的状态
    //vtdElements 记录每行单元格的元素td
    //size 值的总行数
    //length记录每行的单元格td数
    private static List<PersonaBids> parsePropertyValue(int row, List<String> propertys,
                                                         Elements trElements) {
        int propertysSize = propertys.size();
        List<Elements> vtdElements = new ArrayList<Elements>();
        int trsize = trElements.size();
        int size = trsize - row;
        int[] lengths = new int[size];
        boolean[] flags = new boolean[size];
        boolean[][] valueFlags = new boolean[size][propertysSize];

        for (int i = row; i < trsize; i++) {
            Element trElement = trElements.get(i);
            Elements velements = new Elements();
            Elements tdElements = trElement.getElementsByTag("td");
            Elements thElements = trElement.getElementsByTag("th");
            if(thElements!=null&&thElements.size()>0){
                velements.addAll(thElements);
            }
            if(tdElements!=null&&tdElements.size()>0){
                velements.addAll(tdElements);
            }
//				Elements tdElements = trElement.getElementsByTag("td");
            if (velements != null && velements.size() > 0) {
                lengths[i-row] = velements.size();
                vtdElements.add(velements);
                for(int j = 0 ; j < propertysSize;j++){
                    valueFlags[i-row][j]= false;
                }
                flags[i-row] = true;
            } else {
                size--;
            }
        }
        return parseValues(propertys, vtdElements, size, lengths, flags,valueFlags);
    }

    /**
     * 取TABLE里面的数据 包含 ROWSPAN 表明该元素对应多个PROPERTY 包含COLSPAN表明该元素对应多条数据
     *
     * param propertysSize
     * @param vtdElements
     * @param size 值的行数，及中标信息的个数，竖型表格中，一行则代表一条中标信息
     * @param lengths
     * @param flags
     * @param valueFlags
     */
    //一个单元格的内容对应多条记录的 例子:http://kmland.km.gov.cn/view.asp?id=7089&frist_lanmu=173
    //遇到一对多的值 则取出它的rowspan和colspan的,把对应位置的值赋值,并修改该位置的valueFlag为true
    // （暂时注释掉这个策略，目前爬取的网站中这个情况比较少）
    private static List<PersonaBids> parseValues(List<String> propertys,
                                                  List<Elements> vtdElements, int size, int[] lengths,
                                                  boolean[] flags, boolean[][] valueFlags) {
        int propertysSize = propertys.size();
        String[][] pValueStrs = new String[size][propertysSize];

        for (int i = 0; i < size; i++) {
            Elements tdValueElements = vtdElements.get(i);
            int k = 0;
            for (int j = 0; j < lengths[i]; j++) {
                Element tdValueElement = null;
                try {
                    tdValueElement = tdValueElements.get(j);
                } catch (Exception e) {
                    flags[i] = false;
                    break;
                }
                if(valueFlags[i][k]){
                    while (valueFlags[i][k]) {
                        k++;
                    }
                }
//                if (tdValueElement.hasAttr("rowspan")) {
//                    int rowspan = Integer.valueOf(tdValueElement.attr("rowspan"));
//                    int colspan = 1;
//                    if (tdValueElement.hasAttr("colspan")) {
//                        colspan = Integer.valueOf(tdValueElement.attr("colspan"));
//                    }
//                    if (rowspan > 1) {
//                        System.out.println(rowspan);
//                        for (int m = 0; m < rowspan; m++) {
//                            for (int n = 0; n < colspan; n++) {
//                                System.out.println("i = " + i +"m=" +m);
//                                try {
//                                    valueFlags[i + m][k + n] = true;
//                                    pValueStrs[i + m][k + n] = tdValueElement.text();
//                                } catch (Exception e) {
//                                    System.out.println("i = " + i +"m=" +m +"k+n = "+ (k+n)+ "length");
//                                }
//                            }
//                        }
//                    }
//                }else if (tdValueElement.hasAttr("colspan")) {
//                    int colspan = Integer.valueOf(tdValueElement
//                            .attr("colspan"));
//                    if (colspan > 1) {
//                        if (colspan >= size - 1) {
//                            flags[i] = false;
//                            break;
//                        }
//                        for (int m = 0; m < colspan; m++) {
//                            valueFlags[i][k] = true;
//                            pValueStrs[i+m][k] = tdValueElement.text();
//                        }
//                    }
//                }else{
                    if(tdValueElement!=null){
                        pValueStrs[i][k] = tdValueElement.text();
                    }
//                }
                k++;
            }
        }
        List<PersonaBids> personaBidsList = new ArrayList<PersonaBids>();
        for (int i = 0; i < size; i++) {
            PersonaBids personaBids = new PersonaBids();
            if (flags[i]) {
                for (int j = 0; j < propertysSize; j++) {
                    if(propertys.get(j) != null && pValueStrs[i][j] != null){
//                        PropertyInfo propertyValue = new PropertyInfo();
                        if(DataTableUtil.existBidAmount(propertys.get(j)) && !pValueStrs[i][j].contains("%") && BidStringUtils.countSearchChar(pValueStrs[i][j],".") < 2){
                            //中标金额
                            if(propertys.get(j).contains("万元") || pValueStrs[i][j].contains("万元") && !(pValueStrs[i][j].contains("万元整"))){
                                //将中标金额转化为元
//                                BigDecimal amount = new BigDecimal(BidStringUtils.subStringNumber(pValueStrs[i][j]));
                                BigDecimal amount = null;
                                String[] bidStrings = BidStringUtils.subNumbers(pValueStrs[i][j]);
                                if (bidStrings.length == 1){
                                    amount = new BigDecimal(bidStrings[0]);
                                }else if (bidStrings.length == 0){
                                    amount = new BigDecimal(ChineseToNumberUtil.ChineseToNumber(pValueStrs[i][j]).toString());
                                }
                                BigDecimal wan = new BigDecimal("10000.00");
                                try {
                                    if (amount != null){
                                        personaBids.setBidAmount(String.valueOf(amount.multiply(wan)));
                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
//                                personaBids.setBidAmount(String.valueOf(Double.parseDouble(BidStringUtils.subStringNumber(pValueStrs[i][j]))
//                                * 10000.00));
                            }else {
                                String[] bidStrings = BidStringUtils.subNumbers(pValueStrs[i][j]);
                                if (bidStrings.length == 1){
                                    personaBids.setBidAmount(bidStrings[0]);
                                }else if (bidStrings.length == 0){
                                    personaBids.setBidAmount(ChineseToNumberUtil.ChineseToNumber(pValueStrs[i][j]).toString());
                                }
                            }

                        }else if(DataTableUtil.existBidWinningCompany(propertys.get(j))){
                            personaBids.setBidWinner(pValueStrs[i][j].trim());
                        }
//                        propertyValue.setName(propertys.get(j));
//                        propertyValue.setValue(pValueStrs[i][j]);
//                        propertyInfos.add(propertyValue);

                    }
                }
            }
            personaBidsList.add(personaBids);
        }
        return personaBidsList;
    }

    //获取横向表格的中标数据
    public static List<PersonaBids> getAcrossTableValue(Element element)
    {
//			Elements tdElements = element.getElementsByTag("td");
        Elements trElements = element.getElementsByTag("tr");
        List<PropertyInfo> propertyValues = new ArrayList<PropertyInfo>();
        List<PersonaBids> personaBidsList = new ArrayList<>();
        for(Element trElement:trElements){
            Elements tdElements = trElement.getElementsByTag("td");
            int size = tdElements.size();
            if(size % 2 ==0){
                PropertyInfo propertyValue = null;
                for (int i = 0; i < size; i++) {
                    Element tdElement = tdElements.get(i);
                    String value = tdElement.text();
                    if (i % 2 == 0) {
                        //设置属性名
                        propertyValue = new PropertyInfo();
                        propertyValue.setName(BidStringUtils.parseString(value));
                    } else {
                        //设置属性值
                        propertyValue.setValue(value);
                        propertyValues.add(propertyValue);
                    }
                }
            }
        }
        List<String> bidAmountList = new ArrayList<>();
//        List<String> purchaserList = new ArrayList<>();
        List<String> bidWinnerList = new ArrayList<>();
//        PersonaBids personaBids = new PersonaBids();
        //purchaser :招标人
        String purchaser = "";
        for (PropertyInfo propertyInfo : propertyValues){

            if (DataTableUtil.existBidAmount(propertyInfo.getName().trim()) && !propertyInfo.getValue().contains("%") && BidStringUtils.countSearchChar(propertyInfo.getValue(),".") < 2){
                if(propertyInfo.getName().contains("万元") || ((propertyInfo.getValue().contains("万元")) && !(propertyInfo.getValue().contains("万元整")))){
                    //将中标金额转化为元
                    try {
                        BigDecimal amount = null;
//                        String s = BidStringUtils.subStringNumber(propertyInfo.getValue());
                        String[] bidAmounts = BidStringUtils.subNumbers(propertyInfo.getValue());
                        if (bidAmounts.length == 1){
                            //bidAmounts中标金额数组中只存在1条数据则说明中标金额中没有其他脏数据
//                            bidAmountList.add(bidStrings[0]);
                            amount = new BigDecimal(bidAmounts[0]);
                        }
                        else if (bidAmounts.length == 0){
                            //获取不到中标金额，有可能为中文大写的中标金额
                            amount = new BigDecimal(ChineseToNumberUtil.ChineseToNumber(propertyInfo.getValue()).toString());
                        }

                        BigDecimal wan = new BigDecimal("10000.00");
                        //存在多个数字，难以分辨真正的中标数据，舍弃该金额(此时amount为null)
                        if (amount != null){
                            bidAmountList.add(String.valueOf(amount.multiply(wan)));
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
//                    bidAmountList.add(String.valueOf(Double.parseDouble(BidStringUtils.subStringNumber(propertyInfo.getValue()))
//                            * 10000.00));
                }else {
//                    BidStringUtils.subStringNumber(propertyInfo.getValue()));
                    String[] bidAmounts = BidStringUtils.subNumbers(propertyInfo.getValue());
                    if (bidAmounts.length == 1){
                        //bidAmounts中标金额数组中只存在1条数据则说明中标金额中没有其他脏数据
                        bidAmountList.add(bidAmounts[0]);
                    }
                    else if (bidAmounts.length == 0){
                        //获取不到中标金额，有可能为中文大写的中标金额
                        String s = BidStringUtils.subStringNumber(ChineseToNumberUtil.ChineseToNumber(propertyInfo.getValue()).toString());
                        if (StringUtils.isNotEmpty(s)){
                            bidAmountList.add(s);
                        }
                    }
//                    personaBids.setBidAmount(BidStringUtils.subStringNumber(propertyInfo.getValue()));
                }

            } else if (DataTableUtil.existBidWinningCompany(propertyInfo.getName().trim())){
                bidWinnerList.add(propertyInfo.getValue());
//                personaBids.setBidWinner(propertyInfo.getValue());
            } else if (DataTableUtil.existPurchaser(propertyInfo.getName().trim())){
                //若表格中存在招标人，又存在建设单位，则建设单位会设置为"/"，故需过滤掉"/"字符串
                if (!"/".equals(propertyInfo.getValue())){
                    purchaser = propertyInfo.getValue();
                }

//                personaBids.setPurchaser(propertyInfo.getValue());
            }
        }
        //存在中标数据则添加到中标数据列表中
//        if(StringUtils.isNotEmpty(personaBids.getBidWinner()) || StringUtils.isNotEmpty(personaBids.getBidAmount())){
//            personaBidsList.add(personaBids);
//        }
        if (bidAmountList.size() == bidWinnerList.size() ){
            //两个列表数据一一对应,都能获取到中标人和中标金额才赋值
            int size = bidAmountList.size();
            for (int i = 0; i < size; i++){
                PersonaBids personaBids = new PersonaBids();
                personaBids.setBidAmount(bidAmountList.get(i));
                personaBids.setBidWinner(bidWinnerList.get(i).trim());
                personaBids.setPurchaser(purchaser.trim());
                personaBidsList.add(personaBids);
            }
        }
        return personaBidsList;
    }

    /**
     * 提取属性名
     *
     * @param propertyStrs
     * @param index
     * @param lengths
     * @param ind
     * @param tdElementsList
     */
    //遍历属性行中的td,取它的colspan,如果它横跨的大于1则说明它是包含多个小标题的大标题,则进入第二行取值
    private static void parsePropertyString(List<String> propertyStrs,
                                            int[] index, int[] lengths, int ind, List<Elements> tdElementsList) {
        Elements tdElements = tdElementsList.get(ind);
        for (int i = index[ind]; i < lengths[ind]; i++) {
            index[ind] = index[ind] + 1;
            Element tdElement = tdElements.get(i);
            String attribute = tdElement.attr("colspan");
            String value = BidStringUtils.parseString(tdElement.text());
//				if (!value.trim().equals("")) {
            if (attribute != null && !attribute.equals("")) {
                int col = Integer.parseInt(attribute);
                if (col > 1 && tdElementsList.size() > ind + 1) {
                    parsePropertyString(propertyStrs, index, lengths,
                            ind + 1, tdElementsList);
                } else {
                    propertyStrs.add(value);
                }
            } else {
                propertyStrs.add(value);
            }
//				}
        }
    }

    /**
     * 提取属性占的ROW数 取出属性中含有的最大ROWSPAN数
     *
     * @param trElement
     * @return
     */
    private static int getRowElement(Element trElement) {
        Elements tdElements = trElement.getElementsByTag("td");
        int row = 1;
        for (Element tdElement : tdElements) {
            String attribute = tdElement.attr("rowspan");
            if (attribute != null && !attribute.equals("")) {
                int rowSpan = Integer.valueOf(attribute);
                if (rowSpan > row) {
                    row = rowSpan;
                }
            }
        }
        return row;
    }
}