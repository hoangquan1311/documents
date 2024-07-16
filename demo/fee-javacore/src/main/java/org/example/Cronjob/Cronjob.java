package org.example.Cronjob;

import org.example.FeeCommandTransactionService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Cronjob implements Job{
    @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            FeeCommandTransactionService service = new FeeCommandTransactionService();
            service.createCronjob();
        }
}
