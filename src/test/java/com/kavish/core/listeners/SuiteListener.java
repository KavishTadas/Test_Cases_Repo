package com.kavish.core.listeners;

import com.kavish.core.config.ConfigFactory;
import com.kavish.core.config.FrameworkConfig;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        FrameworkConfig cfg = ConfigFactory.getConfig();
        Path resultsDir = Path.of("target", "allure-results");

        try {
            Files.createDirectories(resultsDir);

            Properties environment = new Properties();
            environment.setProperty("env", String.valueOf(cfg.env()));
            environment.setProperty("service", String.valueOf(cfg.service()));
            environment.setProperty("browser", String.valueOf(cfg.browser()));
            environment.setProperty("headless", String.valueOf(cfg.headless()));
            environment.setProperty("grid.url", String.valueOf(cfg.gridUrl()));
            environment.setProperty("java.version", System.getProperty("java.version"));
            environment.setProperty("os.name", System.getProperty("os.name"));
            environment.setProperty("os.version", System.getProperty("os.version"));

            try (OutputStream outputStream = Files.newOutputStream(resultsDir.resolve("environment.properties"))) {
                environment.store(outputStream, "Allure environment");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write Allure environment properties", e);
        }
    }

    @Override
    public void onFinish(ISuite suite) {
    }
}
