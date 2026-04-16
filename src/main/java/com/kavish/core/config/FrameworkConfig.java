package com.kavish.core.config;

import com.kavish.core.utils.PropertyUtils;

import java.util.Properties;

public class FrameworkConfig {

    private final Properties properties;
    private final String activeEnv;
    private final String activeService;

    public FrameworkConfig() {
        String env = getNonBlankSysProp("env");
        if (env == null) env = getNonBlankEnvVar("ENV");
        if (env == null) env = "local";
        this.activeEnv = env;

        String svc = getNonBlankSysProp("service");
        if (svc == null) svc = getNonBlankEnvVar("SERVICE");
        if (svc == null) svc = "hcm";
        this.activeService = svc.toLowerCase();

        this.properties = PropertyUtils.loadProperties(
                "config/" + this.activeEnv + ".properties");
    }

    private String getNonBlankSysProp(String key) {
        String v = System.getProperty(key);
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private String getNonBlankEnvVar(String key) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private String resolve(String key) {
        String sys = getNonBlankSysProp(key);
        if (sys != null) return sys;
        String envVar = getNonBlankEnvVar(key.toUpperCase().replace(".", "_"));
        if (envVar != null) return envVar;
        String file = properties.getProperty(key);
        return (file == null) ? null : file.trim();
    }

    public String env()     { return activeEnv; }
    public String service() { return activeService; }

    public String browser() { return resolve("browser"); }

    public boolean headless() {
        String v = resolve("headless");
        return v != null && Boolean.parseBoolean(v);
    }

    public int explicitWait() {
        String v = resolve("explicitWait");
        if (v == null || v.isBlank()) return 10;
        return Integer.parseInt(v.trim());
    }

    public String gridUrl() {
        String v = resolve("grid.url");
        return (v != null) ? v : "http://localhost:4444";
    }

    public String baseUrl() {
        return serviceBaseUrl(activeService);
    }

    public String serviceBaseUrl(String service) {
        String key = service.toLowerCase() + ".baseUrl";
        String v = resolve(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException(
                    "No baseUrl configured for service='" + service +
                            "' in env='" + activeEnv + "'. " +
                            "Expected key: '" + key + "' in config/" + activeEnv + ".properties");
        }
        return v;
    }

    public String appUsername() {
        return requiredSecret("app.username", "APP_USERNAME");
    }

    public String appPassword() {
        return requiredSecret("app.password", "APP_PASSWORD");
    }

    private String requiredSecret(String sysPropKey, String envVarKey) {
        String value = getNonBlankSysProp(sysPropKey);
        if (value != null) return value;

        value = getNonBlankEnvVar(envVarKey);
        if (value != null) return value;

        throw new IllegalStateException(
                "Missing credential. Provide either -D" + sysPropKey
                        + " or OS environment variable " + envVarKey + ".");
    }
}
