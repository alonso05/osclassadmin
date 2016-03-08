package com.osclass.test;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.osclass.utils.Constants;
import com.osclass.utils.Xls_Reader;

public class UpdateLocations {
    
    public static void main(String[] args) throws Exception {
        
        Properties prop = Constants.getProperties("config.properties");   
        String baseUrl = prop.getProperty("base_url");
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
        
    	String state = "Chiapas";
    	
    	System.out.println(System.getProperty("user.dir")+ "\\src\\test\\resources\\Municipios.xlsx");
   	 	Xls_Reader excel = new Xls_Reader(System.getProperty("user.dir")+ "\\src\\test\\resources\\Municipios.xlsx");

        List<String> validMunicipalities = new ArrayList<String>();
        for(int i = 1; i<=excel.getRowCount(state); i++){
        	validMunicipalities.add(excel.getCellData(state, 0, i).trim().toLowerCase().replace("'", ""));	
        }
        System.out.println("Total Valid Municipalities in " + state + ": " + validMunicipalities.size());

        WebDriver driver = new FirefoxDriver();
        driver.get(baseUrl + "/oc-admin");
        driver.findElement(By.id("user_login")).sendKeys(username);
        driver.findElement(By.id("user_pass")).sendKeys(password);
        driver.findElement(By.id("submit")).click();
        
        driver.navigate().to(baseUrl + "/oc-admin/index.php?page=settings&action=locations");
        driver.findElement(By.className("view-more")).click();
        Thread.sleep(2000);
        clickState(driver, state);
        Thread.sleep(2000);

        int i = 1;
        int deleted = 0;
        
        while(driver.findElements(By.xpath(".//*[@id='i_cities']/div["+ i +"]/div/div/span/input")).size() > 0 && deleted <411){
            String seed = driver.findElement(By.xpath(".//*[@id='i_cities']/div["+ i +"]/div/div/a[2]")).getText().toLowerCase().trim();
            System.out.println("Validating : " + seed + " Deleted till now: " + deleted);
            
            if(!validMunicipalities.contains(seed)){
            	WebElement we = driver.findElement(By.xpath(".//*[@id='i_cities']/div["+ i +"]/div/div/span/input"));
                String js = "arguments[0].style.visibility='visible';";
                ((JavascriptExecutor) driver).executeScript(js, we);
                we.click();
                deleted++;
            }
            i = i + 2;

        }



    }
    
    private static void clickState(WebDriver driver, String state){
        driver.findElement(By.xpath(".//*[substring(text(), string-length(text()) - string-length('"+ state +"') +1) = '"+ state +"']/ancestor::div[@style='float: left;']/following-sibling::div/a")).click();
    }
    

}
