package com.ruoyi.components.utils;


import com.ruoyi.components.domain.bo.PublicBiddingURL;

import java.util.ArrayList;

/**
 * 招投标常量
 */
public class BidConstants {
    /**
     * 用于描述中标金额的属性字典(在不含有表格的html中获取招投标信息)
     */
    public static final String[] AMOUNT_PROPERTIES = {"中标价：", "中标报价：", "中标金额：", "中标（成交）金额：", "中标价（元）：",
            "中标价（万元）：", "投标报价：", "成交金额：", "中标金额（元）：", "中标金额（人民币）：", "中标金额为：",
            "中标单位投标报价金额：", "中标总造价（元）", "中标金额:", "中标金额：", "中标总报价："};

    /**
     * 用于描述中标单位名称的属性字典(在不含有表格的html中获取招投标信息)
     */
    public static final String[] COMPANY_PROPERTIES = {"中标人：","中标人名称：", "中标人名称：", "中标供应商名称：", "中标供应商：", "投标人名称：",
            "供应商名称：", "成交人名称：", "成交单位：", "成交单位名称：", "中标单位名称：", "供应商名称:", "成交供应商：", "中标单位："};

    /**
     * 用于描述中标金额的属性字典(在含有表格的html中获取招投标信息)
     */
    public static final String[] AMOUNT_PROPERTIES_TABLE = {"中标价", "中标报价", "中标金额", "成交金额", "中标价元", "中标价万元", "金额元",
            "中标成交金额元", "金额万元", "投标报价", "中标金额元", "中标金额万元", "中标金额人民币", "中标总造价元", "中标总金额元"};

    /**
     * 用于描述中标单位名称的属性字典(在含有表格的html中获取招投标信息)
     */
    public static final String[] COMPANY_PROPERTIES_TABLE = {"中标人", "中标供应商名称", "中标供应商", "中标人名称", "供应商名称",
            "成交人名称", "中标单位", "成交单位", "中标单位名称"};

    /**
     * 用于描述采购人名称的属性字典(关键字在数组中的下标的顺序需要注意，会命中最后一个字符相同的关键字)
     */
    public static final String[] PURCHASER_PROPERTIES = {"招标人或招标代理机构：","招标人或招标代理机构：（公章）", "招标人（盖章）：", "招标人（公章）：", "招标人或招标代理机构：（公章）", "建设单位", "建设单位: ", "特此公告。", "采购人：", "采购人名称：",
            "采购单位：", "招标人或招标代理机构（公章）：", "1.采购人信息" + " " + "名 称：",
            "招 标 人：", "招   标  人：", "招标人: ", "招标人:", "招   标   人：", "招标人：", "招  标  人：", "采购单位名称：",
            "采购人信息 名称：", "1.采购人信息 名称：", " 1.采购人信息                名    称：",
            "招标单位：", "采购人名称: ", "采购人： 名 称：", "采购人信息 名 称：", "招标人信息 名 称：", "采购人信息       名    称：", "采购人信息 名  称：",
            "采购人信息        名称：","凡对本次公告内容提出询问，请按以下方式联系。 1.名 称：",  "凡对本次公告内容提出询问，请按以下方式联系。 1. 名 称：", "凡对本次公告内容提出询问，请按以下方式联系。 1.  名 称：",
            "采购人信息         名 称：", "采购人信息 名    称：", "采购人信息            名   称：",
            "采购人信息： 名 称：", "采购人： 名    称：", "采购人信息： 名称：", "采购人信息 名 称：", "采购人信息     名    称："};
    public static final String[] PURCHASER_PROPERTIES_TABLE = {"采购人", "采购人名称", "建设单位", "招标人", "招标人或招标代理机构（公章）", "1.采购人信息" + " "
            + "名 称", "招 标 人", "招标单位"};

    public static final String[] SUB_BID_KEYWORD = {"分标", "标段"};

    //广西公共资源交易中心的招标公告url
    public static ArrayList<PublicBiddingURL> getPublicBiddingURL(){
        ArrayList<PublicBiddingURL> publicBiddingURLS = new ArrayList<>();

        //添加"工程建设URL"
        publicBiddingURLS.add(new PublicBiddingURL("84165","房建市政","招标公告"));
        publicBiddingURLS.add(new PublicBiddingURL("84165","铁路工程","招标公告"));
        publicBiddingURLS.add(new PublicBiddingURL("84165","交通工程","交易公告"));
        publicBiddingURLS.add(new PublicBiddingURL("84165","水利工程","交易公告"));

        //添加"政府采购URL"
        publicBiddingURLS.add(new PublicBiddingURL("84129","公开招标","采购公告"));
        return publicBiddingURLS;
    }
}
