package tests;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MultiStepExtentTest {

    // ============================================================
    // 🔵 1. Extent Report Setup
    // ============================================================
    private static ExtentReports extent;
    private static ExtentTest test;
    private WebDriver driver;

    @BeforeSuite
    public void setupExtentReport() {
        // Report output folder
        String reportPath = "extent-report/ExtentReport.html";
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

        // Basic report config
        spark.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.STANDARD);
        spark.config().setDocumentTitle("Automation Test Report");
        spark.config().setReportName("Selenium Execution Report");

        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    // ============================================================
    // 🔵 2. Setup Selenium WebDriver
    // ============================================================
    @BeforeClass
    public void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--window-size=1920,1080"
        );

        driver = new ChromeDriver(options);
    }

    @AfterClass
    public void tearDownDriver() {
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Chrome closed");
        }
    }

    @AfterSuite
    public void flushExtent() {
        extent.flush();
    }

    // ============================================================
    // 🔵 3. Screenshot Logic (Highlighted)
    // ============================================================
    private String takeScreenshot(String testName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            File dir = new File("extent-report/screenshots");
            dir.mkdirs();

            File dest = new File(dir, testName.replaceAll("[^a-zA-Z0-9]", "_") + ".png");
            Files.copy(screenshot.toPath(), dest.toPath());

            return dest.getAbsolutePath();   // important for Extent image
        } catch (IOException e) {
            return null;
        }
    }

    // ============================================================
    // 🔵 4. Attach screenshot to Extent Report after each test
    // ============================================================
    @AfterMethod
    public void handleTestResult(ITestResult result) {

        String screenshotPath = takeScreenshot(result.getName());

        switch (result.getStatus()) {

            case ITestResult.SUCCESS:
                test.pass("✔ PASSED");
                test.addScreenCaptureFromPath(screenshotPath);
                break;

            case ITestResult.FAILURE:
                test.fail(MarkupHelper.createLabel("❌ FAILED", ExtentColor.RED));
                test.fail(result.getThrowable());
                test.addScreenCaptureFromPath(screenshotPath);
                break;

            case ITestResult.SKIP:
                test.skip("⚠ SKIPPED");
                break;
        }
    }

    // ============================================================
    // 🔵 5. SELENIUM TEST CASES (Same as your JS test)
    // ============================================================
    @Test
    public void step1_openHomepage() {
        test = extent.createTest("Step 1: Open homepage");
        driver.get("https://theysaidso.com");
        test.info("Opened homepage");
    }

    @Test
    public void step2_scrollDown() throws InterruptedException {
        test = extent.createTest("Step 2: Scroll down");
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1000)");
        Thread.sleep(1000);
        test.info("Scrolled down 1000px");
    }

    @Test
    public void step3_scrollToTop() throws InterruptedException {
        test = extent.createTest("Step 3: Scroll to top");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0)");
        Thread.sleep(1000);
        test.info("Scrolled to top");
    }

    @Test
    public void step4_getTitle() {
        test = extent.createTest("Step 4: Get title");
        String title = driver.getTitle();
        test.info("Page title: " + title);
    }

    @Test
    public void step5_hoverFooter() throws Exception {
        test = extent.createTest("Step 5: Hover over footer");

        try {
            WebElement footer = driver.findElement(By.cssSelector("footer"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", footer);
            test.info("Found and hovered footer");
        } catch (Exception e) {
            test.warning("⚠ Footer not found");
        }
    }

    @Test
    public void step6_finalScreenshot() {
        test = extent.createTest("Step 6: Final screenshot");
        test.info("Taking final screenshot");
    }
}
