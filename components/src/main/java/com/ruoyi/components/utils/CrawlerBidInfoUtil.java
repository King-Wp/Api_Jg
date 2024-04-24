package com.ruoyi.components.utils;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SnowflakeIdWorker;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.UserAgentUtil;
import com.ruoyi.components.domain.PersonaBids;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CrawlerBidInfoUtil {

	private static final Logger logger = LoggerFactory.getLogger(CrawlerBidInfoUtil.class);
	/*招标公告*/
	public static List<PersonaBids> getSpiderBidInfoList(String url1, String url2, String keyword) {
		String url= url1+ 1 +url2;
		System.out.println("URL "+url);
		List<PersonaBids> bids = new ArrayList<>();
		// html的Document对象
		Document doc = null;
		//todo
		try {
			//System.out.println(url);
			//doc = Jsoup.connect(url).userAgent(UserAgentUtil.getUserAgent()).get();
			doc = Jsoup.connect(url).userAgent(UserAgentUtil.getUserAgent()).timeout(30000).get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int num =0;
		try {
			Element div2 = doc.select("div[class=vT_z]").get(3);
			Element div0 = div2.selectFirst("div");
			Element div1 = div0.selectFirst("div");
			Element p1  = div1.selectFirst("p");
			Elements spans = p1.select("span");
			num = Integer.parseInt(spans.get(1).text()) ;
		}catch (Throwable e){

		}
		logger.info("查询记录数: "+num);
		//有记录数时分页
		if (num>0) {
			int pages1 = num / 20 + 1;
			//System.out.println("分页数量: " + pages1);
			for (int page = 1; page <= pages1; page++) {
				//Elements pages = Objects.requireNonNull(doc).select("p[class=pager]");
				try {
					//重新获取分页数据
					Thread.sleep(3000);
					url =url1+ page +url2;
					doc = Jsoup.connect(url).userAgent(UserAgentUtil.getUserAgent()).timeout(30000).get();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
				//继续中断任务
//				try {

					Elements elements = Objects.requireNonNull(doc).select("ul[class=vT-srch-result-list-bid]");

					Elements li = elements.select("li");
					for (int i = 0; i < li.size(); i++) {
						PersonaBids info = new PersonaBids();
						Element element = li.get(i);
						//详情url
						String href = element.selectFirst("a").attr("href");
						//标题
						String title = element.selectFirst("a").text();

						//获取列表 发布时间,采购人,代理机构
						Element span = li.get(i).selectFirst("span");
						String sf = span.selectFirst("a").text();
						String abs = span.text();
						String[] array = abs.split("\\|");
						String time0 = array[0];
						//2022.09.19 17:50:59
						time0 = time0.replace(".", "-");
						Date ptime = DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", time0);
						String purch = array[1];
						// 采购人：中国农业银行股份有限公司漳州分行
						if (StringUtils.isNotEmpty(purch) && purch.contains("采购人：")) {
							String str1 = purch.substring(0, purch.indexOf("采购人："));
							String str2 = purch.substring(str1.length() + 4, purch.length());
							purch = str2;
						}
						String proxy = array[2];
						// 代理机构：公诚管理咨询有限公司 中标公告
						if (StringUtils.isNotEmpty(proxy) && (proxy.contains("代理机构：") || proxy.contains("中标公告"))) {
							String str1 = proxy.substring(0, proxy.indexOf(" 公开招标公告"));
							String str2 = str1.substring(0, str1.indexOf("代理机构："));
							String str3 = str1.substring(str2.length() + 5, str1.length());
							proxy = str3;
						}
						Elements strongList = element.select("strong");
						//tblx:投标类型  tbxz:类目
						String tblx = "";
						String tbxz = "";
						if (strongList.size() == 0) {
							tblx = "";
							tbxz = "";
						} else if (strongList.size() == 1) {
							tblx = strongList.get(0).text();
							tbxz = "";
						} else if (strongList.size() == 2) {
							tblx = strongList.get(0).text();
							tbxz = strongList.get(1).text();
						}
						info.setIntro(time0);
						info.setLink(href);
						info.setTitle(title);
						info.setType(tblx);
						info.setAbs(tbxz);
						info.setPurchaser(purch);
						info.setProxy(proxy);
						info.setProvince(sf);
						info.setKeyword(keyword);
						info.setSource("http://www.ccgp.gov.cn");

						Document docDetail = null;
						try {
							Thread.sleep(500);
							docDetail = Jsoup.connect(href).userAgent(UserAgentUtil.getUserAgent()).get();
						} catch (IOException | InterruptedException e) {
							continue;
						}
						Element timedoc = docDetail.getElementById("pubTime");
						String time = "";
						if (timedoc != null) {
							time = timedoc.text();
						}
						Element table = docDetail.selectFirst("table");

						//获取详情前端代码
						Element div = docDetail.selectFirst("div[class=vF_detail_content]");
						String detail = "";
						if (div != null) {
							detail = div.toString();
						}
						//获取中标单位
//					Elements ps = docDetail.select("p");
//					String win = "";
//					for (Element p : ps) {
//						if (p.text().indexOf("供应商名称：") != -1) {
//							win = p.text();
//						}
//					}
//					//供应商名称：恒晟集团有限公司
//					if (StringUtils.isNotEmpty(win) && win.contains("供应商名称：")){
//						String str1 = win.substring(0, win.indexOf("供应商名称："));
//						String str2 = win.substring(str1.length()+6, win.length());
//						win= str2;
//					}
						//预算金额
						String money = "";
						Elements tds = table.select("td");
						for (int t = 0; t < tds.size(); t++) {
							String tdStr = tds.get(t).text();
							//if (tdStr.indexOf("￥")!=-1){
							if (tdStr.lastIndexOf("￥") != -1) {
								money = tdStr;
								//break;
							}
						}
						//￥107.883135 万元（人民币）
						if (money.contains("万元")) {
							money = StringUtils.checkNum(money);
							BigDecimal hum = new BigDecimal(money);
							BigDecimal sum = hum.multiply(new BigDecimal(10000)).setScale(2,2);
							money = sum.toString();
						} else {
							money = StringUtils.checkNum(money);
						}

						// 使用雪花算法生成id
						long id = SnowflakeIdWorker.generateId();
						info.setUuid(id + "");
						//info.setContent(detail);
						//info.setBidWinner(win);
						info.setPublishTime(ptime);
						info.setBidAmount(money);
						bids.add(info);
					}
					//不做处理
//				} catch (Throwable e) {
//
//				}
			}
		}
		return bids;
	}

	/*中标公告*/
	public static List<PersonaBids> getSpiderInfoList(String url1, String url2, String keyword) {
		String url= url1 +1+ url2;
		System.out.println("URL "+url);
		List<PersonaBids> bids = new ArrayList<>();
		// html的Document对象
		Document doc = null;
		//todo
		try {
			//System.out.println(url);
			//doc = Jsoup.connect(url).userAgent(UserAgentUtil.getUserAgent()).get();
			doc = Jsoup.connect(url).userAgent(UserAgentUtil.getUserAgent()).timeout(30000).get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int num =0;
//		try {
			Element div2 = doc.select("div[class=vT_z]").get(3);
			Element div0 = div2.selectFirst("div");
			Element div1 = div0.selectFirst("div");
			Element p1  = div1.selectFirst("p");
			Elements spans = p1.select("span");
			num = Integer.parseInt(spans.get(1).text()) ;
//		}catch (Throwable e){
//
//		}
		logger.info("查询记录数: "+num);
		//有记录数时分页
		if (num>0) {
			int pages1 = num / 20 + 1;
			//System.out.println("分页数量: " + pages1);

			for (int page = 1; page <= pages1; page++) {
				//Elements pages = Objects.requireNonNull(doc).select("p[class=pager]");
				try {
					//重新获取分页数据
					Thread.sleep(3000);
					url =url1+ page +url2;
					doc = Jsoup.connect(url).userAgent(UserAgentUtil.getUserAgent()).timeout(30000).get();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}

				//继续中断任务
//				try {

					Elements elements = Objects.requireNonNull(doc).select("ul[class=vT-srch-result-list-bid]");

					Elements li = elements.select("li");
					for (int i = 0; i < li.size(); i++) {
						PersonaBids info = new PersonaBids();
						Element element = li.get(i);
						//详情url
						String href = element.selectFirst("a").attr("href");
						//标题
						String title = element.selectFirst("a").text();

						//获取列表 发布时间,采购人,代理机构
						Element span = li.get(i).selectFirst("span");
						String sf = span.selectFirst("a").text();
						String abs = span.text();
						String[] array = abs.split("\\|");
						String time0 = array[0];
						//2022.09.19 17:50:59
						time0 = time0.replace(".", "-");
						Date ptime = DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", time0);
						String purch = array[1];
						// 采购人：中国农业银行股份有限公司漳州分行
						if (StringUtils.isNotEmpty(purch) && purch.contains("采购人：")) {
							String str1 = purch.substring(0, purch.indexOf("采购人："));
							String str2 = purch.substring(str1.length() + 4, purch.length());
							purch = str2;
						}
						String proxy = array[2];
						// 代理机构：公诚管理咨询有限公司 中标公告
						if (StringUtils.isNotEmpty(proxy) && (proxy.contains("代理机构：") || proxy.contains("中标公告"))) {
							String str1 = proxy.substring(0, proxy.indexOf(" 中标公告"));
							String str2 = str1.substring(0, str1.indexOf("代理机构："));
							String str3 = str1.substring(str2.length() + 5, str1.length());
							proxy = str3;
						}
						Elements strongList = element.select("strong");
						//tblx:投标类型  tbxz:类目
						String tblx = "";
						String tbxz = "";
						if (strongList.size() == 0) {
							tblx = "";
							tbxz = "";
						} else if (strongList.size() == 1) {
							tblx = strongList.get(0).text();
							tbxz = "";
						} else if (strongList.size() == 2) {
							tblx = strongList.get(0).text();
							tbxz = strongList.get(1).text();
						}
						info.setIntro(time0);
						info.setLink(href);
						info.setTitle(title);
						info.setType(tblx);
						info.setAbs(tbxz);
						info.setPurchaser(purch);
						info.setProxy(proxy);
						info.setProvince(sf);
						info.setKeyword(keyword);
						info.setSource("http://www.ccgp.gov.cn");

						Document docDetail = null;
						try {
							Thread.sleep(500);
							docDetail = Jsoup.connect(href).userAgent(UserAgentUtil.getUserAgent()).get();
						} catch (IOException | InterruptedException e) {
							continue;
						}
						Element timedoc = docDetail.getElementById("pubTime");
						String time = "";
						if (timedoc != null) {
							time = timedoc.text();
						}
						Element table = docDetail.selectFirst("table");

						//获取详情前端代码
						Element div = docDetail.selectFirst("div[class=vF_detail_content]");
						String detail = "";
						if (div != null) {
							detail = div.toString();
						}
						//获取中标单位
						Elements ps = docDetail.select("p");
						String win = "";
						for (Element p : ps) {
							if (p.text().indexOf("供应商名称：") != -1) {
								win = p.text();
							}
						}
						//供应商名称：恒晟集团有限公司
						if (StringUtils.isNotEmpty(win) && win.contains("供应商名称：")) {
							String str1 = win.substring(0, win.indexOf("供应商名称："));
							String str2 = win.substring(str1.length() + 6, win.length());
							win = str2;
						}
						String money = "";
						Elements tds = table.select("td");
						for (int t = 0; t < tds.size(); t++) {
							String tdStr = tds.get(t).text();
							//if (tdStr.indexOf("￥")!=-1){
							if (tdStr.lastIndexOf("￥") != -1) {
								money = tdStr;
								//break;
							}
						}
						//￥107.883135 万元（人民币）
						if (money.contains("万元")) {
							money = StringUtils.checkNum(money);
							BigDecimal hum = new BigDecimal(money);
							BigDecimal sum = hum.multiply(new BigDecimal(10000)).setScale(2,2);
							money = sum.toString();
						} else {
							money = StringUtils.checkNum(money);
						}

						// 使用雪花算法生成id
						long id = SnowflakeIdWorker.generateId();
						info.setUuid(id + "");
						//info.setContent(detail);
						info.setBidWinner(win);
						info.setPublishTime(ptime);
						info.setBidAmount(money);
						bids.add(info);
					}
					//不做处理
//				}catch (Throwable e){
//
//				}
			}
		}
		return bids;
	}

	public static void main(String[] args) {
		String str="￥1778.918750 万元（人民币）";
		String money =StringUtils.checkNum(str);
		System.out.println(money);
		BigDecimal hum = new BigDecimal(money);
		System.out.println(hum);
		BigDecimal sum = hum.multiply(new BigDecimal(10000)).setScale(2);
		System.out.println(sum);
	}

}
