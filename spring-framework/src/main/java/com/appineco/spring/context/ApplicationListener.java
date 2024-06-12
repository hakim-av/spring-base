package com.appineco.spring.context;

// A label indicating that the bean is listening for certain events
public interface ApplicationListener<E>{
  void onApplicationEvent(E event);
}
