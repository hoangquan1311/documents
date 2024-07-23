package org.example.Cronjob;

import org.example.Service.FeeCommandTransactionService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cronjob implements Job{
    public Logger logger = LoggerFactory.getLogger(Cronjob.class);
    @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            FeeCommandTransactionService service = new FeeCommandTransactionService();
            service.createCronjob();
        }
}
