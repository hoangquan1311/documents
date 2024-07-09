package com.example.payment.Controller;

import com.example.payment.Request.PaymentRequest;
import com.example.payment.Response.ResponseFromCore;
import com.example.payment.Service.PaymentService;
import com.example.payment.Token.generateToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeoutException;

@Controller
@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @Value("${javainuse.rabbitmq.queue.response}")
    private String queue;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request, HttpServletRequest httpRequest) {
        long startTime = System.currentTimeMillis();

        String clientIp = httpRequest.getRemoteAddr();
        paymentService.logRequest(request, clientIp);

        String token = generateToken.generateToken();
        paymentService.sendToRabbitMQ(request, token);

        try {
            ResponseFromCore response = paymentService.waitForCoreResponse(token, 120000);
            long endTime = System.currentTimeMillis();
            long rangeTime = endTime - startTime;
            paymentService.logResponse(response, rangeTime);
            return ResponseEntity.ok(response);
        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timeout");
        }
    }

}
