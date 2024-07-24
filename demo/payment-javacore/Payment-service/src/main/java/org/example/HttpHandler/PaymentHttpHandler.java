package org.example.HttpHandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.example.Request.PaymentRequest;
import org.example.Response.ResponseFromCore;
import org.example.Service.PaymentService;
import org.example.Token.generateToken;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PaymentHttpHandler implements com.sun.net.httpserver.HttpHandler {

    private PaymentService paymentService = new PaymentService();
    private Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response = "";
        try {
            switch (method) {
                case "POST":
                    if (path.equals("/process")) {
                        PaymentRequest paymentRequest = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), PaymentRequest.class);
                        long startTime = System.currentTimeMillis();
                        String clientIp = exchange.getRemoteAddress().getAddress().getHostAddress();
                        paymentService.logRequest(paymentRequest, clientIp);
                        String token = generateToken.generateToken();
                        paymentService.sendToRabbitMQ(token, paymentRequest);
                        CompletableFuture<String> futureResponse = paymentService.startListeningForResponses(startTime);
                        paymentService.waitForCoreResponse(token, 120000);
                        response = futureResponse.get();
                    }
                    break;
            }
        } catch (Exception e) {
            System.out.println( "An error occurred while processing your request.");
        }
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
