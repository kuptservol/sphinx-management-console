package ru.skuptsov.sphinx.console.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Утилитарный класс для доступа к spring beans из классов, которые не иницилизируется при помощи spring.
 */
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContextProvider instance;

    private ApplicationContext applicationContext;

    public static <T> T getBean(String beanName) {
        return (T) instance.applicationContext.getBean(beanName);
    }

    public static <T> T getBean(java.lang.Class<T> tClass) {
        return (T) instance.applicationContext.getBean(tClass);
    }
    
    public static <T> T getBean(String beanName, java.lang.Class<T> tClass) {
        return (T) instance.applicationContext.getBean(beanName, tClass);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        instance = this;
        instance.applicationContext = applicationContext;
    }
}
