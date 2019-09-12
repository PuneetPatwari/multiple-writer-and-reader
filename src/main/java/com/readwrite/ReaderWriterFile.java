package com.readwrite;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReaderWriterFile {
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private RandomAccessFile file = new RandomAccessFile("src/main/resources/output.txt", "rw");;
    final long SIZE = 1024L * 1;    //5KB size restriction for the log file
    AtomicLong pointer = new AtomicLong(0);

    public ReaderWriterFile() throws FileNotFoundException {
    }

    public void write(String s) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();

        try {
            if (file.length() < SIZE) {
                file.writeBytes(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    public String read(char commitId) {
        Lock readLock = rwLock.readLock();
        readLock.lock();

        try {
            file.seek(pointer.get());
            //Checks if the readers commitId matches with log's 1st character. Simple match
            if (commitId == (char) file.read()) {
                file.seek(pointer.get());
                String line = file.readLine();
                pointer.set(file.getFilePointer());
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        return null;
    }


}
