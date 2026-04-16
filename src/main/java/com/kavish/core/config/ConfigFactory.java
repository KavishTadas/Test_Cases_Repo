package com.kavish.core.config;

public final class ConfigFactory {

    private static final FrameworkConfig CONFIG = new FrameworkConfig();

    private ConfigFactory() {}

    public static FrameworkConfig getConfig() {
        return CONFIG;
    }
}