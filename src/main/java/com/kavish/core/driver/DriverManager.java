package com.kavish.core.driver;

import org.openqa.selenium.WebDriver;

public final class DriverManager {

    private DriverManager() {}

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    /** Stores a WebDriver on the current thread's slot. */
    public static void setDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static void unload() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}