package com.kavish.services.platform.flows;

import com.kavish.core.config.ConfigFactory;
import com.kavish.core.logging.Log;
import com.kavish.services.platform.pages.HomePage;
import com.kavish.services.platform.pages.LoginPage;

public class LoginFlow {

    private final String username;
    private final String password;

    /**
     * Credentials injected at construction time.
     * Source: FrameworkConfig credential resolution (never hardcoded).
     */
    public LoginFlow() {
        this.username = ConfigFactory.getConfig().appUsername();
        this.password = ConfigFactory.getConfig().appPassword();
    }

    /** Overload for when a test needs to supply specific credentials explicitly. */
    public LoginFlow(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Performs the full login sequence and returns a ready HomePage.
     * Used by every test that needs an authenticated session.
     */
    public HomePage login() {
        Log.info("LoginFlow — starting login for user: " + username);
        HomePage home = new LoginPage()
                .enterUsername(username)
                .enterPassword(password)
                .clickLogin();
        Log.info("LoginFlow — login complete");
        return home;
    }

    /**
     * Attempts login with the given credentials expecting failure.
     * Returns the error message text shown on the login page.
     * Use this for negative credential tests instead of login().
     */
    public String loginAndGetError() {
        Log.info("LoginFlow — attempting login expecting failure for user: " + username);
        return new LoginPage()
                .enterUsername(username)
                .enterPassword(password)
                .clickLoginExpectingFailure()
                .getErrorMessage();
    }

    /**
     * Logs in and immediately navigates to a specific post-login path.
     * Useful when a test starts at a deep URL rather than the home page.
     */
    public HomePage loginAndNavigateTo(String relativePath) {
        HomePage home = login();
        String base = ConfigFactory.getConfig().baseUrl()
                .replaceAll("/login$", ""); // strip /login suffix
        home.driver().navigate().to(base + relativePath);
        Log.info("LoginFlow — navigated to: " + relativePath);
        return home;
    }
}
