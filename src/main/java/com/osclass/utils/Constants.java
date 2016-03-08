package com.osclass.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constants {
    
    public Constants() {}
    
    public static Properties getProperties(String propertiesName){
        Properties prop = new Properties();
        InputStream input = null;
        String propFileName = propertiesName;
        
        try{
            input = new FileInputStream(System.getProperty("user.dir")+ "\\src\\test\\resources\\" + propFileName);
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }
}
