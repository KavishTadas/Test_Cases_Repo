package com.kavish.core.driver;

import org.openqa.selenium.WebDriver;

public final class DriverManager {

    private DriverManager() {}

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static WebDriver getRequiredDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver is not initialised for the current thread. Call DriverFactory.initDriver() first.");
        }
        return driver;
    }

    /** Stores a WebDriver on the current thread's slot. */
    public static void setDriver(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("DriverManager cannot store a null WebDriver.");
        }
        DRIVER.set(driver);
    }

    public static void quitDriver() {
        unload();
    }

    public static void unload() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DRIVER.remove();
        }
    }
}
