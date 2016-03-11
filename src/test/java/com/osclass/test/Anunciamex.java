package com.osclass.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import com.osclass.utils.Constants;
import com.osclass.utils.FileDownloader;
import com.osclass.utils.SimpleDate;
import com.osclass.utils.URLStatusChecker;
import com.osclass.utils.URLStatusChecker.RequestMethod;

public class Anunciamex {
    boolean limitFound = false;
    URLStatusChecker urlChecker;
    FileDownloader downloadTestFile;
    Properties prop = Constants.getProperties("config.properties"); 
    String baseUrl;
    String categorySelector;
    
    public Anunciamex(){}
    
    public static void main(String[] args) throws Exception {
        Anunciamex anunciamex = new Anunciamex();
        anunciamex.navigate();
    }
    
    public void navigate() throws Exception{
        SimpleDate limitDate = new SimpleDate();
        limitDate.advanceDay(-1); //hasta ayer
        //limitDate.advanceDay(-35); //hasta ayer
        System.out.println("Limit Date - " + limitDate.getDay() + " " + limitDate.getMonthString());
        
        baseUrl = prop.getProperty("base_url");
        String copyUrl = prop.getProperty("copy_url");
        String category = "/listing.php?key=&page=0&category=Autos+y+Camionetas&state=Todo+Mexico";
        ///listing.php?key=&page=0&category=Autos+y+Camionetas&state=Todo+Mexico
        ///listing.php?key=&page=0&category=Motocicletas&state=Todo+Mexico
        //listing.php?key=&page=0&category=Casas+y+Departamentos&state=Todo+Mexico
        //listing.php?key=&page=0&category=Telefonos&state=Todo+Mexico
        categorySelector = category.substring((category.indexOf("category=")+9), category.indexOf("&state"));
        
        WebDriver driverNav = new FirefoxDriver();
        WebDriver driverListing = new FirefoxDriver();
        WebDriver driverOsclass = new FirefoxDriver();
        
        urlChecker = new URLStatusChecker(driverListing);
        downloadTestFile = new FileDownloader(driverListing);
        
        driverOsclass.get(baseUrl + "/oc-admin");
        driverOsclass.findElement(By.id("user_login")).sendKeys(prop.getProperty("username"));
        driverOsclass.findElement(By.id("user_pass")).sendKeys(prop.getProperty("password"));
        driverOsclass.findElement(By.id("submit")).click();        
        driverOsclass.get(baseUrl + "/oc-admin/index.php?page=items&action=post");
        
        driverNav.get(copyUrl + category);
        List<WebElement> listings = driverNav.findElements(By.xpath("//*[@class='table table-condensed table-hover']/tbody/tr"));

        do{
            for(int i=0; i<listings.size(); i++){
                SimpleDate pointerDate = new SimpleDate();
                String pointerDateString =  driverNav.findElement(By.xpath("//*[@class='table table-condensed table-hover']/tbody/tr[" + (i+1) + "]/td/h4")).getText().trim();
                if(pointerDateString.equals("Hoy")){
                    
                }else if(pointerDateString.equals("Ayer")){
                    pointerDate.advanceDay(-1);
                }else{
                    String day = pointerDateString.split(" ")[0];
                    String month = pointerDateString.split(" ")[1];
                    pointerDate.setDay(Integer.parseInt(day));
                    pointerDate.setStringMoth(month);
                }
                String listingText =  driverNav.findElement(By.xpath("//*[@class='table table-condensed table-hover']/tbody/tr[" + (i+1) + "]/td[5]/a/h2")).getText().trim();
                String listingUrl = driverNav.findElement(By.xpath("//*[@class='table table-condensed table-hover']/tbody/tr[" + (i+1) + "]/td[5]/a")).getAttribute("href");
                String imagePath = driverNav.findElement(By.xpath("//*[@class='table table-condensed table-hover']/tbody/tr[" + (i+1) + "]/td[2]/a/img")).getAttribute("src");
                
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
                    
                    List<String> listImages = new ArrayList<String>();
                    
                    if(!imagePath.contains("noImage")){
                        String urlImage = copyUrl + "/items/images/" + imagePath;
                        listImages = downloadImages(urlImage);
                    }
                    
                    publishNewListing(driverOsclass, hashtable, listImages);
                }
                else{
                    limitFound = true;
                    break;
                }
            }
            if(!limitFound)
                driverNav.findElement(By.linkText("Sig")).click();
        }while(!limitFound);
    }
    
    public Hashtable<String, String> captureListing(WebDriver driver, String imagePath, String listingUrl) throws Exception{
        
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        
        driver.get(listingUrl);
        
        String location = driver.findElement(By.xpath("//*[@class='itemUbi']")).getText();
        String municipio = location.split(",")[0].trim();
        if(municipio.contains("Tlajomulco"))
            municipio = "Tlajomulco de Zúñiga";
        hashtable.put("municipio", municipio);
        hashtable.put("state", location.split(",")[1].trim());
        
        String price = driver.findElement(By.xpath("//*[@class='itemInfo']/h3")).getText().trim();
        price = price.replaceAll("\\$", "").split(" ")[0];
        hashtable.put("price", price);
        
        String text = driver.findElement(By.xpath("//*[@class='itemText']")).getText();
        hashtable.put("text", text + " SOLO LLAMADAS");
        
        driver.findElement(By.linkText("Mostrar Teléfono")).click();
        String phone = driver.findElement(By.xpath(".//*[@class='itemPhone']/a")).getText().trim();
        hashtable.put("phone", phone);
        
        return hashtable;
    }
    
    private void selectCategory(WebDriver driver, String title) throws Exception{
        switch(categorySelector){
        case "Autos+y+Camionetas" :
            new Select(driver.findElement(By.id("select_1"))).selectByValue("2");
            new Select(driver.findElement(By.id("select_2"))).selectByValue("31");
            break;
        case "Motocicletas" :
            new Select(driver.findElement(By.id("select_1"))).selectByValue("2");
            new Select(driver.findElement(By.id("select_2"))).selectByValue("33");
            break;
        case "Casas+y+Departamentos" :
            new Select(driver.findElement(By.id("select_1"))).selectByValue("4");
            if(title.toLowerCase().contains("rent")){
                if(title.toLowerCase().contains("depa"))
                    new Select(driver.findElement(By.id("select_2"))).selectByValue("100");
                else
                    new Select(driver.findElement(By.id("select_2"))).selectByValue("44");
            }
            else{
                if(title.toLowerCase().contains("depa"))
                    new Select(driver.findElement(By.id("select_2"))).selectByValue("99");
                else
                    new Select(driver.findElement(By.id("select_2"))).selectByValue("43");
            }
            break;
        case "Telefonos" :
            new Select(driver.findElement(By.id("select_1"))).selectByValue("1");
            new Select(driver.findElement(By.id("select_2"))).selectByValue("15");
            break;
        default :
            throw new Exception("Category Selector not found");
        }
    }
    
    public void publishNewListing(WebDriver driver, Hashtable<String, String> hashtable, List<String> listImages) throws Exception{
        
       
        selectCategory(driver, hashtable.get("description"));
        
        driver.findElement(By.id("contactName")).sendKeys("Edgar");
        driver.findElement(By.id("contactEmail")).sendKeys("edgar.hdz99@gmail.com");
        
        driver.findElement(By.id("title[es_ES]")).sendKeys(hashtable.get("description"));
        driver.findElement(By.id("description[es_ES]")).sendKeys(hashtable.get("text"));
        
        driver.findElement(By.id("price")).sendKeys(hashtable.get("price"));
        
        
        driver.findElement(By.id("region")).sendKeys(hashtable.get("state"));
        driver.findElement(By.id("city")).sendKeys(hashtable.get("municipio"));
        
        uploadImages(driver, listImages);
        
        driver.findElement(By.id("meta_new-custom-field")).sendKeys(hashtable.get("phone")); //auto submit
        Thread.sleep(1500);
        if(driver.getCurrentUrl().contains("post")){
            driver.findElement(By.xpath("//*[@class='form-actions']/input")).click();
        }

        driver.get(baseUrl + "/oc-admin/index.php?page=items&action=post");
    }
    
    public void uploadImages(WebDriver driver, List<String> listImages) throws Exception{
        for(int i=0; i<listImages.size(); i++){
            driver.findElement(By.xpath(".//*[@id='photos']/div[" + (i+1) + "]/input")).sendKeys(listImages.get(i));;
            Thread.sleep(300);
        }
        
    }
    
    public String searchAndReplace(String pool, String needle, String replaceWith) {
        if(pool.contains(needle)) {
            String search = needle.replaceAll("\\$", "\\\\\\$");
            pool = pool.replaceAll(search, replaceWith);
        }
        return pool;
    }
    
    public List<String> downloadImages(String urlImage) throws Exception {
 
        String path = System.getProperty("user.dir")+ "\\src\\test\\resources\\temp\\";
        downloadTestFile.setLocalDownloadPath(path);
        List<String> images = new ArrayList<String>();

        int i = 1;      
        while(i<=4){
            
            String image = "file" + i + ".png";
            String urlPath = urlImage + "/" + image;
            
            urlChecker.setURIToCheck(urlPath);
            urlChecker.setHTTPRequestMethod(RequestMethod.GET);
            
            if(urlChecker.getHTTPStatusCode() != 404){
                String downloadedImageAbsoluteLocation = downloadTestFile.downloadImage(urlPath);
                if(new File(downloadedImageAbsoluteLocation).exists()){
                    images.add(downloadedImageAbsoluteLocation);
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
        return images;
    }

}
