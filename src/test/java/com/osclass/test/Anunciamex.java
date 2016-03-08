package com.osclass.test;

import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import com.osclass.utils.Constants;
import com.osclass.utils.SimpleDate;

public class Anunciamex {
    boolean limitFound = false;
    
    public Anunciamex(){}
    
    public static void main(String[] args) throws Exception {
        Anunciamex anunciamex = new Anunciamex();
        anunciamex.navigate();
    }
    
    public void navigate() throws Exception{
        SimpleDate limitDate = new SimpleDate();
        limitDate.advanceDay(-1); //hasta ayer
        System.out.println("Limit Date - " + limitDate.getDay() + " " + limitDate.getMonthString());
                
        Properties prop = Constants.getProperties("config.properties");   
        String copyUrl = prop.getProperty("copy_url");
        String category = "/listing.php?key=&page=0&category=Autos+y+Camionetas&state=Todo+Mexico";
        ///listing.php?key=&page=0&category=Autos+y+Camionetas&state=Todo+Mexico
        ///listing.php?key=&page=0&category=Motocicletas&state=Todo+Mexico
        WebDriver driver = new FirefoxDriver();
        WebDriver driverListing = new FirefoxDriver();
        WebDriver driverOsclass = new FirefoxDriver();
        driver.get(copyUrl + category);
        List<WebElement> listings = driver.findElements(By.xpath("//*[@class='table table-condensed table-hover']/tbody/tr"));

        do{
            for(int i=0; i<listings.size(); i++){
                SimpleDate pointerDate = new SimpleDate();
                String pointerDateString =  driver.findElement(By.xpath("//*[@class='table table-condensed table-hover']/tbody/tr[" + (i+1) + "]/td/h4")).getText().trim();
                if(pointerDateString.equals("Hoy")){
                    
                }else if(pointerDateString.equals("Ayer")){
                    pointerDate.advanceDay(-1);
                }else{
                    String day = pointerDateString.split(" ")[0];
                    String month = pointerDateString.split(" ")[1];
                    pointerDate.setDay(Integer.parseInt(day));
                    pointerDate.setStringMoth(month);
                }
                String listingText =  driver.findElement(By.xpath("//*[@class='table table-condensed table-hover']/tbody/tr[" + (i+1) + "]/td[5]/a/h2")).getText().trim();
                String listingUrl = driver.findElement(By.xpath("//*[@class='table table-condensed table-hover']/tbody/tr[" + (i+1) + "]/td[5]/a")).getAttribute("href");
                String imagePath = driver.findElement(By.xpath("//*[@class='table table-condensed table-hover']/tbody/tr[" + (i+1) + "]/td[2]/a/img")).getAttribute("src");
                
                if(imagePath.contains("noImage")){
                    imagePath = "noImage";
                }
                else{
                    imagePath = imagePath.split("/")[5];
                }
                
                System.out.println(pointerDateString + " - " + listingText + " - " + imagePath + " - " + listingUrl);
                
                if(pointerDate.compareTo(limitDate) == 1){
                    System.out.println("Validate listing");
                    Hashtable<String, String> hashtable = captureListing(driverListing, imagePath, listingUrl);
                    hashtable.put("imagePath", imagePath);
                    hashtable.put("description", listingText);
                    publishNewListing(driverOsclass, hashtable);
                }
                else{
                    limitFound = true;
                    break;
                }
            }
            if(!limitFound)
                driver.findElement(By.linkText("Sig")).click();
        }while(!limitFound);
    }
    
    public Hashtable<String, String> captureListing(WebDriver driver, String imagePath, String listingUrl) throws Exception{
        
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        
        driver.get(listingUrl);
        
        String location = driver.findElement(By.xpath("//*[@class='itemUbi']")).getText();
        hashtable.put("municipio", location.split(",")[0].trim());
        hashtable.put("state", location.split(",")[1].trim());
        
        String price = driver.findElement(By.xpath("//*[@class='itemInfo']/h3")).getText().trim();
        price = price.replaceAll("\\$", "").split(" ")[0];
        hashtable.put("price", price);
        
        String text = driver.findElement(By.xpath("//*[@class='itemText']")).getText();
        hashtable.put("text", text);
        
        driver.findElement(By.linkText("Mostrar Teléfono")).click();
        String phone = driver.findElement(By.xpath(".//*[@class='itemPhone']/a")).getText().trim();
        hashtable.put("phone", phone);
        
        return hashtable;
    }
    
    public void publishNewListing(WebDriver driver, Hashtable<String, String> hashtable){
        Properties prop = Constants.getProperties("config.properties");   
        String baseUrl = prop.getProperty("base_url");
        
        driver.get(baseUrl + "/oc-admin");
        driver.findElement(By.id("user_login")).sendKeys(prop.getProperty("username"));
        driver.findElement(By.id("user_pass")).sendKeys(prop.getProperty("password"));
        driver.findElement(By.id("submit")).click();
        
        driver.get(baseUrl + "/oc-admin/index.php?page=items&action=post");
        new Select(driver.findElement(By.id("select_1"))).selectByValue("2");
        new Select(driver.findElement(By.id("select_2"))).selectByValue("31");
        
        driver.findElement(By.id("contactName")).sendKeys("Edgar");
        driver.findElement(By.id("contactEmail")).sendKeys("edgar.hdz99@gmail.com");
        
        driver.findElement(By.id("title[es_ES]")).sendKeys(hashtable.get("description"));
        driver.findElement(By.id("description[es_ES]")).sendKeys(hashtable.get("text"));
        
    }
    
    public String searchAndReplace(String pool, String needle, String replaceWith) {
        if(pool.contains(needle)) {
            String search = needle.replaceAll("\\$", "\\\\\\$");
            pool = pool.replaceAll(search, replaceWith);
        }
        return pool;
    }

}
