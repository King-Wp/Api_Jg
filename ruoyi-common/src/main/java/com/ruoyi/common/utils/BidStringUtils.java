package com.ruoyi.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BidStringUtils {
    /**
     * 截取指定字符后的字符串，若原字符串不包含指定字符串则返回原字符串的内容
     * @param str：原字符串, subString：指定字符串
     * @return 截取后的字符串
     */
    public static String subStringAfter(String str,String subString){
        if(str.contains(subString)){
            String str1 = str.substring(0, str.indexOf(subString));
            return str.substring(str1.length()+1);
        }
        else {
            return str;
        }
    }

    /**
     * 获取指定字符后的数据，如获取不到则返回空字符串
     */
    public static String getStringAfter(String str,String subString){
        if(str.contains(subString)){
            String str1 = str.substring(0, str.indexOf(subString));
            return str.substring(str1.length()+subString.length());
        }
        else {
            return "";
        }
    }
    /**
     * 截取指定字符后的字符串，若指定字符不存在则返回空
     * @param str 输入字符串
     * @param subString 指定字符串
     * @return
     */
    public static String subStringByAfter(String str,String subString){
        if(str.contains(subString)){
            String str1 = str.substring(0, str.indexOf(subString));
            return str.substring(str1.length()+1);
        }
        else {
            return null;
        }
    }

    /**
     * 截取只包含数字和小数点的字符串（用于在包含中文的字符串中提取金额）
     * @param str:输入字符串
     * @return 截取后的数字
     */
    public static String subStringNumber(String str){
        return str.replaceAll("[^0-9.]", "");
    }

    /**
     * 截取字符串中的各个数字（用于在包含中文的字符串中提取金额）
     * @param str:输入字符串
     * @return 截取后的数字
     */
    public static String[] subNumbers(String str){
        //正则表达式，用于匹配非数字串，+号用于匹配出多个非数字串
        String regEx="[^0-9.]+";
        Pattern pattern = Pattern.compile(regEx);
        //用定义好的正则表达式拆分字符串，把字符串中的数字留出来
        return pattern.split(str);
    }

    /**
     * 截取前后指定字符之间的字符串，若原字符串不包含指定字符串则返回空
     * @param OriStr：原字符串， subString：起始字符串，endString:结束字符串
     * @return 截取后的字符串
     */
    public static String subStringBetween(String OriStr,String startString,String endString){

        if(OriStr != null && OriStr.contains(startString)){
            //一、获取起始字符串所在位置
//            int startIndex = OriStr.substring(0, OriStr.indexOf(startString) + startString.length()).length();
            //replaceAll("^\\s+", ""):去除字符前面的空格，尾部的空格保留
            String start = OriStr.substring(OriStr.indexOf(startString) + startString.length()).replaceAll("^\\s+", "");
//            String startTest = OriStr.substring(OriStr.indexOf(startString) + startString.length());
            //二、判断在起始字符串之后是否存在结束字符串
            if(start.contains(endString)){
                //三，获取起始字符串到结束字符串之间的长度
//                int i = start.indexOf(endString);
                String substring = start.substring(0, start.indexOf(endString));
//                int length = startTest.substring(0,start.indexOf(endString)).length();
//                return OriStr.substring(startIndex, startIndex+length);
                return substring;
            }else {
                return null;
            }

        }
        else {
            return null;
        }
    }

    /**
     * 获取以指定字符串为结尾字符串
     * @param OriStr
     * @param endString
     * @return
     */
    public static String subEndString(String OriStr, String endString){
        return OriStr.substring(0, OriStr.indexOf(endString));
    }
    private static Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5a-zA-Z\\d\\../:]");

    /**
     * 去掉多余字符
     *
     * @return
     */
    public static String parseString(String str) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            sb.append(matcher.group());
        }
        return sb.toString();
    }

    /**
     * 计算字符串中某个字符出现的次数
     * @param str
     * @param searchChar
     * @return
     */
    public static int countSearchChar(String str, String searchChar){
        //先计算原来字符串的长度，再计算将查询字符串设置为空后的长度，两个长度相减则为指定字符串的长度
        int count = 0;
        int originalLength = str.length();
        str = str.replace(searchChar, "");
        int newLength = str.length();
        count = originalLength - newLength;
        return count;
    }

    /**
     * 判断输入的字符串是否包含数字
     * @param str 输入字符串
     * @return 是否包含数字
     */
    public static boolean hasNumber(String str){
        boolean flag = false;
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            flag = true;
        }
        return flag;

    }

    /**
     * 处理招标人字符串
     * @param str 招标人字符串
     * @return
     */
    public static String purchaserStringHandle(String str){
        String purchaser = "";
        if (StringUtils.isNotEmpty(str) && str.contains("；")){
            String s = BidStringUtils.subEndString(str,"；").trim().replace("（公章）","").replace("（盖章）","").replace("（章）","");
            purchaser = BidStringUtils.subStringAfter(s,"：");
        }
        else if(StringUtils.isNotEmpty(str)){
            String s = str.trim().replace("（公章）", "").replace("（盖章）","").replace("（章）","");
            purchaser = BidStringUtils.subStringAfter(s,"：");
        }
        return purchaser.trim();
    }

    /**
     * 过滤非数字
     * @param str
     * @return
     */
    public static String getNumeric(String str) {
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
