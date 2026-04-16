package com.kavish.core.driver;

import com.kavish.core.config.ConfigFactory;
import com.kavish.core.constants.BrowserType;
import com.kavish.core.logging.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.logging.Level;

public final class DriverFactory {

    private DriverFactory() {}

    public static WebDriver initDriver() {
        BrowserType browser = resolveBrowser();
        boolean headless = ConfigFactory.getConfig().headless();
        String gridUrl = ConfigFactory.getConfig().gridUrl();
        boolean remote = shouldUseRemoteDriver(gridUrl);
        String thread = Thread.currentThread().getName();

        Log.info("DriverFactory [" + thread + "] — initialising " + browser
                + " | headless=" + headless);

        WebDriver driver;

        try {
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
                    driver = remote
                            ? new RemoteWebDriver(new URL(gridUrl), chromeOptions)
                            : new ChromeDriver(chromeOptions);
                    break;

                // ── FIREFOX ───────────────────────────────────────────────────
                case FIREFOX:
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");
                    if (headless) {
                        firefoxOptions.addArguments("--headless");
                    }
                    driver = remote
                            ? new RemoteWebDriver(new URL(gridUrl), firefoxOptions)
                            : new FirefoxDriver(firefoxOptions);
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
                    driver = remote
                            ? new RemoteWebDriver(new URL(gridUrl), edgeOptions)
                            : new EdgeDriver(edgeOptions);
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

    private static BrowserType resolveBrowser() {
        String browser = ConfigFactory.getConfig().browser();
        if (browser == null || browser.isBlank()) {
            return BrowserType.CHROME;
        }
        return BrowserType.valueOf(browser.trim().toUpperCase());
    }

    private static boolean shouldUseRemoteDriver(String gridUrl) {
        String explicitGridUrl = firstNonBlank(
                System.getProperty("grid.url"),
                System.getenv("GRID_URL"));

        if (explicitGridUrl != null) {
            return !isLocalModeValue(explicitGridUrl);
        }

        if (gridUrl == null || gridUrl.isBlank()) {
            return false;
        }

        String normalized = gridUrl.trim().toLowerCase();
        return !normalized.equals("local")
                && !normalized.equals("http://localhost:4444")
                && !normalized.equals("http://127.0.0.1:4444");
    }

    private static boolean isLocalModeValue(String value) {
        String normalized = value.trim().toLowerCase();
        return normalized.equals("local") || normalized.equals("false");
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) return first.trim();
        if (second != null && !second.isBlank()) return second.trim();
        return null;
    }
}
