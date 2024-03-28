package com.ruoyi.common.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ruoyi.common.enums.UrlAddressEnum.*;

/**
 * @author: 11653
 * @createTime: 2024/03/21 14:50
 * @package: com.ruoyi.common.utils
 * @description:
 */
public class HttpApiUtils {

    /**
     * 发送get请求获取天眼查网页信息
     * @param url 网址
     * @param param 公司名称
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String sendTycGet(String url, String param) throws UnsupportedEncodingException {
        String result = "";
        String urlName = url + "?" + "key=" + param;
        try {
            URL realURL = new URL(urlName);
            URLConnection conn = realURL.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
            //TYCID（需要获取天眼查ID，可能存在访问次数限制，限制手段——汉字输入）
            //TYCID需要定时更新，目前的有效期是两年
            conn.setRequestProperty("cookie", "HWWAFSESTIME=;csrfToken=;HWWAFSESID=;TYCID=6a30f5c0664a11ed8026153652183d0b");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 发送post请求获取天眼查网页信息 使用token
     * token会失效，失效后需要重新登录
     * @param name 公司名称
     * @return 返回第一条数据
     * @throws UnsupportedEncodingException
     */
    public Map<String, String> PostTycApi (String name) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        long currentTimestamp = System.currentTimeMillis();
        String jsonBody = "{\"keyword\" : \""+ name +"\"}";
        JSONObject jsonObject = JSON.parseObject(jsonBody);

        String url = "https://capi.tianyancha.com/cloud-tempest/search/suggest/v3?_=" + currentTimestamp;
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            //token会更新
            httpPost.addHeader("X-Auth-Token",
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxODc3NzIzMjMyNCIsImlhdCI6MTcxMDE0MTY1MCwiZXhwIjoxNzEyNzMzNjUwfQ.wF_chyLiUJQxMm2OhlT88vKQRUn1C2b-8ufENc9TjEuABQxZ8d2mVS2yyde4byGMQ68B7qGj7-YhAo98fC5dng");
            httpPost.addHeader("X-Tycid", "12d2ed60aba911ee9f17b153529da940");
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Version", "TYC-Web");
            httpPost.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
            // 创建请求内容
            StringEntity entity = new StringEntity(String.valueOf(jsonObject), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONArray data = JSON.parseObject(resultString).getJSONArray("data");

        List<Map<String, String>> objects = new ArrayList<>();
        for (Object obj : data) {
            Map<String, String> resultMap = new HashMap<>();
            JSONObject rt = (JSONObject) obj;
            for (Map.Entry<String, Object> entry : rt.entrySet()) {
                if (ObjectUtils.isEmpty(entry.getValue())){
                    continue;
                }
                resultMap.put(entry.getKey(), entry.getValue().toString());
            }
            objects.add(resultMap);
        }
        return objects.get(0);
    }


    public static String executeGetCompanyId(String companyId) {
        String url = BASE_INFO_V2.toString();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setParameter("keyword", companyId);

            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", TOKEN.getUrl());

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String executeGet(String suffix) {
        String token = TOKEN.getUrl();
        String url = APP_BK_INFO + suffix;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", token);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity, "UTF-8");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * http get请求
     * @param url 接口url
     * @param token token
     * @return  返回接口数据
     */
    public static String executeGet(String url, String token) {
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        String result = null;
        try {

            HttpGet get = new HttpGet(url);
            // 设置header
            get.setHeader("Authorization",token);
            // 设置类型
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }

    public static String getMessageByUrlToken(String companyId){
        String url = PROFILE + "profile?keyword=" + companyId;
        String result="";
        try {
            // 根据地址获取请求
            HttpGet request = new HttpGet(url);//这⾥发送get请求
            // 获取当前客户端对象
            request.setHeader("Authorization", TOKEN.getUrl());
            HttpClient httpClient = new DefaultHttpClient();
            // 通过请求对象获取响应对象
            HttpResponse response = httpClient.execute(request);
            // 判断⽹络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result= EntityUtils.toString(response.getEntity(),"utf-8"); }
        } catch (Exception e) {
            // TODO Auto-generated catch block e.printStackTrace();
            e.printStackTrace();
        }
        return result;
    }

    public static String getCompanyDetailSpider(String companyId){
        String docPubUrl = COMPANY + companyId;
        Document doc = null;
        String companyProfile = "";
        // 利用jsoup连接目标url网页获取整个html对象
        try {
            doc = Jsoup.connect(docPubUrl).userAgent(UserAgentUtil.getUserAgent()).get();
            //防止频繁访问（模拟网络延迟）
            int temp = (int)(Math.random()*10000);
            Thread.sleep(temp);
            if (ObjectUtils.isNotEmpty(doc)) {
                //获取主页面的HTML对象
                Elements span = doc.select("div[class^=index_detail-linewrap__AKtCa index_-intro__ma3Qd]").select("span");
                if (!span.isEmpty()){
                    companyProfile = doc.select("div[class^=index_detail-linewrap__AKtCa index_-intro__ma3Qd]").select("span").get(1).text();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return companyProfile;
    }

    /**
     * 获取工商信息的logo
     * @param companyId
     * @return
     */
    public static JSONObject getBusinessLogoByTyc(String companyId){
        String docPubUrl = COMPANY + companyId;
            // 利用jsoup连接目标url网页获取整个html对象
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
