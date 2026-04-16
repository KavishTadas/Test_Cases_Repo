package com.kavish.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Single source of truth for all test metadata.
 * Apply to every @Test method.
 *
 * TestListener reads this at runtime and:
 *  1. Logs all fields to framework.log via Log4j2
 *  2. Drives all Allure report labels (Feature, Story, Severity, Author, Tags)
 *  3. Attaches a "Test Metadata" plain-text block to the Allure test detail
 *
 * No other Allure annotations (@Feature, @Story, @Severity, @Owner) are needed
 * on test methods — this annotation replaces all of them.
 *
 * Example:
 * <pre>
 *   @FrameworkAnnotation(
 *       category = { TestCategory.SMOKE, TestCategory.REGRESSION },
 *       author   = "Kavish",
 *       service  = "platform",
 *       story    = "Successful Login",
 *       priority = Priority.P1
 *   )
 *   @Test(description = "Valid credentials navigate to HCM home page")
 *   public void verifySuccessfulLogin() { ... }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FrameworkAnnotation {

    /**
     * One or more test categories.
     * Drives Allure "tag" labels — visible as filter chips in the report.
     */
    TestCategory[] category();

    /**
     * Name of the engineer who owns this test.
     * Drives Allure "owner" label — visible in test detail and Authors widget.
     */
    String author();

    /**
     * The service/module under test (e.g. "platform", "hcm", "crm").
     * Drives Allure "feature" label — groups tests in the Behaviors tab.
     * Should match the service name used in FrameworkConfig.serviceBaseUrl().
     */
    String service();

    /**
     * The specific flow or scenario being tested.
     * Drives Allure "story" label — sub-groups under Feature in Behaviors tab.
     * e.g. "Successful Login", "Invalid Credentials", "Data-Driven Valid Login"
     */
    String story();

    /**
     * Priority/severity of the test.
     * Drives Allure severity chip — P1=CRITICAL, P2=HIGH, P3=NORMAL, P4=LOW.
     * Defaults to P3 if not specified.
     */
    Priority priority() default Priority.P3;
}