package com.kavish.core.base;

import org.testng.annotations.Listeners;
import com.kavish.core.annotations.FrameworkAnnotation;
import com.kavish.core.config.ConfigFactory;
import com.kavish.core.config.FrameworkConfig;
import com.kavish.core.driver.DriverFactory;
import com.kavish.core.driver.DriverManager;
import com.kavish.core.logging.Log;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

@Listeners({com.kavish.core.listeners.TestListener.class, com.kavish.core.listeners.GroupsListener.class})
public class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        FrameworkConfig cfg = ConfigFactory.getConfig();
        String resolvedService = resolveServiceForMethod(method, cfg);
        String resolvedUrl = cfg.serviceBaseUrl(resolvedService);
        String thread = Thread.currentThread().getName();

        Log.info("========== FRAMEWORK CONFIG [" + thread + "] ==========");
        Log.info("  env         : " + cfg.env());
        Log.info("  service     : " + resolvedService);
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

    private String resolveServiceForMethod(Method method, FrameworkConfig cfg) {
        String service = System.getProperty("service");
        if (service != null && !service.isBlank()) {
            return service.trim();
        }

        service = System.getenv("SERVICE");
        if (service != null && !service.isBlank()) {
            return service.trim();
        }

        FrameworkAnnotation annotation = method.getAnnotation(FrameworkAnnotation.class);
        if (annotation != null && !annotation.service().isBlank()) {
            return annotation.service().trim();
        }

        return cfg.service();
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
