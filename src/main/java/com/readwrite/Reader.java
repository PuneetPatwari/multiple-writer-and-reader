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
        try {
            int i = 0;
            while (i < nThreads) {
                es.execute(() -> {
                    FileChannel fc = null;
                    FileLock fileLock = null;
                    try {
                        while (true) {
                            {
                                try {
                                    fc = input.getChannel();
//                                    fileLock = fc.tryLock(0, input.length(), false);
                                    synchronized(input) {
                                        String log = "";
//                                        if ((log = input.readLine()) != null) {
//                                        System.out.println(log);
                                            //Checks if the readers commitId matches with log's 1st character. Simple match
//                                        System.out.println(log.charAt(1));
//                                            if (commitId == log.charAt(0))
                                        long pointer = input.getFilePointer();
                                        if (commitId == (char)input.read()) {
                                            input.seek(pointer);
                                            log = input.readLine();
                                            System.out.println(log);
                                        }
                                        else
                                            input.seek(pointer);


//                                    }
                                    }
//                                    fileLock.release();
                                } catch (final OverlappingFileLockException | IOException e) {
                                    if (fileLock != null && fileLock.isValid())
                                        fileLock.release();
                                    Thread.sleep(20);
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }

                });
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
