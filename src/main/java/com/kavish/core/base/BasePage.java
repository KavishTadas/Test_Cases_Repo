package com.kavish.core.base;

import com.kavish.core.config.ConfigFactory;
import com.kavish.core.constants.WaitStrategy;
import com.kavish.core.driver.DriverManager;
import com.kavish.core.exceptions.FrameworkException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {

    public WebDriver driver() {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver is null. Did you call DriverFactory.initDriver()?");
        }
        return driver;
    }

    protected WebDriverWait getWait() {
        int seconds = ConfigFactory.getConfig().explicitWait();
        if (seconds <= 0) seconds = 10;
        return new WebDriverWait(driver(), Duration.ofSeconds(seconds));
    }

    // ── Central wait dispatcher ────────────────────────────────────────────────

    protected WebElement waitFor(By locator, WaitStrategy strategy) {
        switch (strategy) {
            case CLICKABLE:
                return getWait().until(ExpectedConditions.elementToBeClickable(locator));
            case VISIBLE:
                return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
            case PRESENCE:
                return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
            case NONE:
                return driver().findElement(locator);
            default:
                throw new FrameworkException("Unsupported WaitStrategy: " + strategy);
        }
    }

    // ── Default strategy overloads (backward-compatible) ─────────────────────

    protected WebElement waitVisible(By locator) {
        return waitFor(locator, WaitStrategy.VISIBLE);
    }

    protected WebElement waitClickable(By locator) {
        return waitFor(locator, WaitStrategy.CLICKABLE);
    }

    // ── Action methods — default strategy ─────────────────────────────────────

    protected void click(By locator) {
        waitFor(locator, WaitStrategy.CLICKABLE).click();
    }

    protected void type(By locator, String value) {
        WebElement el = waitFor(locator, WaitStrategy.VISIBLE);
        el.clear();
        el.sendKeys(value);
    }

    protected String getText(By locator) {
        return waitFor(locator, WaitStrategy.VISIBLE).getText();
    }

    // ── Action methods — explicit strategy overloads ──────────────────────────

    protected void click(By locator, WaitStrategy strategy) {
        waitFor(locator, strategy).click();
    }

    protected void type(By locator, WaitStrategy strategy, String value) {
        WebElement el = waitFor(locator, strategy);
        el.clear();
        el.sendKeys(value);
    }

    protected String getText(By locator, WaitStrategy strategy) {
        return waitFor(locator, strategy).getText();
    }

    // ── URL helpers ───────────────────────────────────────────────────────────

    public void waitForUrlContains(String partial) {
        getWait().until(ExpectedConditions.urlContains(partial));
    }

    public void waitForUrlToBe(String exactUrl) {
        getWait().until(ExpectedConditions.urlToBe(exactUrl));
    }
}