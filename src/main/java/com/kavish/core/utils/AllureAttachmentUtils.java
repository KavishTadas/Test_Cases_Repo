package com.kavish.core.utils;

import com.kavish.core.config.ConfigFactory;
import com.kavish.core.config.FrameworkConfig;
import com.kavish.core.logging.Log;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public final class AllureAttachmentUtils {

    private AllureAttachmentUtils() {}

    public static void attachScreenshot(String testName) {
        try {
            String screenshotPath = ScreenshotUtils.capture(testName);
            if (screenshotPath != null && screenshotPath.endsWith(".png")) {
                try (InputStream inputStream = Files.newInputStream(Path.of(screenshotPath))) {
                    Allure.addAttachment(
                            "Screenshot --- " + testName,
                            "image/png",
                            inputStream,
                            ".png"
                    );
                    Allure.addAttachment("Screenshot Path", "text/plain", screenshotPath);
                    Log.info("attachScreenshot: saved screenshot path: " + screenshotPath);
                    return;
                } catch (Exception e) {
                    Log.warn("attachScreenshot: file attachment failed --- falling back to captureBytes(). Test: "
                            + testName + " | " + e.getMessage());
                }
            }

            byte[] screenshot = ScreenshotUtils.captureBytes();
            if (screenshot == null || screenshot.length == 0) {
                Log.warn("attachScreenshot: captureBytes() returned empty --- "
                        + "driver may have already been closed. Test: " + testName);
                return;
            }
            Allure.addAttachment(
                    "Screenshot --- " + testName,
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );
            Log.info("attachScreenshot: embedded in Allure for test: " + testName);
        } catch (Exception e) {
            Log.error("attachScreenshot FAILED for test: " + testName
                    + " | " + e.getMessage());
        }
    }

    public static void attachFailureReason(Throwable t) {
        try {
            String content = (t != null)
                    ? t.getClass().getName() + ": " + t.getMessage()
                    : "No exception captured";
            Allure.addAttachment("Failure Reason", "text/plain", content);
        } catch (Exception e) {
            Log.error("attachFailureReason FAILED: " + e.getMessage());
        }
    }

    public static void attachCurrentUrl() {
        try {
            Allure.addAttachment("Current URL", "text/plain",
                    BrowserUtils.getCurrentUrl());
        } catch (Exception e) {
            Log.error("attachCurrentUrl FAILED: " + e.getMessage());
        }
    }

    public static void attachEnvInfo() {
        try {
            attachEnvInfo(ConfigFactory.getConfig().service());
        } catch (Exception e) {
            Log.error("attachEnvInfo FAILED: " + e.getMessage());
        }
    }

    public static void attachEnvInfo(String resolvedService) {
        try {
            FrameworkConfig cfg = ConfigFactory.getConfig();
            String baseUrl;
            try { baseUrl = cfg.serviceBaseUrl(resolvedService); }
            catch (IllegalStateException ex) {
                baseUrl = "unavailable --- " + ex.getMessage();
            }

            String content =
                    "thread      : " + Thread.currentThread().getName() + "\n" +
                            "env         : " + cfg.env()          + "\n" +
                            "service     : " + resolvedService    + "\n" +
                            "baseUrl     : " + baseUrl            + "\n" +
                            "gridUrl     : " + cfg.gridUrl()      + "\n" +
                            "browser     : " + cfg.browser()      + "\n" +
                            "headless    : " + cfg.headless()     + "\n" +
                            "explicitWait: " + cfg.explicitWait() + "s";
            Allure.addAttachment("Env Info", "text/plain", content);
        } catch (Exception e) {
            Log.error("attachEnvInfo FAILED: " + e.getMessage());
        }
    }

    public static void attachPageSource() {
        try {
            String source = BrowserUtils.getPageSource();
            Allure.addAttachment("Page Source", "text/html",
                    new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)),
                    ".html");
        } catch (Exception e) {
            Log.error("attachPageSource FAILED: " + e.getMessage());
        }
    }

    public static void attachConsoleLogs() {
        try {
            Allure.addAttachment("Browser Console Logs", "text/plain",
                    BrowserUtils.getConsoleLogs());
        } catch (Exception e) {
            Log.error("attachConsoleLogs FAILED: " + e.getMessage());
        }
    }
}
