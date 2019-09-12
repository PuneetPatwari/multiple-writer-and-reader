package com.readwrite;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Writer implements Runnable {
    private AtomicInteger uniqueId;
    private char commitId;
    private ExecutorService es;
    private int nThreads;
    private ReaderWriterFile output;


    public Writer(char commitId, int nThreads, ReaderWriterFile output) {
        uniqueId = new AtomicInteger(0);
        this.commitId = commitId;
        this.nThreads = nThreads;
        this.output = output;
        es = Executors.newFixedThreadPool(nThreads);
    }

    @Override
    public void run() {
        int i = 0;
        while (i < nThreads) {
            es.execute(() -> {
                while (true) {
                    synchronized (this) {
                        String thName = Thread.currentThread().getName();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        output.write(commitId + "-" + thName.charAt(thName.length() - 1) + ": " + uniqueId.incrementAndGet() + ": " + "Data from " + thName + "\n");
//                                Thread.sleep(20);
                    }
                }
            });
            i++;
        }
    }
}
