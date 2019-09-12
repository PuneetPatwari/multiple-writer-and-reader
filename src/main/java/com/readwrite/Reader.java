package com.readwrite;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Reader implements Runnable {

    private char commitId;
    ExecutorService es;
    private int nThreads;
    private ReaderWriterFile input;

    public Reader(char commitId, int nThreads, ReaderWriterFile input) {
        this.commitId = commitId;
        this.nThreads = nThreads;
        this.input = input;
        es = Executors.newFixedThreadPool(nThreads);
    }

    @Override
    public void run() {
        int i = 0;
        while (i < nThreads) {
            es.execute(() -> {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String line = input.read(commitId);
                    if (line != null)
                        System.out.println(line);
                }
            });
            i++;
        }
    }
}
