package com.appineco.spring.beans.config;

// Implementation of the ability to interfere with the creation of beans.
// Using this, you can create a proxy object that wraps its calls (and adds security)
// Or you can return another object altogether by re-creating the bean again
public interface BeanPostProcessor {
  Object postProcessBeforeInitialization(Object bean, String beanName);
  Object postProcessAfterInitialization(Object bean, String beanName);
}
