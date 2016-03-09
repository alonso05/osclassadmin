package com.osclass.test;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.osclass.utils.FileDownloader;
import com.osclass.utils.URLStatusChecker;
import com.osclass.utils.URLStatusChecker.RequestMethod;

public class ImageTest {
	URLStatusChecker urlChecker;
	FileDownloader downloadTestFile;
	WebDriver driver = new FirefoxDriver();
	
	@BeforeTest
    public void setup() throws Exception {
    	urlChecker = new URLStatusChecker(driver);
    	//downloadTestFile = new FileDownloader(driver);
    }
	
	@Test
	public void statusCode404FromString() throws Exception {
	    urlChecker.setURIToCheck("http://anunciamex.mx/items/images/ffUnbBSsuh/file5.png");
	    urlChecker.setHTTPRequestMethod(RequestMethod.GET);
	    Assert.assertEquals(urlChecker.getHTTPStatusCode(), 404);
	}
	
	
	public void downloadAFile() throws Exception {
	    FileDownloader downloadTestFile = new FileDownloader(driver);
	    driver.get("http://www.localhost.com/downloadTest.html");
	    WebElement downloadLink = driver.findElement(By.id("fileToDownload"));
	    String downloadedFileAbsoluteLocation = downloadTestFile.downloadFile(downloadLink);
	    
	    Assert.assertEquals(new File(downloadedFileAbsoluteLocation).exists(), true);
	    Assert.assertEquals(downloadTestFile.getHTTPStatusOfLastDownloadAttempt(), 200);
	}
	 
	
	public void downloadAnImage() throws Exception {
	    String urlPath = "http://anunciamex.mx/items/images/ffUnbBSsuh/file1.png";
	    String downloadedImageAbsoluteLocation = downloadTestFile.downloadImage(urlPath);
	    
	    Assert.assertEquals(new File(downloadedImageAbsoluteLocation).exists(), true);
	    Assert.assertEquals(downloadTestFile.getHTTPStatusOfLastDownloadAttempt(), 200);

	}
	
}
