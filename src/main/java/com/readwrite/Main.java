package com.readwrite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        try {
            InputStream input = new FileInputStream("src/main/resources/config.properties");
//            InputStream input = new FileInputStream("/resources/config.properties");
            properties.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int nTypes = Integer.parseInt(properties.getProperty("types")); //Number of unique commit IDs

        WriterMain.main(properties,nTypes);
        ReaderMain.main(properties,nTypes);
    }
}
