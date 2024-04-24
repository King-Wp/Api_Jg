package com.ruoyi.components.utils;

import com.ruoyi.common.utils.BidStringUtils;

import java.math.BigDecimal;

public class ChineseToNumberUtil {
    /**
     * 中文简体
     */
    public static final String[] RMB_NUMBERS = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "零"};
    /**
     * 中文繁体
     */
    public static final String[] BIG_RMB_NUMBERS = new String[]{"壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "零"};
    /**
     * 与汉字相应的转化的数字
     */
    public static final Long[] TO_ARABIC_NUMBERS = new Long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 0L};
    /**
     * 人民币单位关键词  简写（大写数字倍数，一定要由大到小）
     */
    public static final String[] RMB_UNIT = new String[]{"亿", "万", "千", "百", "十", "元", "角", "分", "厘"};
    /**
     * 繁体
     */
    public static final String[] BIG_RMB_UNIT = new String[]{"億", "萬", "仟", "佰", "拾", "圆", "角", "分", "厘"};
    /**
     * 与人民币单位关键词对应的基数
     */
    public static final BigDecimal[] TO_CARDINAL_NUMBERS = new BigDecimal[]{
            new BigDecimal(100000000L), new BigDecimal(10000L), new BigDecimal(1000L),
            new BigDecimal(100L), BigDecimal.TEN, BigDecimal.ONE, new BigDecimal("0.1"),
            new BigDecimal("0.01"), new BigDecimal("0.001")
    };

    /**
     * todo  已亲测试好用  支持繁体、简体格式
     * 大写转化为小写数值的过程操作  支持到小数点后3位
     * 壹仟零壹拾壹亿零壹仟零捌元伍分====>101100001008.05
     * 一千零一十一亿零一千零八元五分====>101100001008.05
     * 没有用number而使用了BigDecimal，是由于number可能导致精度丢失。
     * 该工具类处理最多能处理小数点前14位的数字金额，一般网站的转化大多也是如此。
     */
    public static BigDecimal ChineseToNumber(String money) {
        BigDecimal number = getDigitalNum(money);
        //return Double.parseDouble(number.toString());
        //return number.toString();
        return new BigDecimal(number.toString());
    }

    /**
     * 辅助类，处理中文数字转换成阿拉伯数字，利用递归算法
     *
     * @param money
     * @return
     */
    public static BigDecimal getDigitalNum(String money) {
        BigDecimal result = BigDecimal.ZERO;
        if ((money == null || money.trim().length() <= 0)) {
            return result;
        }
        //匹配大写金额的单位
        for (int i = 0; i < RMB_UNIT.length; i++) {
            //查找字符中的简繁单位
            int index = money.lastIndexOf(RMB_UNIT[i]) == -1 ? money.lastIndexOf(BIG_RMB_UNIT[i])
                    : money.lastIndexOf(RMB_UNIT[i]);
            if (index >= 0) {
                String pre_money = money.substring(0, index);//截取当前单位前面的中文字符串
                money = money.substring(index + 1);//截取当前单位后面的字符串，进行下一次迭代比较
                if ((pre_money == null || pre_money.length() <= 0) && TO_CARDINAL_NUMBERS[i].intValue() == 10) {
                    //处理拾开头的特殊字符例如拾、十
                    result = result.add(TO_CARDINAL_NUMBERS[i]);
                } else {
                    //对当前单位截取的前面的字符递归处理
                    result = result.add(getDigitalNum(pre_money).multiply(TO_CARDINAL_NUMBERS[i]));
                }
            }
        }
        //如果不带单位直接阿拉伯数字匹配替换
        if (money != null && money.length() > 0) {
            result = result.add(getArabicNumByBig(money));
        }
        return result;
    }

    /**
     * 辅助类中文数字转为对应阿拉伯数字
     *
     * @param big
     * @return
     */
    public static BigDecimal getArabicNumByBig(String big) {
        BigDecimal result = BigDecimal.ZERO;
        for (int j = 0; j < RMB_NUMBERS.length; j++) {
            big = big.replaceAll(RMB_NUMBERS[j], TO_ARABIC_NUMBERS[j].toString());//中文小写替换
            big = big.replaceAll(BIG_RMB_NUMBERS[j], TO_ARABIC_NUMBERS[j].toString());//中文大写替换
        }
        try {
            //由于现在没有对输入的字符串进行限制（必须只包含大写中文数字），故需过滤非数字的字符
            result = new BigDecimal(BidStringUtils.getNumeric(big));
        } catch (Exception e) {
            result = BigDecimal.ZERO;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(ChineseToNumber("人民币叁佰陆拾陆万零玖佰零贰元柒角整（¥"));
        System.out.println(ChineseToNumber("一千零一十一亿零一千零八元五分"));
        System.out.println(ChineseToNumber("壹仟零壹拾壹亿零壹仟零捌元伍分"));
    }

}

