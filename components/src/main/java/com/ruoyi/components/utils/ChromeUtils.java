package com.ruoyi.components.utils;

import com.ruoyi.common.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Leeyq
 * @version 1.0
 * @date 2022年09月22日 11:37
 */
//@Component
public class ChromeUtils {

	private static final Logger log = LoggerFactory.getLogger(ChromeUtils.class);

	public static Map<String,Object> getPageBySelenium(String url){
		//测试bug
		//url ="http://zfcg.gxzf.gov.cn/ZcyAnnouncement/ZcyAnnouncement5/ZcyAnnouncement3014/RmCmZn3iVQ89nqgDjRkgeA==.html";
		System.out.println("URL "+url);
		Map<String,Object> map = new HashMap<>();
//		//应用驱动
//		WebDriverManager.chromedriver().setup();
//		//加载驱动
//		WebDriver driver = new ChromeDriver();
//		//指定页面地址
//		driver.get(url);
		try {
			// 设置 chromedirver 的存放位置
			System.getProperties().setProperty("webdriver.chrome.driver", "E:/Documents/chromedriver.exe");

			// 设置浏览器参数
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--no-sandbox");//禁用沙箱
			chromeOptions.addArguments("--disable-dev-shm-usage");//禁用开发者shm
			chromeOptions.addArguments("--headless"); //无头浏览器，这样不会打开浏览器窗口
			WebDriver webDriver = new ChromeDriver(chromeOptions);
			webDriver.get(url);

			//获取页面的iframe元素， “g_iframe”是元素的ID, 根据实际页面来获取
			WebElement iframe = webDriver.findElement(By.ById.id("iframe"));

			//跳转到iframe页面内
			webDriver.switchTo().frame(iframe);

			//返回iframe的全部页面html内容
			String content =webDriver.getPageSource();
			String company = "";
			String money = "";
			String cgrxx = "";
			String agency = "";

			Document doc = Jsoup.parse(content);
			if (doc != null) {
				Element table = doc.selectFirst("table");
				if (table != null) {
					Element moneyElement = table.selectFirst("td[class=code-summaryPrice]");
					if (moneyElement != null) {
						money = moneyElement.text();
						if (!StringUtils.isNumeric(money)){
							money = StringUtils.checkNum(money);
						}
					}
					Element companyElement = table.selectFirst("td[class=code-winningSupplierName]");
					if (companyElement != null) {
						company = companyElement.text();
					}
					Elements ps = doc.select("p");
					//获取采购人信息
					for (int j =0; j<=ps.size()-1; j++){
						String text = ps.get(j).text();
						if (text.contains("采购人信息")){
							String res =ps.get(j+1).text();
							if (res.contains("名") && res.contains("称：")) {
								//cgrxx = res.substring(3);
								cgrxx =res.substring(res.lastIndexOf("称：")+2);
							}
						}else if(text.contains("采购代理机构信息")){
							String str =ps.get(j+1).text();
							if (str.contains("名") && str.contains("称：")) {
								agency =str.substring(str.lastIndexOf("称：")+2);
							}
						}
					}
				} else {
					Elements ps = doc.select("p");
					for (Element p : ps) {
						String text = p.text();
						if (StringUtils.isNotEmpty(text)) {
							if (text.contains("供应商名称：")) {
								company=text;
							}
							if (text.contains("中标金额：") || text.contains("成交金额：")) {
								money =text;
							}
						}
					}
					//获取采购人信息
					for (int k =0; k<=ps.size()-1; k++){
						String text = ps.get(k).text();
						if (text.contains("采购人信息")){
							String res =ps.get(k+1).text();
							if (res.contains("名") && res.contains("称：")) {
								//cgrxx = res.substring(3);
								cgrxx =res.substring(res.lastIndexOf("称：")+2);
							}
						}else if(text.contains("采购代理机构信息")){
							String str =ps.get(k+1).text();
							if (str.contains("名") && str.contains("称：")) {
								agency =str.substring(str.lastIndexOf("称：")+2);
							}
						}
					}
				}
			}
			map.put("money", money);
			map.put("company", company);
			map.put("cgrxx", cgrxx);
			map.put("proxy", agency);
			webDriver.close();
		}catch (Throwable e){
			log.error("驱动异常 {}", e);
		}

		return map;
	}

	public static Map<String,Object> getPlanPageBySelenium(String url){
		//测试bug
		//url ="http://zfcg.gxzf.gov.cn/reformColumn/ZcyAnnouncement10016/winUWIsee/xAi4BXEYAP2g==.html";

		Map<String,Object> map = new HashMap<>();

		try{
			// 设置 chromedirver 的存放位置
			System.setProperty("webdriver.chrome.whitelistedIps", "");
			System.getProperties().setProperty("webdriver.chrome.driver", "E:/Documents/chromedriver.exe");
			// 设置浏览器参数
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--no-sandbox");//禁用沙箱
			chromeOptions.addArguments("--disable-dev-shm-usage");//禁用开发者shm
			chromeOptions.addArguments("--headless"); //无头浏览器，这样不会打开浏览器窗口
			WebDriver webDriver1 = new ChromeDriver(chromeOptions);

			webDriver1.get(url);
			System.out.println("--------------------- "+url);

			//获取页面的iframe元素， “g_iframe”是元素的ID, 根据实际页面来获取
			WebElement iframe = webDriver1.findElement(By.ById.id("iframe"));

			//跳转到iframe页面内
			webDriver1.switchTo().frame(iframe);

			//返回iframe的全部页面html内容
			String content =webDriver1.getPageSource();
			String name = "";
			String price = "";
			String date = "";
			String unit = "";

			Document doc = Jsoup.parse(content);
			if (doc != null) {
				Element table = doc.selectFirst("table");
				if (table != null) {
					Element nameElement = table.selectFirst("td[class=code-purchaseProjectName]");
					if (nameElement != null) {
						name = nameElement.text();
					}
					Element priceElement = table.selectFirst("td[class=code-budgetPrice]");
					if (priceElement != null) {
						price = priceElement.text();
					}
					Element dateElement = table.selectFirst("td[class=code-estimatedPurchaseTime]");
					if (dateElement != null) {
						date = dateElement.text();
					}
				}
				Element unitElement = doc.selectFirst(".code-singleChoicePurchaser");
				if (unitElement != null) {
					unit = unitElement.text();
				}
			}
			map.put("name", name);
			map.put("price", price);
			map.put("date", date);
			map.put("unit", unit);
			webDriver1.close();
		}catch (Throwable e){
			log.error("驱动异常 {}", e);
		}
		return map;
	}


	public static void main(String[] args) {
		String url = "http://zfcg.gxzf.gov.cn/ZcyAnnouncement/ZcyAnnouncement2/ZcyAnnouncement3004/2301667.html";
		ChromeUtils coo =new ChromeUtils();
		Map<String, Object> map = coo.getPlanPageBySelenium(url);
	}

}