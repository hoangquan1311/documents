package com.example.core.Entity;

import com.example.core.Request.PaymentRequest;

public class PaymentMessage {
    private PaymentRequest paymentRequest;
    private String token;

    public PaymentMessage(PaymentRequest paymentRequest, String token) {
        this.paymentRequest = paymentRequest;
        this.token = token;
    }

    public PaymentRequest getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(PaymentRequest paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "paymentRequest=" + paymentRequest +
                ", token='" + token + '\'' +
                '}';
    }
}
