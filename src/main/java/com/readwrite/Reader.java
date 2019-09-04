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
    private RandomAccessFile input;

    public Reader(char commitId, int nThreads, RandomAccessFile input) {
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
                FileChannel fc = null;
                FileLock fileLock = null;
                while (true) {
                    try {
                        String log = "";
                        fc = input.getChannel();
                        fileLock = fc.tryLock(0, input.length(), false);
                        long pointer = input.getFilePointer();
                        //Checks if the readers commitId matches with log's 1st character. Simple match
                        if (commitId == (char) input.read()) {
                            input.seek(pointer);
                            log = input.readLine();
                            System.out.println(log);
                        } else
                            input.seek(pointer);
                        fileLock.release();
                    } catch (final OverlappingFileLockException | IOException e) {
//                                Thread.sleep(20);
                    }
                }

            });
            i++;
        }
    }
}
