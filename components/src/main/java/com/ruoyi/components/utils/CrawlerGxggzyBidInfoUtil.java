package com.ruoyi.components.utils;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.SnowflakeIdWorker;
import com.ruoyi.components.domain.PersonaBids;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HBF
 * @date 2022-10-18 15:53
 * @description: 爬取全国公共资源交易平台（广西壮族自治区）招标公告、中标公告工具类 gxggzy
 */

public class CrawlerGxggzyBidInfoUtil {

    /**
     * 根据客户名称和查询条件爬取全国公共资源交易平台（广西壮族自治区）上的招标、中标信息
     * @param customer 客户名称
     * @param equal 查询条件
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param driver 浏览器驱动
     * @return 爬取结果
     */
    public static List<PersonaBids> crawlerGxggzyBids(String customer, String equal, String startTime,
                                                      String endTime, WebDriver driver) {
        // 存放爬取成功的结果
        List<PersonaBids> personaBidsList = new ArrayList<>();

        // 存放爬取失败的结果
        // Map<String, String> errMap = new HashMap<>();
        // 存放爬取详情失败的结果
        // Map<String, String> detailErrMap = new HashMap<>();

        // 数据来源：全国公共资源交易平台（广西壮族自治区）
        String source = "http://ggzy.jgswj.gxzf.gov.cn/";
        // 全国公共资源交易平台（广西壮族自治区）
        String baseUrl = "http://ggzy.jgswj.gxzf.gov.cn/inteligentsearchgxes/rest/esinteligentsearch/getFullTextDataNew";
        // 详情链接
        String link = "";
        // 起始下标
        int pn = 0;
        // 总记录数
        int count = 0;
        // 请求体
        String requestBody = "";

        // 详情数据
        // Map<String, JSONArray> detailMap = new HashMap<>();

        JSONArray jsonArray = new JSONArray();
        requestBody = getRequestBody(customer, equal, pn, startTime, endTime);
        String result;
        JSONObject jsonObject;
        // 查询记录数
        Integer totalCount = null;
        // 页数
        int pageNum = 0;
        int timeOutCount = 0;
        boolean isConnectException = false;

        while (totalCount == null && timeOutCount <= 5 && !isConnectException) {
            try {
                Thread.sleep(1000);
                // 获取第一页数据，可能存在请求超时的情况
                result = PutBidUtils.doPost(baseUrl, requestBody);
                jsonObject = JSONObject.parseObject(result);
                // 获取查询的总记录数
                totalCount = jsonObject.getJSONObject("result").getInteger("totalcount");
                // System.out.println("当前线程是：" + Thread.currentThread().getName());
                // System.out.println("["+ Thread.currentThread().getName() +"]" + customer +"：有" + totalCount + "条" + equal + "记录");
                if (totalCount == 0) continue;

                // System.out.println("第1页");
                count = count + totalCount;
//                    if (totalCount / 15 == 0) {
//                        throw new Exception("模拟第1页出错");
//                    }
                jsonArray.addAll(jsonObject.getJSONObject("result").getJSONArray("records"));
                // objectList.addAll(jsonArray);
                timeOutCount = 0;
                isConnectException = false;
                // 计算总页数，向上取整
                pageNum = totalCount % 15 == 0 ? totalCount / 15 : totalCount / 15 + 1;
                // 前面已经获取第一页，这里从第二页开始遍历
                for (int i = 1; i < pageNum; i++) {
                    Integer tempCount = null;
                    while (tempCount == null && timeOutCount <= 5 && !isConnectException) {
                        try {
                            // 每一页都可能存在请求超时的情况
                            // System.out.println("第"+ (i + 1) +"页");
                            pn = i * 15;
                            requestBody = getRequestBody(customer, equal, pn, startTime, endTime);
                            Thread.sleep(1000);
                            result = PutBidUtils.doPost(baseUrl, requestBody);
                            jsonObject = JSONObject.parseObject(result);
                            tempCount = jsonObject.getJSONObject("result").getInteger("totalcount");
                            jsonArray.addAll(jsonObject.getJSONObject("result").getJSONArray("records"));
                            timeOutCount = 0;
                            isConnectException = false;
                            // jsonArray = jsonObject.getJSONObject("result").getJSONArray("records");
                        } catch (Exception exception) {
                            // 将爬取失败的结果记录下来
                            // errMap.put(equalMap.get(equal) + " 第" + (i + 1) + "页", customer);
                            // {key: "01001001001/15", value: "中国电信"}
                            timeOutCount++;
                            isConnectException = true;
                            // errMap.put(equal + "," + pn, customer);
                        }
                    }
                }
            } catch (Exception exception) {
                // 将爬取失败的结果记录下来
                // {key: "中国电信,01001001001,0", value: "错误信息"}
                timeOutCount++;
                isConnectException = true;
                // errMap.put(customer + "," + equal + "," + pn, exception.toString());
            }
//            finally {
//                try {
//                    FileOutputStream outStream = new FileOutputStream("E:/1.txt");
//                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
//                    objectOutputStream.writeObject(errMap.toString());
//                    outStream.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }

//        if (jsonArray.size() != 0) {
//            detailMap.put(customer, jsonArray);
//        }

        int notFound = 0;

        if (jsonArray.size() != 0) {
            for (Object value : jsonArray) {
                String page = null;
                int detailTimeOutCount = 0;
                boolean isDetailConnectException = false;
                JSONObject o = (JSONObject) value;
                String title = o.getString("title");

                if (!isInclude(title, customer)) continue;
//                if (isPreBidWinning(title)) continue;
//                if (isAbandonedBid(title)) continue;
//                if (isClarification(title)) continue;
//                if (isPreExamine(title)) continue;
                if (filterTitle(title)) continue;

                while (page == null && detailTimeOutCount <= 5 && !isDetailConnectException) {
                    try {
                        // Thread.sleep(1000);
                        String categoryNum = o.getString("categorynum");
                        // if (getAbs(categoryNum) == null) continue;
                        // System.out.println(categoryNum);
                        // System.out.println(customer + "：" + getAbs(categoryNum));

                        // 可能存在请求超时的情况
                        long id = SnowflakeIdWorker.generateId();
                        String infoId = o.getString("infoid");
                        String province = o.getString("areaname");
                        String abs = getAbs(categoryNum);
                        int length = abs.split("/").length;
                        link = "http://ggzy.jgswj.gxzf.gov.cn/gxggzy/projectDetails.html?infoid=" + infoId + "&categorynum=" + categoryNum;

                        page = getPageBySelenium(driver, link);
                        Document document = Jsoup.parse(page);
                        Element main = document.selectFirst("div[class=ewb-details-info]");
                        if (null == main) {
                            // System.out.println("=============404=============");
                            notFound++;
                            continue;
                        }

                        PersonaBids personaBids = new PersonaBids();
                        String content = Jsoup.clean(main.html(), Whitelist.none());
                        // System.out.println(content);
                        content = content.replaceAll("&nbsp;+", "")
                                .replaceAll(" +", "");
                        personaBids.setUuid(id+"");
                        personaBids.setProvince(province);
                        personaBids.setLink(link);
                        personaBids.setAbs(abs);
                        personaBids.setType(abs.split("/")[length - 1]);
                        personaBids.setPublishTime(o.getDate("infodatepx"));
                        personaBids.setTitle(title);
                        personaBids.setSource(source);
                        personaBids.setKeyword(customer);
                        personaBids.setContent(content);
                        personaBids.setPurchaser(matchPurchaser(content));
                        personaBids.setProxy(matchProxy(content));
                        personaBids.setBidWinner(matchBidWinner(content));
                        personaBids.setBidAmount(matchBidAmount(content));
                        // System.out.println("["+ Thread.currentThread().getName() +"]" + personaBids.getTitle());
                        personaBidsList.add(personaBids);
                        detailTimeOutCount = 0;
                        isDetailConnectException = false;
                    } catch (Exception exception) {
                        // 将爬取失败的结果记录下来
                        // errMap.put(link, customer);
                        // detailErrMap.put(link, customer);
                        detailTimeOutCount++;
                        isDetailConnectException = true;
                    }
                }
            }
        }

//        try {
//            File file = new File("E:/crawlerGxggzyBidLog.txt");
//            // 如果文件不存在则创建文件
//            if(!file.exists()){
//                file.createNewFile();
//            }
//
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            PrintStream printStream = new PrintStream(fileOutputStream);
//            // 获取要被写入内容的文件（将内容写入文件）
//            // 如果构造方法的第二个参数为true，则新内容会添加到旧内容之后
////            FileWriter writer = new FileWriter(file.getName(), true);
//            PrintWriter writer = new PrintWriter(printStream);
//            //将文本写入文件
//            for (String key : errMap.keySet()) {
//                writer.write("keyword= "+ key + "，value= " + errMap.get(key) + "\r\n");
//                writer.flush();
//            }
//            writer.write(customer + "一共有" + count + "条记录，实际爬取有" + personaBidsList.size() + "条，404有"
//                            + notFound + "条\r\n");
//            //关闭文件写入对象
//            writer.close();
//            printStream.close();
//            fileOutputStream.close();
//            // 关闭浏览器驱动
//            if (driver != null) {
//                driver.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return personaBidsList;
    }

    /**
     * 加载浏览器驱动
     * @return 浏览器驱动
     */
    public static WebDriver getDriver() {
        // 设置 ChromeDriver 的存放位置
		System.getProperties().setProperty("webdriver.chrome.driver", "E:/Documents/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        // 禁用沙箱
        options.addArguments("--no-sandbox");
        // 禁用开发者shm
        options.addArguments("--disable-dev-shm-usage");
        // 无头浏览器，这样不会打开浏览器窗口
        options.addArguments("--headless");

        return new ChromeDriver(options);
    }

    /**
     * 封装请求体
     * @param keyword 查询关键词
     * @param equal 查询条件
     * @param pn 起始下标
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 结果
     */
    public static String getRequestBody(String keyword, String equal, int pn, String startTime, String endTime) {
        return "{\n" +
                "    \"token\": \"\",\n" +
                "    \"pn\": " + pn + ",\n" +
                "    \"rn\": 15,\n" +
                "    \"sdt\": \"\",\n" +
                "    \"edt\": \"\",\n" +
                "    \"wd\": \"" + keyword + "\",\n" +
                "    \"inc_wd\": \"\",\n" +
                "    \"exc_wd\": \"\",\n" +
                "    \"fields\": \"title\",\n" +
                "    \"cnum\": \"001\",\n" +
                "    \"sort\": \"{\\\"infodatepx\\\":\\\"0\\\"}\",\n" +
                "    \"ssort\": \"title\",\n" +
                "    \"cl\": 200,\n" +
                "    \"terminal\": \"\",\n" +
                "    \"condition\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"categorynum\",\n" +
                "            \"equal\": \"" + equal + "\",\n" +
                "            \"notEqual\": null,\n" +
                "            \"equalList\": null,\n" +
                "            \"notEqualList\": null,\n" +
                "            \"isLike\": true,\n" +
                "            \"likeType\": 2\n" +
                "        }\n" +
                "    ],\n" +
                "    \"time\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"infodatepx\",\n" +
                "            \"startTime\": \"" + startTime + "\",\n" +
                "            \"endTime\": \"" + endTime + "\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"highlights\": \"\",\n" +
                "    \"statistics\": null,\n" +
                "    \"unionCondition\": null,\n" +
                "    \"accuracy\": \"\",\n" +
                "    \"noParticiple\": \"0\",\n" +
                "    \"searchRange\": null,\n" +
                "    \"isBusiness\": \"1\"\n" +
                "}";
    }

    /**
     * 获取摘要信息
     * @param equal 查询条件
     * @return 摘要信息
     */
    public static String getAbs(String equal) {
        String abs;
        if (GxggzyBidConstant.ENGINEERING_CONSTRUCTION.containsKey(equal)) {
            abs = GxggzyBidConstant.ENGINEERING_CONSTRUCTION.get(equal);
            return abs;
        }
        if (GxggzyBidConstant.GOVERNMENT_PROCUREMENT.containsKey(equal)) {
            abs = GxggzyBidConstant.GOVERNMENT_PROCUREMENT.get(equal);
            return abs;
        }
        if (GxggzyBidConstant.OTHER_TRANSACTIONS.containsKey(equal)) {
            abs = GxggzyBidConstant.OTHER_TRANSACTIONS.get(equal);
            return abs;
        }
        return null;
    }

    /**
     * 使用浏览器驱动访问详情页
     * @param driver 驱动
     * @param url 详情页地址
     * @return 结果
     */
    public static String getPageBySelenium(WebDriver driver, String url) {
        String page = null;
        driver.get(url);
        try {
            Thread.sleep(4000);
            WebElement element = driver.findElement(By.ById.id("iframeList"));
            if (element != null) {
                // 跳转到iframe页面内
                driver.switchTo().frame(element);
                page = driver.getPageSource();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return page;
    }

    /**
     * 判断公告标题是否包含客户单位
     * @param title 公告标题
     * @param customer 客户单位
     * @return 结果
     */
    public static boolean isInclude(String title, String customer) {
        return title.contains(customer);
    }

    /**
     * 判断公告是否预中标公告或者中标候选人公告
     * @param title 公告标题
     * @return 结果
     */
    public static boolean isPreBidWinning(String title) {
        return title.contains("预中标") || title.contains("候选人");
    }

    /**
     * 判断公告是否废标或者流标
     * @param title 公告标题
     * @return 结果
     */
    public static boolean isAbandonedBid(String title) {
        return title.contains("废标") || title.contains("流标");
    }

    /**
     * 判断公告是否澄清公告、更正公告、更改公告
     * @param title 公告标题
     * @return 结果
     */
    public static boolean isClarification(String title) {
        return title.contains("更正") || title.contains("澄清") || title.contains("更改");
    }

    /**
     * 判断公告是否资格预审公告
     * @param title 公告标题
     * @return 结果
     */
    public static boolean isPreExamine(String title) {
        return title.contains("资格预审");
    }

    /**
     * 从标题内容过滤不符合的招标、中标公告
     * @param title 公告标题
     * @return 结果
     */
    public static boolean filterTitle(String title) {
        return title.contains("预中标") || title.contains("候选人") || title.contains("废标") || title.contains("流标")
                || title.contains("更正") || title.contains("澄清") || title.contains("更改") || title.contains("资格预审")
                || title.contains("终止") || title.contains("停止");
    }

    /**
     * 提取招标人
     * @param s 公告内容
     * @return 招标人
     */
    public static String matchPurchaser(String s) {
        String purchaser = null;
        //Matcher matcher = Pattern.compile("(招标人|招标单位|建设单位|采购人(名称)?|采购单位(名称)?)((([:： ])?.*?)(商铺|幼儿园|小学|中学|学院|大学|分公司|子公司|公司|支行|银行|街道办|居委会|委员会|管理所|管理站|保持站|指挥部|分队|支队|大队|派出所|办公室|检察院|局|单位|中心|医院|部队|政府))").matcher(s);
        Matcher matcher = Pattern.compile("((招( )*标( )*|建( )*设( )*|采( )*购( )*)(人( )*|单( )*位( )*)(名( )*称( )*)?)" +
                "((([:： ]).*?)(商铺|幼儿园|小学|中学|学院|大学|分公司|子公司|公司|支行|银行|街道办|居委会|委员会|管理所|管理站|保持站|指挥部|分队|支队|大队|派出所|办公室|检察院|局|单位|中心|医院|部队|政府))").matcher(s);
        if (matcher.find() && matcher.group().length() <= 25) {
            purchaser = matcher.group()
                    //.replaceAll("(招标人|招标单位|建设单位|采购人(名称)?|采购单位(名称)?)", "")
                    //.replaceAll("[:： ]", "")
                    .replaceAll("([:： ])", "")
                    .replaceAll("(招标|建设|采购)(人|单位)(名称)?", "");;
        }
        return purchaser;
    }

    /**
     * 提取招标代理机构
     * @param s 公告内容
     * @return 招标代理机构
     */
    public static String matchProxy(String s) {
        String proxy = null;
        //Matcher matcher = Pattern.compile("(代理机构)((([:： ])?.*?)(商铺|幼儿园|小学|中学|学院|大学|分公司|子公司|公司|支行|银行|街道办|居委会|委员会|管理所|管理站|保持站|指挥部|分队|支队|大队|派出所|办公室|检察院|局|单位|中心|医院|部队|政府))").matcher(s);
        Matcher matcher = Pattern.compile("(代( )*理( )*机( )*构( )*)((([:： ])?.*?)(商铺|幼儿园|小学|中学|学院|大学|分公司|子公司|公司|支行|银行|街道办|居委会|委员会|管理所|管理站|保持站|指挥部|分队|支队|大队|派出所|办公室|检察院|局|单位|中心|医院|部队|政府))").matcher(s);
        if (matcher.find() && matcher.group().length() <= 25) {
            proxy = matcher.group()
                    //.replaceAll("代理机构", "")
                    //.replaceAll("[:： ]", "")
                    .replaceAll("([:： ])", "")
                    .replaceAll("代理机构", "");
        }
        return proxy;
    }

    /**
     * 提取中标人
     * @param s 公告内容
     * @return 中标人
     */
    public static String matchBidWinner(String s) {
        String bidWinner = null;
        //Matcher matcher = Pattern.compile("(预)?(拟)?(中标人|中标单位)((([:： ])?.*?)(商铺|幼儿园|小学|中学|学院|大学|分公司|子公司|公司|支行|银行|街道办|居委会|委员会|管理所|管理站|保持站|指挥部|分队|支队|大队|派出所|办公室|检察院|局|单位|中心|医院|部队|政府))").matcher(s);
        Matcher matcher = Pattern.compile("((中( )*标( )*|采( )*购( )*)(人( )*|单( )*位( )*)(名( )*称( ))?([:：  ])?).*?" +
                "(幼儿园|小学|中学|学院|大学|分公司|子公司|公司|支行|银行|街道办|居委会|委员会|管理所|管理站|分队|支队|大队|派出所|办公室|检察院|局|单位|中心|医院|部队|政府)").matcher(s);

        if (matcher.find() && matcher.group().length() <= 25) {
            bidWinner = matcher.group()
                    //.replaceAll("(预)?(拟)?(中标人|中标单位)", "")
                    //.replaceAll("[:： ]", "");
                    .replaceAll("(中( )*标( )*|采( )*购( )*)(人( )*|单( )*位( )*)(名( )*称( ))?", "")
                    .replaceAll("中标金额（元）", "")
                    .replaceAll("勘察设计服务期限", "");
        }
        return bidWinner;
    }

    /**
     * 提取中标金额
     * @param s 公告内容
     * @return 中标金额
     */
    public static String matchBidAmount(String s) {
        String bidAmount = null;
        Matcher matcher = Pattern.compile("(中标价(格)?|中标金额|投标报价)(.*?([0-9]+(,)?)+(.[0-9]+)?(元|万|万元|\\D)?)").matcher(s);

        if (matcher.find()) {
            bidAmount = matcher.group().replaceAll("(中标价(格)?|中标金额|投标报价)", "")
                    .replaceAll("[:： （）(),，;；¥￥【】]", "")
                    .replaceAll("[\\u4e00-\\u9fa5]", "");
        }
        return bidAmount;
    }

}
