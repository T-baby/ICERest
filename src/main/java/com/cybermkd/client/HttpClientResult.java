package com.cybermkd.client;

import com.cybermkd.common.http.result.HttpStatus;

/**
 * Created by wangrenhui on 15/1/11.
 */
public class HttpClientResult {
    private HttpStatus status;
    private String result;

    public HttpClientResult(HttpStatus status, String result) {
        this.status = status;
        this.result = result;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }

    public String toString() {
        return "HttpClientResult{" +
                "status=" + status.getCode() +
                ", result='" + result + '\'' +
                '}';
    }
}
