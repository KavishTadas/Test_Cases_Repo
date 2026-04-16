package com.kavish.core.utils;

import com.kavish.core.constants.FrameworkConstants;
import com.kavish.core.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {

    private ScreenshotUtils() {}

    public static String capture(String testName) {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver == null) return "Driver was null — screenshot not captured";

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            Files.createDirectories(Path.of(FrameworkConstants.SCREENSHOTS_DIR));
            Path destination = Path.of(
                    FrameworkConstants.SCREENSHOTS_DIR, testName + "_" + timestamp + ".png");
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            return destination.toAbsolutePath().toString();
        } catch (Exception e) {
            return "Failed to capture screenshot: " + e.getMessage();
        }
    }

    public static byte[] captureBytes() {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver == null) return new byte[0];
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            return new byte[0];
        }
    }
}