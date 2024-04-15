package com.ruoyi.components.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.BidStringUtils;
import com.ruoyi.common.utils.UserAgentUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static com.ruoyi.common.enums.UrlAddressEnum.TOKEN_API;

/**
 * @author: 11653
 * @createTime: 2024/03/21 14:50
 * @package: com.ruoyi.common.utils
 * @description:
 */
public class HttpApiUtils {

    public static String executeGetCompanyId(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", TOKEN_API.getVal());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String executeGet(String url,String token) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", token);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMessageByUrlToken(String url) {
        String result = "";
        try {
            // 根据地址获取请求
            HttpGet request = new HttpGet(url);//这⾥发送get请求
            // 获取当前客户端对象
            request.setHeader("Authorization", TOKEN_API.getVal());
            HttpClient httpClient = new DefaultHttpClient();
            // 通过请求对象获取响应对象
            HttpResponse response = httpClient.execute(request);
            // 判断⽹络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block e.printStackTrace();
            e.printStackTrace();
        }
        return result;
    }

    public static String getCompanyDetailSpider(String companyId) {
        String docPubUrl = "https://www.tianyancha.com/company/" + companyId;
        Document doc = null;
        String companyProfile = "";
        // 利用jsoup连接目标url网页获取整个html对象
        try {
            doc = Jsoup.connect(docPubUrl).userAgent(UserAgentUtil.getUserAgent()).get();
            //防止频繁访问（模拟网络延迟）
            int temp = (int) (Math.random() * 10000);
            Thread.sleep(temp);
            if (ObjectUtils.isNotEmpty(doc)) {
                //获取主页面的HTML对象
                Elements span = doc.select("div[class^=index_detail-linewrap__AKtCa index_-intro__ma3Qd]").select("span");
                if (!span.isEmpty()) {
                    companyProfile = doc.select("div[class^=index_detail-linewrap__AKtCa index_-intro__ma3Qd]").select("span").get(1).text();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return companyProfile;
    }

    /**
     * 获取工商信息的logo
     *
     * @param companyId
     * @return
     */
    public static JSONObject getBusinessLogoByTyc(String companyId) {
        String docPubUrl = "https://www.tianyancha.com/company/" + companyId;
        try {
            Document doc = Jsoup.connect(docPubUrl).userAgent(UserAgentUtil.getUserAgent()).get();
            if (ObjectUtils.isNotEmpty(doc)) {
                Element nextData = doc.getElementById("__NEXT_DATA__");
                if (nextData != null) {
                    String s = BidStringUtils.subStringBetween(nextData.toString(), "<script id=\"__NEXT_DATA__\" type=\"application/json\">", "</script>");
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    JSONArray queries = jsonObject.getJSONObject("props").getJSONObject("pageProps").getJSONObject("dehydratedState").getJSONArray("queries");
                    if (!queries.isEmpty()) {
                        return queries.getJSONObject(0).getJSONObject("state").getJSONObject("data").getJSONObject("data");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new JSONObject();
    }

}
