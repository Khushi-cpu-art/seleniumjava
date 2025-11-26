import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

public class test {

    private WebDriver driver;
    private static ExtentReports extent;
    private ExtentTest logger;

    // ================================
    // SETUP EXTENT REPORT
    // ================================
    @BeforeSuite
    public void setupReport() {
        System.out.println("📄 Setting up Extent Report...");

        ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-report/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);

        System.out.println("📁 Report folder created: target/extent-report/");
    }

    // ================================
    // SETUP CHROME BEFORE EACH TEST
    // ================================
    @BeforeMethod
    public void setup(Method method) {

        logger = extent.createTest(method.getName());

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        try {
            driver = new ChromeDriver(options);
            logger.info("Chrome launched");
        } catch (Exception e) {
            logger.fail("Chrome failed to start: " + e.getMessage());
            throw e;
        }
    }

    // ================================
    // UTILITY — TAKE SCREENSHOT
    // ================================
    public void capture(String name) throws IOException {
        Path screenshotsDir = Path.of("target/extent-report/screenshots");
        Files.createDirectories(screenshotsDir);

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path dest = screenshotsDir.resolve(name + ".png");
        Files.copy(src.toPath(), dest);

        logger.addScreenCaptureFromPath("screenshots/" + name + ".png");
    }

    // ================================
    // AFTER EVERY TEST
    // ================================
    @AfterMethod
    public void tearDown(ITestResult result, Method method) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.fail("Test Failed: " + result.getThrowable());
        } else {
            logger.pass("Test Passed");
        }

        capture(method.getName());

        if (driver != null) driver.quit();
    }

    // ================================
    // TEST CASES
    // ================================
    @Test
    public void openGoogle() {
        driver.get("https://google.com");
        logger.info("Opened Google");
    }

    @Test
    public void openYouTube() {
        driver.get("https://youtube.com");
        logger.info("Opened YouTube");
    }

    @Test
    public void openBing() {
        driver.get("https://bing.com");
        logger.info("Opened Bing");
    }

    // ================================
    // FLUSH REPORT
    // ================================
    @AfterSuite
    public void flush() {
        extent.flush();
        System.out.println("📄 Extent Report generated.");
    }
}
