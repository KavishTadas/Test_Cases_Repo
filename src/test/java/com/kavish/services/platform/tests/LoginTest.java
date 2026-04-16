package com.kavish.services.platform.tests;

import com.kavish.core.annotations.FrameworkAnnotation;
import com.kavish.core.annotations.Priority;
import com.kavish.core.annotations.TestCategory;
import com.kavish.core.base.BaseTest;
import com.kavish.core.dataproviders.ExcelDataProvider;
import com.kavish.core.driver.DriverManager;
import com.kavish.core.listeners.TestListener;
import com.kavish.core.utils.DataProviderUtils;
import com.kavish.services.platform.flows.LoginFlow;
import com.kavish.services.platform.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Map;

@Listeners(TestListener.class)
public class LoginTest extends BaseTest {

    // ── Data Providers ────────────────────────────────────────────────────────

    @DataProvider(name = "validLoginData", parallel = true)
    public Object[][] validLoginData() {
        return DataProviderUtils.toObjectArray(
                ExcelDataProvider.getData("testdata/login.xlsx", "ValidCredentials")
        );
    }

    @DataProvider(name = "invalidLoginData", parallel = true)
    public Object[][] invalidLoginData() {
        return DataProviderUtils.toObjectArray(
                ExcelDataProvider.getData("testdata/login.xlsx", "InvalidCredentials")
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @FrameworkAnnotation(
            category = { TestCategory.SMOKE, TestCategory.REGRESSION },
            author   = "Kavish",
            service  = "platform",
            story    = "Successful Login",
            priority = Priority.P1
    )
    @Test(description = "Valid credentials navigate to HCM home page")
    public void verifySuccessfulLogin() {
        HomePage home = new LoginFlow().login();

        Assert.assertTrue(home.isLoaded(),
                "Home page header not visible after login");
        Assert.assertTrue(
                DriverManager.getDriver().getCurrentUrl().contains("/home"),
                "URL did not reach /home after login");
    }

    @FrameworkAnnotation(
            category = { TestCategory.REGRESSION },
            author   = "Kavish",
            service  = "platform",
            story    = "Invalid Credentials",
            priority = Priority.P2
    )
    @Test(
            description  = "Login with invalid credentials shows error message",
            dataProvider = "invalidLoginData"
    )
    public void verifyLoginWithInvalidCredentials(Map<String, String> data) {
        String errorMsg = new LoginFlow(
                data.get("username"),
                data.get("password")
        ).loginAndGetError();

        Assert.assertNotNull(errorMsg, "No error message was displayed");
        Assert.assertFalse(errorMsg.isBlank(), "Error message was blank");
    }

    @FrameworkAnnotation(
            category = { TestCategory.SANITY },
            author   = "Kavish",
            service  = "platform",
            story    = "Data-Driven Valid Login",
            priority = Priority.P1
    )
    @Test(
            description  = "Data-driven login test with multiple valid credential sets",
            dataProvider = "validLoginData"
    )
    public void verifyDataDrivenLogin(Map<String, String> data) {
        HomePage home = new LoginFlow(
                data.get("username"),
                data.get("password")
        ).login();

        Assert.assertTrue(home.isLoaded(),
                "Home page not loaded for user: " + data.get("username"));
    }
}