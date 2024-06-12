package com.appineco.spring.beans;

// Label. All beans implementing this interface will have the ability to destroy themselves (free resources, for example).
public interface DisposableBean {
  void destroy();
}
