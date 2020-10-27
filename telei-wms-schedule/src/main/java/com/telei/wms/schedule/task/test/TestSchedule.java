package com.telei.wms.schedule.task.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/11 18:38
 */

@Slf4j
@Component
public class TestSchedule {
    @Autowired
    private TestScheduleHandler testScheduleHandler;

//    @Scheduled(cron = TaskCron.EVERY_SECONDS_10)
    public void testbiz(){

        testScheduleHandler.run();
    }
}
