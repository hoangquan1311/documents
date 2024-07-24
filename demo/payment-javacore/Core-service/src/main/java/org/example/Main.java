package org.example;

import org.example.Service.CoreService;

public class Main {
    public static void main(String[] args) {
        CoreService coreService = new CoreService();
        coreService.startListening();
    }
}
