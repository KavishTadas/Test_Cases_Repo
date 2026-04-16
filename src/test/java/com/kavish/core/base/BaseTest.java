package com.kavish.core.base;

import com.kavish.core.config.ConfigFactory;
import com.kavish.core.config.FrameworkConfig;
import com.kavish.core.driver.DriverFactory;
import com.kavish.core.driver.DriverManager;
import com.kavish.core.logging.Log;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        FrameworkConfig cfg = ConfigFactory.getConfig();
        String resolvedUrl = cfg.baseUrl();
        String thread = Thread.currentThread().getName();

        Log.info("========== FRAMEWORK CONFIG [" + thread + "] ==========");
        Log.info("  env         : " + cfg.env());
        Log.info("  service     : " + cfg.service());
        Log.info("  baseUrl     : " + resolvedUrl);
        Log.info("  gridUrl     : " + cfg.gridUrl());
        Log.info("  browser     : " + cfg.browser());
        Log.info("  headless    : " + cfg.headless());
        Log.info("  explicitWait: " + cfg.explicitWait() + "s");
        Log.info("======================================");

        // initDriver() stores the WebDriver in a ThreadLocal inside DriverManager.
        // Each parallel thread gets its own isolated WebDriver session.
        DriverFactory.initDriver();
        DriverManager.getDriver().get(resolvedUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String thread = Thread.currentThread().getName();
        String testName = result.getMethod().getMethodName();
        Log.info("tearDown [" + thread + "] : " + testName
                + " — status=" + statusLabel(result.getStatus()));
        DriverManager.unload();
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private static String statusLabel(int status) {
        switch (status) {
            case ITestResult.SUCCESS: return "PASSED";
            case ITestResult.FAILURE: return "FAILED";
            case ITestResult.SKIP:    return "SKIPPED";
            default:                  return "UNKNOWN(" + status + ")";
        }
    }
}