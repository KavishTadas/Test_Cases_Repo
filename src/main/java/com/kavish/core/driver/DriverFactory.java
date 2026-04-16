package com.kavish.core.driver;

import com.kavish.core.config.ConfigFactory;
import com.kavish.core.constants.BrowserType;
import com.kavish.core.logging.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.logging.Level;

public final class DriverFactory {

    private DriverFactory() {}

    public static WebDriver initDriver() {
        BrowserType browser = BrowserType.valueOf(
                ConfigFactory.getConfig().browser().trim().toUpperCase());
        boolean headless = ConfigFactory.getConfig().headless();
        String thread = Thread.currentThread().getName();

        Log.info("DriverFactory [" + thread + "] — initialising " + browser
                + " | headless=" + headless);

        WebDriver driver;

        try {
            URL gridUrl = new URL(ConfigFactory.getConfig().gridUrl());

            switch (browser) {

                // ── CHROME ────────────────────────────────────────────────────
                case CHROME:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--window-size=1920,1080");
                    if (headless) {
                        chromeOptions.addArguments("--headless=new");
                    }
                    LoggingPreferences logPrefs = new LoggingPreferences();
                    logPrefs.enable(LogType.BROWSER, Level.ALL);
                    chromeOptions.setCapability("goog:loggingPrefs", logPrefs);
                    driver = new RemoteWebDriver(gridUrl, chromeOptions);
                    break;

                // ── FIREFOX ───────────────────────────────────────────────────
                case FIREFOX:
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--no-sandbox");
                    firefoxOptions.addArguments("--disable-dev-shm-usage");
                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");
                    if (headless) {
                        firefoxOptions.addArguments("--headless");
                    }
                    driver = new RemoteWebDriver(gridUrl, firefoxOptions);
                    break;

                // ── EDGE ──────────────────────────────────────────────────────
                case EDGE:
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--no-sandbox");
                    edgeOptions.addArguments("--disable-dev-shm-usage");
                    edgeOptions.addArguments("--disable-gpu");
                    edgeOptions.addArguments("--window-size=1920,1080");
                    if (headless) {
                        edgeOptions.addArguments("--headless=new");
                    }
                    driver = new RemoteWebDriver(gridUrl, edgeOptions);
                    break;

                default:
                    throw new RuntimeException("Browser not supported: " + browser);
            }

        } catch (Exception e) {
            // Log before rethrowing so framework.log always has the root cause
            // even if the test runner swallows the stack trace.
            Log.error("DriverFactory [" + thread + "] — session creation FAILED"
                    + " | browser=" + browser
                    + " | gridUrl=" + ConfigFactory.getConfig().gridUrl()
                    + " | " + e.getMessage(), e);
            throw new RuntimeException("Failed to create driver session for "
                    + browser + ": " + e.getMessage(), e);
        }

        DriverManager.setDriver(driver);
        Log.info("DriverFactory [" + thread + "] — session ready | sessionId="
                + ((RemoteWebDriver) driver).getSessionId());
        return driver;
    }
}