package com.kavish.services.platform.pages;

import com.kavish.core.base.BasePage;
import com.kavish.core.constants.WaitStrategy;
import org.openqa.selenium.By;

public class HomePage extends BasePage {


    private final By header = By.cssSelector("header.fixed.top-0");

    public boolean isLoaded() {
        try {
            waitFor(header, WaitStrategy.VISIBLE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}