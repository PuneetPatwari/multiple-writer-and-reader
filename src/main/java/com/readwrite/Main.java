package com.readwrite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        InputStream input = new FileInputStream("src/main/resources/config.properties");
        properties.load(input);
        int nTypes = Integer.parseInt(properties.getProperty("types")); //Number of unique commit IDs

        ReaderWriterFile file = new ReaderWriterFile();
        WriterMain.main(properties, nTypes, file);
        ReaderMain.main(properties, nTypes, file);
    }
}
