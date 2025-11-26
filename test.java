package tests;

import com.aventstack.extentreports.*;
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
import java.nio.file.*;

public class SingleTest {

    private WebDriver driver;
    private static ExtentReports extent;
    private ExtentTest test;

    // -----------------------------
    // EXTENT REPORT SETUP
    // -----------------------------
    @BeforeSuite
    public void setupExtent() {
        System.out.println("📄 Creating Extent Report...");
        ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-report/ExtentReport.html");

        extent = new ExtentReports();
        extent.attachReporter(spark);

        System.out.println("📄 Extent Report initialized at target/extent-report/");
    }

    // -----------------------------
    // DRIVER SETUP BEFORE EACH TEST
    // -----------------------------
    @BeforeMethod
    public void setupDriver(Method method) {

        test = extent.createTest(method.getName());

        System.out.println("➡ Setting up ChromeDriver for test: " + method.getName());

        WebDriverManager.chromedriver().avoidBrowserDetection().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        try {
            driver = new ChromeDriver(options);
            System.out.println("✅ ChromeDriver started successfully");
        } catch (Exception e) {
            System.out.println("❌ CHROME FAILED TO START");
            e.printStackTrace();
            throw e;  // Make sure test fails if Chrome fails
        }
    }

    // -----------------------------
    // TAKE SCREENSHOT
    // -----------------------------
    private void takeScreenshot(String name) throws IOException {
        Path folder = Path.of("target/extent-report/screenshots");
        Files.createDirectories(folder);

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path dest = folder.resolve(name + ".png");

        Files.copy(src.toPath(), dest);

        System.out.println("📸 Screenshot saved: " + dest.toAbsolutePath());

        test.addScreenCaptureFromPath("screenshots/" + name + ".png");
    }

    // -----------------------------
    // AFTER EACH TEST LOGIC
    // -----------------------------
    @AfterMethod
    public void tearDownMethod(ITestResult result) throws IOException {
        String name = result.getMethod().getMethodName();

        if (result.getStatus() == ITestResult.FAILURE) {
            System.out.println("❌ Test FAILED: " + name);
            System.out.println("❌ ERROR: " + result.getThrowable());
            test.fail(result.getThrowable());
        } else {
            System.out.println("✅ Test PASSED: " + name);
        }

        takeScreenshot(name);

        if (driver != null) {
            driver.quit();
        }
    }

    // -----------------------------
    // FLUSH REPORT
    // -----------------------------
    @AfterSuite
    public void flushReport() {
        System.out.println("📄 Flushing Extent Report...");
        extent.flush();
        System.out.println("📄 Extent Report Flushed!");
    }

    // -----------------------------
    // SAMPLE TEST CASES
    // -----------------------------
    @Test
    public void openGoogle() {
        driver.get("https://google.com");
        test.info("Opened Google");
    }

    @Test
    public void openYouTube() {
        driver.get("https://youtube.com");
        test.info("Opened YouTube");
    }

    @Test
    public void openBing() {
        driver.get("https://bing.com");
        test.info("Opened Bing");
    }
}
