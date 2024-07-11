package org.example.Entity;


import org.example.Request.PaymentRequest;

public class PaymentMessage {
    private PaymentRequest paymentRequest;
    private String token;

    public PaymentMessage(String token, PaymentRequest paymentRequest ) {
        this.token = token;
        this.paymentRequest = paymentRequest;
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
