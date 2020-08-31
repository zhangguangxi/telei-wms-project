package com.telei.wms.schedule.configuration;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 自定义监听器
 */
@Component
public class CustomizeListener implements ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            onReady((ApplicationReadyEvent) event);
            return;
        }
        if (event instanceof ApplicationFailedEvent) {
            onFailed((ApplicationFailedEvent) event);
        }
    }

    private void onReady(ApplicationReadyEvent event) {
        final ConfigurableApplicationContext context = event.getApplicationContext();

    }

    private void onFailed(ApplicationFailedEvent event) {
    }
}
