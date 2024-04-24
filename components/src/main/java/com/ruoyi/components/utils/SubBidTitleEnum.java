package com.ruoyi.components.utils;

public enum SubBidTitleEnum {
    DEFAULT(-1,'π'),
    CAPITAL_ENGLISH(1, 'A'),
    LOWERCASE_ENGLISH(2, 'a'),
    ROMAN_NUMERALS(3, 'Ⅰ');

    private Integer value;
    private char desc;

    SubBidTitleEnum(Integer value, char desc) {
        this.desc = desc;
        this.value = value;
    }

    public static SubBidTitleEnum getSubBidTitleEnumByValue(Integer value) {
        for (SubBidTitleEnum c : SubBidTitleEnum.values()) {
            if (c.value.equals(value)) {
                return c;
            }
        }
        return SubBidTitleEnum.DEFAULT;
    }

    public static char getDescByValue(Integer value) {
        for (SubBidTitleEnum c : SubBidTitleEnum.values()) {
            if (c.value.equals(value)) {
                return c.desc;
            }
        }
        return '0';
    }

    /**
     * 获取输入字段中包含的desc字符串
     * @param str 输入字符
     * @return desc字符串
     */
    public static char hasDesc(String str){
        for (SubBidTitleEnum c : SubBidTitleEnum.values()) {
            String s = String.valueOf(c.desc);
            if (str.contains(s)) {
                return c.desc;
            }
        }
        return 'π';
    }
}
