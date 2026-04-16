package com.kavish.services.platform.pages;

import com.kavish.core.base.BasePage;
import com.kavish.core.constants.WaitStrategy;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    private final By username = By.id("loginId");
    private final By password = By.id("password");
    private final By loginBtn = By.cssSelector(
            ".w-full.py-3.px-4.border.border-transparent.text-sm.font-medium.rounded-lg.text-white");


    private final By errorMessage = By.cssSelector("p.text-sm.text-red-600.font-medium");

    public LoginPage enterUsername(String user) {
        type(username, WaitStrategy.VISIBLE, user);
        return this;
    }

    public LoginPage enterPassword(String pass) {
        type(password, WaitStrategy.VISIBLE, pass);
        return this;
    }

    /**
     * Clicks login and waits for successful redirect to /home.
     * Use this on the happy path only.
     */
    public HomePage clickLogin() {
        click(loginBtn, WaitStrategy.CLICKABLE);
        waitForUrlContains("/home");
        return new HomePage();
    }

    /**
     * Clicks login but stays on LoginPage — used when we expect authentication to fail.
     * Does NOT wait for /home URL, so the error message can be read immediately.
     */
    public LoginPage clickLoginExpectingFailure() {
        click(loginBtn, WaitStrategy.CLICKABLE);
        return this;
    }

    /**
     * Returns the error message text shown after a failed login attempt.
     * Waits for the element to be visible before reading.
     */
    public String getErrorMessage() {
        return getText(errorMessage, WaitStrategy.VISIBLE);
    }
}