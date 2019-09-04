package com.readwrite;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReaderMain {

    public static void main(Properties properties, int nTypes) throws IOException {
        ExecutorService es = Executors.newFixedThreadPool(nTypes);
        RandomAccessFile file = new RandomAccessFile("src/main/resources/output.txt", "rw");
//        RandomAccessFile file = new RandomAccessFile("/tmp/output.txt", "rw");

        for (int i = 0; i < nTypes; i++) {
            char commitId = (char) (i + 65); //Assuming that we will have commitID like A,B,C, to Z only

            // Number of threads for a commitId
            int nThreads = Integer.parseInt(properties.getProperty((String.valueOf(commitId))));

            Reader reader = new Reader(commitId,nThreads,file);
            es.execute(reader);
        }
        es.shutdown();
    }
}
