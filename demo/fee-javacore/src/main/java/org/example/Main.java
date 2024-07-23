package org.example;


import com.sun.net.httpserver.HttpServer;
import org.example.Cronjob.Cronjob;
import org.example.HttpHandler.FeeHttpHandler;
import org.example.Service.FeeCommandTransactionService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public Logger logger = LoggerFactory.getLogger(Main.class);
    //    public static void main(String[] args) throws SchedulerException {
//        FeeCommandTransactionService service = new FeeCommandTransactionService();
//        service.addFeeCommandAndTransactions("hjawfhwqsnmkfu123894y222233", "20230623155051", "FC230623523226", 1000, 1000, "admin", "20230623155051");
//        service.getFeeCommand("FC230623523226");
//        service.updateFeeTransactions();
//        service.updateChargeScanAndUpdateStatus();
//
//        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//        JobDetail job = JobBuilder.newJob(Cronjob.class)
//                .withIdentity("job1", "group1")
//                .build();
//        Trigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity("feeTransactionTrigger", "group1")
//                .startNow()
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
//                        .withIntervalInMinutes(3)
//                        .repeatForever())
//                .build();
//        scheduler.scheduleJob(job, trigger);
//
//        scheduler.start();
//    }
    public static void main(String[] args) throws IOException, SchedulerException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/fee", new FeeHttpHandler());
        server.createContext("/update-fee-transactions", new FeeHttpHandler());
        server.createContext("/update-charge-scan-and-status", new FeeHttpHandler());
        server.setExecutor(null);
        System.out.println("Server started on port 8080");
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job = JobBuilder.newJob(Cronjob.class)
                .withIdentity("job1", "group1")
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("feeTransactionTrigger", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(5)
                        .repeatForever())
                .build();
        scheduler.scheduleJob(job, trigger);
        scheduler.start();
        server.start();
    }
}