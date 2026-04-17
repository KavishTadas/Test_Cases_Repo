package com.kavish.sanity;

import com.kavish.core.annotations.FrameworkAnnotation;
import com.kavish.core.annotations.Priority;
import com.kavish.core.annotations.TestCategory;
import io.qameta.allure.Allure;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HelloWorldTest {

 @FrameworkAnnotation()
 category = { TestCategory.SANITY },
 author = "local-bootstrap",
 service = "platform",
 story = "Hello World local verification",
 priority = Priority.P4
 )
 @Test(description = "Verifies the local TestNG plus Allure pipeline without browser or credentials")
 public void verifyHelloWorld() {
 String message = "Hello, Allure";
 Allure.addAttachment("hello-world.txt", "text/plain", message);
 Assert.assertEquals(message, "Hello, Allure");
 }
}
