package com.kavish.core.utils;

import com.kavish.core.exceptions.FrameworkException;

import java.io.InputStream;
import java.util.Properties;

public final class PropertyUtils {

    private PropertyUtils() {}

    public static Properties loadProperties(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new FrameworkException("Properties file name is null/empty");
        }
        Properties properties = new Properties();
        try (InputStream input = PropertyUtils.class
                .getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new FrameworkException(fileName + " not found in classpath");
            }
            properties.load(input);
            return properties;
        } catch (FrameworkException fe) {
            throw fe;
        } catch (Exception e) {
            throw new FrameworkException(
                    "Failed to load properties file: " + fileName + " | " + e.getMessage());
        }
    }
}