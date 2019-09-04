package com.readwrite;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Writer implements Runnable {
    final long SIZE = 1024L * 5; //5KB size restriction for the log file
    private AtomicInteger uniqueId;
    private char commitId;
    private ExecutorService es;
    private int nThreads;
    private RandomAccessFile output;


    public Writer(char commitId, int nThreads, RandomAccessFile output) {
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
                FileChannel fc = null;
                FileLock fileLock = null;
                while (true) {
                    try {
                        fc = output.getChannel();
                        fileLock = fc.tryLock();
                        if (output.length() < SIZE) {
                            String thName = Thread.currentThread().getName();
                            output.writeBytes(commitId + "-" + thName.charAt(thName.length() - 1) + ": " + uniqueId.incrementAndGet() + ": " + "Data from " + thName + "\n");
                        }
//                                Thread.sleep(20);
                        fileLock.release();
                    } catch (final OverlappingFileLockException | IOException e) {
                    }
                }
            });
            i++;
        }
    }
}
