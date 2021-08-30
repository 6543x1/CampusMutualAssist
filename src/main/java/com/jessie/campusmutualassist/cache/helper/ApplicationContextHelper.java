package com.jessie.campusmutualassist.cache.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHelper implements ApplicationContextAware {

    private static ApplicationContext ctx;

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    @Override
    synchronized public void setApplicationContext(ApplicationContext appContext)
            throws BeansException {

        ApplicationContextHelper.ctx = appContext;

    }
}
