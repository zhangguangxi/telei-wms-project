package com.telei.wms.schedule.task.test;


import com.nuochen.framework.component.task.TaskContext;
import com.nuochen.framework.component.task.TaskHandler;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.schedule.task.service.GoodsBussiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/11 18:40
 */
@Slf4j
@Component
public class TestScheduleHandler extends TaskHandler {
    @Autowired
    private GoodsBussiness goodsBussiness;

    @Autowired
    private Id idGenerator;

    protected TestScheduleHandler() {
        super("test1");
    }

    @Override
    protected void handle(TaskContext context) {
        goodsBussiness.addGoods();
    }
}


