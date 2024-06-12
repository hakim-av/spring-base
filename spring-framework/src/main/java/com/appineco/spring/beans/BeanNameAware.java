package com.appineco.spring.beans;

// A label for a bean means that this bean can recognize its bean name in a collection of beans
public interface BeanNameAware {
  void setBeanName(String name);
}
