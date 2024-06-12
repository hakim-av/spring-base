package com.appineco.spring.beans;

// A label that marks the bean with the ability to execute some code after the dependencies have been injected
public interface InitializingBean {
  void afterPropertiesSet();
}
