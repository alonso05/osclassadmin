package com.osclass.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    	downloadTestFile = new FileDownloader(driver);
    }
	
	
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
	 
	@Test
	public void downloadImages() throws Exception {
	    String itemCode = "nsJoMXGjsj";
	    String path = System.getProperty("user.dir")+ "\\src\\test\\resources\\temp\\";
	    downloadTestFile.setLocalDownloadPath(path);
	    List<String> images = new ArrayList<String>();

	    int i = 1;	    
	    while(i<=4){
	        
	        String image = "file" + i + ".png";
	        String urlPath = "http://www.anunciamex.mx/items/images/" + itemCode + "/" + image;
	        
	        urlChecker.setURIToCheck(urlPath);
	        urlChecker.setHTTPRequestMethod(RequestMethod.GET);
	        
	        if(urlChecker.getHTTPStatusCode() != 404){
	            String downloadedImageAbsoluteLocation = downloadTestFile.downloadImage(urlPath);
	            if(new File(downloadedImageAbsoluteLocation).exists()){
	                images.add(image);
	                i++;
	            }
	            else{
	                break;
	            }
	                
	        }
	        else{
	            break;
	        }
	      	        
	    }
	   
	}
	
}
