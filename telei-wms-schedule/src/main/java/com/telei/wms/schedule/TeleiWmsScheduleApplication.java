package com.telei.wms.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/11 15:52
 */
@SpringBootApplication(scanBasePackages = {"com.nuochen","com.telei"})
@EnableScheduling
public class TeleiWmsScheduleApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeleiWmsScheduleApplication.class);
    }
}
