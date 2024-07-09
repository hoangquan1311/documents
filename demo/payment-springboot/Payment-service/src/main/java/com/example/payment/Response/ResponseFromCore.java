package com.example.payment.Response;

public class ResponseFromCore {
    private String code;
    private String message;
    private String data;

    public ResponseFromCore(String code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseFromCore() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseFromCore{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
