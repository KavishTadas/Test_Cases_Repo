package com.kavish.sanity;

import com.kavish.core.config.ConfigFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigSanityTest {

    @Test
    public void verifyConfigLoads() {
        System.out.println("env="         + ConfigFactory.getConfig().env());
        System.out.println("service="     + ConfigFactory.getConfig().service());
        System.out.println("baseUrl="     + ConfigFactory.getConfig().baseUrl());
        System.out.println("gridUrl="     + ConfigFactory.getConfig().gridUrl());
        System.out.println("browser="     + ConfigFactory.getConfig().browser());
        System.out.println("headless="    + ConfigFactory.getConfig().headless());
        System.out.println("explicitWait="+ ConfigFactory.getConfig().explicitWait());

        Assert.assertNotNull(ConfigFactory.getConfig().baseUrl());
        Assert.assertFalse(ConfigFactory.getConfig().baseUrl().isBlank());
    }
}