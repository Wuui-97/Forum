package com.wuui.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Dee
 * @create 2022-06-02-20:56
 * @describe
 */

@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
}
