package com.wuui.community.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Dee
 * @create 2022-06-02-21:14
 * @describe
 */

public class AlphaJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("当前线程为：" + Thread.currentThread().getName() + "is executing a job!");
    }
}
