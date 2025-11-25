package tests;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MultiStepTest {

    private WebDriver driver;
    private ExtentReports extent;
    private ExtentTest test;

    @BeforeSuite
    public void setupExtent() {
        extent = ExtentManager.getInstance();
    }

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");

        driver = new ChromeDriver(options);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @AfterSuite
    public void tearDownExtent() {
        extent.flush();
    }

    // ------------ Screenshot Utility ---------------
    private String takeScreenshot(String testName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            File screenshotDir = new File("extent-report/screenshots");
            screenshotDir.mkdirs();

            File dest = new File(screenshotDir, testName.replaceAll("[^a-zA-Z0-9]", "_") + ".png");
            Files.copy(screenshot.toPath(), dest.toPath());

            return dest.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    @AfterMethod
    public void captureScreenOnFailure(ITestResult result) {

        String screenshotPath = takeScreenshot(result.getName());

        if (result.getStatus() == ITestResult.FAILURE) {
            test.fail(MarkupHelper.createLabel(result.getName() + " FAILED", ExtentColor.RED));
            test.addScreenCaptureFromPath(screenshotPath);
        } else {
            test.pass("Test passed");
            test.addScreenCaptureFromPath(screenshotPath);
        }
    }

    // ------------------ TESTS -----------------------

    @Test
    public void step1_openHomepage() {
        test = extent.createTest("Step 1: Open homepage");
        driver.get("https://theysaidso.com");
    }

    @Test
    public void step2_scrollDown() throws InterruptedException {
        test = extent.createTest("Step 2: Scroll down");
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1000)");
        Thread.sleep(1000);
    }

    @Test
    public void step3_scrollToTop() throws InterruptedException {
        test = extent.createTest("Step 3: Scroll to top");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0)");
        Thread.sleep(1000);
    }

    @Test
    public void step4_getTitle() {
        test = extent.createTest("Step 4: Get title");
        test.info("Page title: " + driver.getTitle());
    }

    @Test
    public void step5_hoverFooter() throws Exception {
        test = extent.createTest("Step 5: Hover over footer");

        try {
            WebElement footer = driver.findElement(By.cssSelector("footer"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", footer);
        } catch (Exception e) {
            test.warning("Footer not found!");
        }
    }

    @Test
    public void step6_finalScreenshot() {
        test = extent.createTest("Step 6: Final screenshot");
    }
}
