package com.kavish.sanity;

import com.kavish.core.config.ConfigFactory;
import com.kavish.core.annotations.FrameworkAnnotation;
import com.kavish.core.annotations.Priority;
import com.kavish.core.annotations.TestCategory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigSanityTest {

    @FrameworkAnnotation(category = { TestCategory.SANITY }, author = "Kavish", service = "hcm", story = "Framework bootstrap", priority = Priority.P4)
    @Test(description = "Hello World sanity test: framework config loads without browser or credential bootstrap")
    public void verifyHelloWorldFrameworkBootstrap() {
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
