package com.ruoyi.components.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author HBF
 * @date 2022-11-24 15:42
 * @description: Selenium工具类
 */
public class SeleniumUtil {

//    /**
//     * 政策文件下载存放的地址
//     */
//    private static String downloadFilePath = RuoYiConfig.getPolicyFileDownloadPath();
//
//    /**
//     * 浏览器驱动存放地址
//     */
//    public static String webdriverPath = RuoYiConfig.getPolicyFile() + "/chromedriver.exe";
//
//    public static String getDownloadFilePath() {
//        return downloadFilePath;
//    }
//
//    public static void setDownloadFilePath(String downloadFilePath) {
//        SeleniumUtil.downloadFilePath = downloadFilePath;
//    }
//
//    public static String getWebdriverPath() {
//        return webdriverPath;
//    }
//
//    public static void setWebdriverPath(String webdriverPath) {
//        SeleniumUtil.webdriverPath = webdriverPath;
//    }


    // 文件下载地址
    public static String downloadFilePath = "E:\\Documents\\downloadFile\\";
    // 浏览器驱动地址
    public static String webdriverPath = "E:/Documents/chromedriver.exe";

    /**
     * 加载浏览器驱动
     * @return 浏览器驱动
     */
    public static WebDriver getDriver() {
        // 设置 ChromeDriver 的存放位置
        System.getProperties().setProperty("webdriver.chrome.driver", webdriverPath);
        // 保存下载地址信息
        Map<String, Object> chromePrefs = new HashMap<>();
        // 禁止在chrome中查看pdf
        chromePrefs.put("plugins.plugins_disabled", new String[] { "Chrome PDF Viewer" });
        // 在启动相应链接或URI时下载pdf
        chromePrefs.put("plugins.always_open_pdf_externally", true);
        // 更改默认下载位置
        chromePrefs.put("download.default_directory", downloadFilePath);

        // 中设置下载路径信息，需要传入保存有下载路径的 HashMap
        ChromeOptions options = new ChromeOptions();

//        options.setBinary("C:/Program Files/Google/Chrome/Application/chrome.exe");
        options.setExperimentalOption("prefs", chromePrefs);
        // 禁用沙箱
        options.addArguments("--no-sandbox");
        // 禁用开发者shm
        options.addArguments("--disable-dev-shm-usage");
        // 无头浏览器，这样不会打开浏览器窗口
        options.addArguments("--headless","--disable-gpu");


        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
        // 依据 ChromeOptions 来产生 DesiredCapabilities，这时 DesiredCapabilities 就也具备了下载路径的信息了
//        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
//        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);

        return new ChromeDriver(options);
    }

    /**
     * 判断文件夹是否存在，不存在则新建
     */
    public static void createNewFolder() {
        File folder = new File(downloadFilePath);
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
    }

