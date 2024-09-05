package com.example.demo;

import com.example.demo.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Autowired
    private MessageService productService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        productService.syncMessagesToElasticsearch();
    }
}