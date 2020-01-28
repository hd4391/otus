package hw;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import hw.classes.ServerConfig;
import io.github.bonigarcia.wdm.WebDriverManager;

public class AppTest {

    private static InfluxDB influxDB;
    private static String dbName = "selenium";
    private static WebDriver driver;
    private static final Logger logger = LogManager.getLogger(AppTest.class);
    private static ServerConfig cfg = ConfigFactory.create(ServerConfig.class);

    @Rule
    public final TestRule watcher = new TestWatcher() {

        @Override
        protected void succeeded(Description description) {
            sendTestStatus(description, "succeeded");
        }

        @Override
        protected void failed(Throwable e, Description description) {
            sendTestStatus(description, "failed");
        }

        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
            sendTestStatus(description, "skipped");
        }

        private void sendTestStatus(Description description, String status) {
            Point point = Point
            .measurement("testResults")
            .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .tag("name", description.getMethodName())
            .tag("result", status)
            .addField("testCount", description.testCount())
            .build();

            influxDB.write(dbName, "autogen", point);
        }
    };

    @BeforeClass
    public static void setupClass() {
        influxDB = InfluxDBFactory.connect(cfg.hostname() + ":" + cfg.port(), "root", "root");
        WebDriverManager.chromedriver().setup();
        WebDriverManager.firefoxdriver().setup(); 
    }

    @AfterClass
    public static void teardown() {
        if (!driver.toString().contains("null")) {
            driver.quit();
        }
    }

     @Test
    public void testFail() {
        Assert.fail();
    }

    @Test
    public void testChrome() {
        driver = new ChromeDriver();
        driver.get("https://otus.ru");
        assertEquals("Онлайн‑курсы для профессионалов, дистанционное обучение современным профессиям", driver.getTitle());
        driver.quit(); 
    }

    @Test
    public void testFirefox() {
        driver = new FirefoxDriver();
        driver.get("https://otus.ru");
        assertEquals("Онлайн‑курсы для профессионалов, дистанционное обучение современным профессиям",
                driver.getTitle());
        driver.quit();
    }

    @Test
    public void logTest() {
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        logger.fatal("fatal");
    }
}