    /**
     * 通过文件名判断文件是否已经下载
     * @param fileName 文件名
     * @return 结果
     */
    public static Map<String, Object> isFileDownloaded(String fileName) {
        createNewFolder();
        Map<String, Object> map = new HashMap<>();
        boolean flag = false;
        String fileFullName = null;
        File dir = new File(downloadFilePath);
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                String name = file.getName();
                if (name.contains(fileName)) {
                    flag = true;
                    fileFullName = downloadFilePath + file.getName();
                }
            }
        }
        map.put("flag", flag);
        map.put("fileFullName", fileFullName);

        return map;
    }

    /**
     * 关闭浏览器驱动
     */
    public static void closeDriver(WebDriver driver) {
        driver.quit();
    }

    /**
     * 使用浏览器驱动访问网页模拟点击下载文件并重命名
     * @param driver 浏览器驱动
     * @param url 文件下载地址
     * @param fileType 下载的文件类型
     * @param newFileName 新文件名
     * @return 文件全名（包含路径）
     * @throws TimeoutException 查找元素超时异常
     */
    public static String downloadFileBySelenium(WebDriver driver, String url, String fileType, String newFileName) throws TimeoutException {
        driver.get(url);
        String fileFullName = null;
        WebElement fileDownBtn = null;
        // 下载 ".doc" 格式文件
        if (fileType.equals("20")) {
            // 等待元素可被点击
            WebDriverWait wait = new WebDriverWait(driver,5);
            fileDownBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fileDownBtn")));
            if (fileDownBtn != null) {
                // 点击下载文件
                fileDownBtn.click();
            }
        }
        // 监听文件下载，并重命名文件，获取文件全名
        fileFullName = getDownloadedFileFullName(downloadFilePath, newFileName, ".doc",".pdf");

        return fileFullName;
    }

    /**
     * 获取下载的文件的全名
     * @param filePath 文件下载路径
     * @param newFileName 新文件名
     * @param suffix 文件后缀名
     * @return 文件全名
     */
    public static String getDownloadedFileFullName(String filePath, String newFileName, String ...suffix) {
        String fileFullName = null;
        boolean valid = true;
        boolean found = false;

        //设置默认监听文件下载超时时间 30s
        long timeOut = 30;
        try {
            Path downloadFolderPath = Paths.get(filePath);
            WatchService watchService = FileSystems.getDefault().newWatchService();
            downloadFolderPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            long startTime = System.currentTimeMillis();
            do {
                WatchKey watchKey;
                watchKey = watchService.poll(timeOut, TimeUnit.SECONDS);
                long currentTime = (System.currentTimeMillis() - startTime) / 1000;
                if(currentTime > timeOut) {
                    System.out.println("未发现预期下载文件");
                    return fileFullName;
                }

                for (WatchEvent<?> event: watchKey.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                        String fileName = event.context().toString();
                        // System.out.println("有新文件创建：" + fileName);
                        for (String s : suffix) {
                            if(fileName.endsWith(s)) {
                                fileFullName = filePath + fileName;
                                // System.out.println("找到扩展名为 " + s + "的下载文件。" + "文件名是：" + fileName);
                                Thread.sleep(1000);
                                // 重命名文件
                                File file = new File(fileFullName);
                                String[] temp = fileName.split("\\.");
                                int length = temp.length;
                                File newFile = new File(filePath + newFileName + "." + temp[length - 1]);
                                file.renameTo(newFile);
                                fileFullName = filePath + newFile.getName();
                                found = true;
                                // System.out.println(file.getName() + "下载成功");
                                break;
                            }
                        }
                    }
                }
                if(found) {
                    return fileFullName;
                } else {
                    currentTime = (System.currentTimeMillis() - startTime) / 1000;
                    if(currentTime > timeOut) {
                        System.out.println("未能下载预期的文件");
                        return fileFullName;
                    }
                    valid = watchKey.reset();
                }
            } while (valid);
        } catch (InterruptedException e) {
            System.out.println("Interrupted error - " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("下载操作超时。未下载预期文件");
        } catch (Exception e) {
            System.out.println("Error occured - " + e.getMessage());
            e.printStackTrace();
        }

        return fileFullName;
    }

    public static void main(String[] args) {
//        renameDownloadedFile(downloadFilePath);
//        isFileDownloaded(downloadFilePath, "");
//        String downloadedDocumentName = getDownloadedDocumentName(downloadFilePath, ".doc");
//        System.out.println(downloadedDocumentName);
        //isFileDownloaded("桂政办发〔2022〕10号");
//        String fullName = downloadFileBySelenium(
//                getDriver(),
//                "http://www.gxzf.gov.cn/zfwj/zzqrmzfbgtwj_34828/2022ngzbwj/t11621803.shtml",
//                "20",
//                "广西壮族自治区人民政府办公厅关于印发广西地方法人金融机构高质量发展三年行动计划（2022—2024年）的通知（桂政办发〔2022〕20号）");
//        System.out.println(fullName);

    }

}
