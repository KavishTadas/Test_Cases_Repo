package com.kavish.core.listeners;

import com.kavish.core.annotations.FrameworkAnnotation;
import com.kavish.core.logging.Log;
import com.kavish.core.utils.AllureAttachmentUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.model.Label;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        Log.info(tag(result) + "STARTED : " + result.getMethod().getMethodName());
        applyAnnotationMetadata(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Log.info(tag(result) + "PASSED  : " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Log.error(tag(result) + "FAILED  : " + testName);
        AllureAttachmentUtils.attachScreenshot(testName);
        AllureAttachmentUtils.attachFailureReason(result.getThrowable());
        AllureAttachmentUtils.attachCurrentUrl();
        AllureAttachmentUtils.attachEnvInfo();
        AllureAttachmentUtils.attachPageSource();
        AllureAttachmentUtils.attachConsoleLogs();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Log.info(tag(result) + "SKIPPED : " + result.getMethod().getMethodName());
    }

    @Override
    public void onStart(ITestContext context) {
        Log.info("===== TEST SUITE STARTED  : " + context.getName() + " =====");
    }

    @Override
    public void onFinish(ITestContext context) {
        Log.info("===== TEST SUITE FINISHED : " + context.getName() + " =====");
    }

    // ── private ───────────────────────────────────────────────────────────────

    /** Returns "[ThreadName] " prefix for log lines. */
    private static String tag(ITestResult result) {
        return "[" + Thread.currentThread().getName() + "] ";
    }

    private void applyAnnotationMetadata(ITestResult result) {
        try {
            Method method =
                    result.getMethod().getConstructorOrMethod().getMethod();
            FrameworkAnnotation annotation =
                    method.getAnnotation(FrameworkAnnotation.class);

            if (annotation == null) {
                Log.warn("@FrameworkAnnotation missing on: "
                        + result.getMethod().getMethodName()
                        + " --- add it for categorisation and Allure metadata.");
                return;
            }

            // ── 1. Structured logging ──────────────────────────────────────────
            String categories = Arrays.stream(annotation.category())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));

            String t = tag(result);
            Log.info(t + "  author   : " + annotation.author());
            Log.info(t + "  service  : " + annotation.service());
            Log.info(t + "  story    : " + annotation.story());
            Log.info(t + "  category : " + categories);
            Log.info(t + "  priority : " + annotation.priority().name());

            // ── 2. Allure labels via lifecycle API ────────────────────────────
            // Allure's lifecycle is ThreadLocal-backed — safe for parallel runs.
            Allure.getLifecycle().updateTestCase(testResult -> {

                testResult.getLabels().add(
                        new Label().setName("feature").setValue(annotation.service()));

                testResult.getLabels().add(
                        new Label().setName("story").setValue(annotation.story()));

                testResult.getLabels().add(
                        new Label().setName("owner").setValue(annotation.author()));

                Arrays.stream(annotation.category()).forEach(cat ->
                        testResult.getLabels().add(
                                new Label().setName("tag").setValue(cat.name())));

                testResult.getLabels().add(
                        new Label().setName("severity").setValue(
                                mapPriorityToSeverity(annotation.priority().name())));
            });

            // ── 3. Plain-text metadata attachment ────────────────────────────
            String metadata =
                    "author   : " + annotation.author()   + "\n" +
                            "service  : " + annotation.service()  + "\n" +
                            "story    : " + annotation.story()    + "\n" +
                            "category : " + categories            + "\n" +
                            "priority : " + annotation.priority().name();

            Allure.addAttachment("Test Metadata", "text/plain", metadata);

        } catch (Exception e) {
            Log.error("applyAnnotationMetadata FAILED: " + e.getMessage());
        }
    }

    private String mapPriorityToSeverity(String priority) {
        switch (priority) {
            case "P1": return SeverityLevel.CRITICAL.value();
            case "P2": return SeverityLevel.NORMAL.value();
            case "P3": return SeverityLevel.MINOR.value();
            case "P4": return SeverityLevel.TRIVIAL.value();
            default:   return SeverityLevel.NORMAL.value();
        }
    }
}