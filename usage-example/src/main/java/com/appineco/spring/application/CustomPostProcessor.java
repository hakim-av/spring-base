package com.appineco.spring.application;

import com.appineco.spring.beans.config.BeanPostProcessor;

// Implementation of the BeanPostProcessor interface. Performing some logic before and after the bean is initialized
public class CustomPostProcessor implements BeanPostProcessor {
  // Let's just display some information
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    System.out.println("---CustomPostProcessor Before " + beanName);
    return bean;
  }

  // Let's just display some information
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    System.out.println("---CustomPostProcessor After " + beanName);
    return bean;
  }
}