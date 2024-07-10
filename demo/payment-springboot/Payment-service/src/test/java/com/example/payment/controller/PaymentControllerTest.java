package com.example.payment.controller;

import com.example.payment.Request.PaymentRequest;
import com.example.payment.Response.ResponseFromCore;
import com.example.payment.Service.PaymentService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @BeforeEach
    void setUp() throws TimeoutException {
        when(paymentService.waitForCoreResponse(any(String.class), any(Long.class)))
                .thenThrow(new TimeoutException("Timeout occurred"));
    }

    @Test
    void testProcessPayment() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(200000);
        paymentRequest.setCustomerName("NGUY T T HA");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRemoteAddr("127.0.0.1");
        Thread.sleep(2 * 60 * 1000 + 1000);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(paymentRequest))
                        .requestAttr("javax.servlet.http.HttpServletRequest", mockRequest))
                .andExpect(MockMvcResultMatchers.status().isRequestTimeout());
    }
}
