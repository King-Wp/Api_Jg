package com.comp.components.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PutBidUtils {

	private static final Logger log = LoggerFactory.getLogger(PutBidUtils.class);
	//提取pdf文本
	private static final String PDF_URL = "http://10.203.7.252:19002/PDF_OCR";
	//提取名称关键词
	private static final String KEYWORD_URL = "http://10.203.7.251:19002/CKE_out_results";
	//提取公司名称分词
	private static final String COMPANY_URL = "http://10.203.7.251:19002/fenci_out_result";

	//post请求
	public static String doPost(String url ,String json) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String res = "";

		try {
			HttpPost post = new HttpPost(url);
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			post.setEntity(entity);
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0"); // 设置请求头消息User-Agent
			response = httpClient.execute(post);

			res = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (IOException e) {
			log.info("第三方AI服务访问异常");
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	//post请求
	public static String[] doKeyPost(String keyword) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String[] str = null;
		String res = "";

		//封装数据
		JSONArray arrs = new JSONArray();
		Map<String, Object> map = new HashMap<>();
		map.put("company", keyword);
		arrs.add(map);
		String json =arrs.toJSONString();

		try {
			HttpPost post = new HttpPost(COMPANY_URL);
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			post.setEntity(entity);
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0"); // 设置请求头消息User-Agent
			response = httpClient.execute(post);

			res = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (IOException e) {
			log.info("第三方AI服务访问异常");
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//处理数据
		JSONObject resultObj = JSONObject.parseObject(res);
		String code = (String) resultObj.get("code");
		JSONArray lists = resultObj.getJSONArray("data");
		if ("200".equals(code) && lists.size() > 0) {
			JSONObject data = (JSONObject) lists.get(0);
			str =StringUtils.JsonToArray(data.getJSONArray("company"));
		}
		return str;
	}

	//模拟浏览器post请求
	public static String postByHttpClient(String url, String... strings) {

		String result ="";
		try {
			if (strings.length % 2 != 0)
				return null;// 参数不合法
			HttpClient client = HttpClients.createDefault();// 打开浏览器
			HttpPost post = new HttpPost(url);// 输入网址
			List parameters = new ArrayList();
			for (int i = 0; i < strings.length; i += 2) {
// 封装表单
				parameters.add(new BasicNameValuePair(strings[i], strings[i + 1]));
			}
// 将参数传入post
			post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
			HttpResponse response = client.execute(post); // 执行post
			HttpEntity entity = response.getEntity(); // 获取响应数据
			result = EntityUtils.toString(entity); // 将响应数据转成字符串

		} catch (UnsupportedEncodingException e) {
// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 *
	 *  发送文件MultipartFile类型的参数请求第三方接口
	 * @param url  请求url
	 * @param file 参数
	 * @return 字符流
	 * @throws IOException
	 */
	public static String JPost(String url, MultipartFile file) throws IOException {
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("file", new FileSystemResource(convert(file)));
		HttpHeaders headers = new HttpHeaders();
		headers.add("accept", "*/*");
		headers.add("connection", "Keep-Alive");
		headers.add("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		headers.add("Accept-Charset", "utf-8");
		headers.add("Content-Type", "application/json; charset=utf-8");
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		org.springframework.http.HttpEntity<MultiValueMap<String, Object>> requestEntity = new org.springframework.http.HttpEntity<>(bodyMap, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		String body = response.getBody();
		return body;
	}

	/*页面上传文件*/
	public static String PDFPost(String url, MultipartFile file) throws IOException {
		String text ="";
		try {
			MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
			bodyMap.add("file", new FileSystemResource(convert(file)));
			HttpHeaders headers = new HttpHeaders();
			headers.add("accept", "*/*");
			headers.add("connection", "Keep-Alive");
			headers.add("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			headers.add("Accept-Charset", "utf-8");
			headers.add("Content-Type", "application/json; charset=utf-8");
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			org.springframework.http.HttpEntity<MultiValueMap<String, Object>> requestEntity = new org.springframework.http.HttpEntity<>(bodyMap, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			String body = response.getBody();

			//将返回数据转换成json
			JSONObject jsonObject = JSONObject.parseObject(body);
			String message = (jsonObject.get("msg")).toString();
			String code = (jsonObject.get("code")).toString();
			JSONArray data = jsonObject.getJSONArray("data");
			if ("200".equals(code)) {
				text = StringUtils.JsonToString(data);
			}else{
				text="第三方AI服务访问异常";
			}
		}catch (Throwable e){
			log.info("第三方AI服务访问异常");
			e.printStackTrace();
		}
		return text;
	}

	public static void main(String[] args) {
		//String path ="/profile/avatar/2024/01/29/6c70b2dc-baa4-446f-8cbe-1eedc765ef2e.pdf";
		//String path ="/profile/avatar/2024/01/30/0452e761-50ad-4b3f-b78d-b08e5af8c50e.pdf";
		//String path ="/profile/upload/2024/03/12/7642624b-3300-4f1f-bfc1-9711d3d03d3a.pdf";
		String path ="/profile/upload/2024/03/12/7642624b-3300-4f1f-bfc1-9711d3d03d3a.pdf";
		//String path ="/profile/avatar/2024/01/30/9775f8cb-856e-4508-8572-c28022dfd8de.pdf";
		String res = getFileContent(path);
		System.out.println(res);
	}
	/**
	 * 通过文件路径提取文件文本
	 */
	public static String getFileContent(String path){
		String res ="";
		if (path.contains("/profile")){//处理window路径
			path = path.replace("/profile",  "C:/ruoyi/uploadPath");
		}else{
			path = RuoYiConfig.getProfile() +path;
		}
		File info = new File(path);
		if(info.exists())
			res = FiletoPost(PDF_URL, info);
		log.info(path +" .\n" +res);
		return res;
	}

	//本地文件
	public static String FiletoPost(String url, File file) {
		String text ="";
		try {
			MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
			bodyMap.add("file", new FileSystemResource(file));
			HttpHeaders headers = new HttpHeaders();
			headers.add("accept", "*/*");
			headers.add("connection", "Keep-Alive");
			headers.add("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			headers.add("Accept-Charset", "utf-8");
			headers.add("Content-Type", "application/json; charset=utf-8");
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			org.springframework.http.HttpEntity<MultiValueMap<String, Object>> requestEntity = new org.springframework.http.HttpEntity<>(bodyMap, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			String body = response.getBody();

			//将返回数据转换成json
			JSONObject jsonObject = JSONObject.parseObject(body);
			String message = (jsonObject.get("msg")).toString();
			String code = (jsonObject.get("code")).toString();
			JSONArray data = jsonObject.getJSONArray("data");
			if ("200".equals(code)) {
				text = StringUtils.JsonToString(data);
			}else{
				text="第三方AI服务访问异常";
			}
		}catch (Throwable e){
			log.info("第三方AI服务访问异常");
			e.printStackTrace();
		}
		return text;
	}

	/**
	 * 接收处理传过来的文件
	 * @param file MultipartFile 类型的文件
	 * @return
	 */
	public static File convert(MultipartFile file) {
		// 本地资源路径
		String localPath = RuoYiConfig.getProfile();
		File convFile = new File(localPath+"/"+file.getOriginalFilename());
		if(!convFile.exists()) {
			//先得到文件的上级目录，并创建上级目录，在创建文件
			convFile.getParentFile().mkdir();
		}
		try {
			convFile.createNewFile();
			log.info(convFile.getCanonicalPath());
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return convFile;
	}

}
