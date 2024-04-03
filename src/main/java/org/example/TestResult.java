package org.example;

import java.net.HttpURLConnection;

public class TestResult {
    private String dataType;
    private String contentType;
    private String dataEncoding;
    private boolean success;
    private Object responseCode;
    private Object responseMessage;
    private byte[] responseData;
    private String responseHeaders;

    private long startTime = 0;
    private long connectTime = 0;
    private long latency = 0;
    private long endTime = 0;

    public static final String TEXT = "text"; // $NON-NLS-1$
    private static final String OK_CODE = Integer.toString(HttpURLConnection.HTTP_OK);
    private static final String OK_MSG = "OK"; // $NON-NLS-1$
    private static final byte[] EMPTY = new byte[0];
    private long elapsedTime;


    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public void setContentType(String string) {
        contentType = string;
    }

    public void setDataEncoding(String dataEncoding) {
        this.dataEncoding = dataEncoding;
    }

    public void setSuccessful(boolean success) {
        this.success = success;
    }
    public void setMsgOK() {
        this.responseMessage = OK_MSG;
    }

    public void setCodeOK(){
        this.responseCode=OK_CODE;
    }

    public void setRespData(byte[] response) {
//        this.responseDataAsString = null;
        this.responseData = response == null ? EMPTY : response;
    }
    public void setRespHeaders(String string) {
        this.responseHeaders = string;
    }

    public void markStart() {
        startTime = System.currentTimeMillis();
    }

    public void connectionEstablished() {
        connectTime = System.currentTimeMillis() - startTime;
    }

    public void execTerm() {
        latency = System.currentTimeMillis() - startTime;
    }

    public void markTerminated() {
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;
    }

    public long getBytes() {
        return responseData.length;
    }

    public int getErrorCount() {
        return success ? 0 : 1;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getTime() {
        return elapsedTime;
    }
}
