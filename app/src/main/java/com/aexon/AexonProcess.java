package com.aexon;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AexonProcess extends Process {

    private final InputStream inputStream;
    private final InputStream errorStream;

    AexonProcess(String result) {
        this.inputStream = new ByteArrayInputStream(result.getBytes());
        this.errorStream = new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public InputStream getErrorStream() {
        return errorStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return OutputStream.nullOutputStream();
    }

    @Override
    public int waitFor() {
        return 0;
    }

    @Override
    public int exitValue() {
        return 0;
    }

    @Override
    public void destroy() {}
}