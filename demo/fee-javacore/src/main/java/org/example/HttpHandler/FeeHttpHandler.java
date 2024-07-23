package org.example.HttpHandler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.example.Connection.LocalDateTimeTypeAdapter;
import org.example.Entity.FeeTransaction;
import org.example.Resquest.FeeRequest;
import org.example.Service.FeeCommandTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FeeHttpHandler implements HttpHandler {
    private FeeCommandTransactionService feeCommandTransactionService = new FeeCommandTransactionService();
    private Gson gson = new Gson();

    Gson customGson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateTimeTypeAdapter()).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()).create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response = "";

        try {
            switch (method) {
                case "GET":
                    if (path.equals("/fee")) {
                        String query = exchange.getRequestURI().getQuery();
                        String commandCode = null;
                        if (query != null) {
                            for (String param : query.split("&")) {
                                String[] pair = param.split("=");
                                if (pair.length == 2 && "commandCode".equals(pair[0])) {
                                    commandCode = pair[1];
                                    break;
                                }
                            }
                        }

                        if (commandCode != null) {
                            List<FeeTransaction> feeTransactions = feeCommandTransactionService.getFeeCommand(commandCode);
                            response = customGson.toJson(feeTransactions);
                            System.out.println("Response: " + response);
                        } else {
                            response = "commandCode parameter is missing.";
                        }
                    }
                    break;
                case "POST":
                    if (path.equals("/fee")) {
                        FeeRequest feeRequest = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), FeeRequest.class);
                        feeCommandTransactionService.addFeeCommandAndTransactions(feeRequest);
                        response = "Fee added successfully";
                    }
                    break;
                case "PUT":
                    if (path.equals("/update-fee-transactions")) {
                        feeCommandTransactionService.updateFeeTransactions();
                        response = "Update success";
                    }
                    if (path.equals("/update-charge-scan-and-status")) {
                        feeCommandTransactionService.updateChargeScanAndUpdateStatus();
                        response = "Update success";
                    }
            }
        } catch (Exception e) {
            response = "An error occurred while processing your request.";
        }
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        System.out.println("Response Length: " + response.getBytes().length);

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}