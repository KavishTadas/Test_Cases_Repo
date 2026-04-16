package com.kavish.core.utils;

import com.kavish.core.config.ConfigFactory;
import com.kavish.core.driver.DriverManager;
import com.kavish.core.logging.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.List;
import java.util.stream.Collectors;

public final class BrowserUtils {

    private BrowserUtils() {}

    public static String getCurrentUrl() {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver == null) return "unavailable --- driver is null";
            return driver.getCurrentUrl();
        } catch (Exception e) {
            return "unavailable --- " + e.getMessage();
        }
    }

    public static String getPageSource() {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver == null) return "unavailable --- driver is null";
            return driver.getPageSource();
        } catch (Exception e) {
            return "unavailable --- " + e.getMessage();
        }
    }

    public static String getConsoleLogs() {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver == null) return "unavailable --- driver is null";

            // Firefox and Edge do not support the W3C log endpoint.
            // Calling it throws UnsupportedCommandException and pollutes logs.
            String browser = ConfigFactory.getConfig().browser();
            if (browser != null) {
                String b = browser.trim().toUpperCase();
                if (b.equals("FIREFOX") || b.equals("EDGE")) {
                    return "console log capture not supported on " + b
                            + " (W3C log endpoint removed from spec)";
                }
            }

            List<LogEntry> entries =
                    driver.manage().logs().get(LogType.BROWSER).getAll();
            if (entries.isEmpty()) return "no console entries captured";
            return entries.stream()
                    .map(e -> "[" + e.getLevel() + "] " + e.getMessage())
                    .collect(Collectors.joining("\n"));

        } catch (Exception e) {
            Log.info("Console log capture skipped: " + e.getMessage());
            return "unavailable --- " + e.getMessage();
        }
    }
}