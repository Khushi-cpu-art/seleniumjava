package tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SampleTest {

    private static ExtentReports extent;
    private ExtentTest test;
    private WebDriver driver;

    @BeforeSuite
    public void setupExtent() {
        ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-report/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @BeforeMethod
    public void setupDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @Test
    public void openHomePage() throws IOException {
        test = extent.createTest("Open Home Page");
        driver.get("https://theysaidso.com");
        takeScreenshot("HomePage");
        test.pass("Home page opened and screenshot taken");
    }

    @Test
    public void scrollPage() throws IOException {
        test = extent.createTest("Scroll Page");
        driver.get("https://theysaidso.com");
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1000)");
        takeScreenshot("ScrolledPage");
        test.pass("Page scrolled and screenshot taken");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @AfterSuite
    public void tearDownExtent() {
        if (extent != null) extent.flush();
    }

    private void takeScreenshot(String name) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path dest = Path.of("target/extent-report/screenshots", name + ".png");
        Files.createDirectories(dest.getParent());
        Files.copy(screenshot.toPath(), dest);
        test.addScreenCaptureFromPath("screenshots/" + name + ".png");
    }
}
