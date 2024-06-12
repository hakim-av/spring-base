package com.appineco.spring.application;

import com.appineco.spring.beans.BeanNameAware;
import com.appineco.spring.beans.stereotype.Service;
import com.appineco.spring.context.ApplicationListener;
import com.appineco.spring.context.event.ContextClosedEvent;

// This class is marked as a bean, marked with the knowledge mark of the bean name, marked with the ContextClosedEvent event listener mark
@Service
public class PromotionService  implements BeanNameAware, ApplicationListener<ContextClosedEvent> {
  private String beanName;

  // Possibility to find out the bean name
  // Label BeanNameAware
  @Override
  public void setBeanName(String name) {
    beanName = name;
  }

  public String getBeanName() {
    return beanName;
  }

  // The code that will be executed when the ContextClosedEvent event occurs
  @Override
  public void onApplicationEvent(ContextClosedEvent event) {
    System.out.println(">> ContextClosed EVENT");
  }
}
