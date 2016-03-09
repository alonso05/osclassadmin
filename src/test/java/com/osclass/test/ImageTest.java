package com.osclass.test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.osclass.utils.URLStatusChecker;
import com.osclass.utils.URLStatusChecker.RequestMethod;

public class ImageTest {
	URLStatusChecker urlChecker;
	
	@BeforeTest
    public void setup() throws Exception {
    	WebDriver driver = new FirefoxDriver();
    	urlChecker = new URLStatusChecker(driver);
    }
	
	@Test
	public void statusCode404FromString() throws Exception {
	    urlChecker.setURIToCheck("http://www.morrisvende.com/doesNotExist.html");
	    urlChecker.setHTTPRequestMethod(RequestMethod.GET);
	    Assert.assertEquals(urlChecker.getHTTPStatusCode(), 404);
	}
	
}
