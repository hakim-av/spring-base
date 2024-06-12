package com.appineco.spring.application;

import com.appineco.spring.beans.DisposableBean;
import com.appineco.spring.beans.InitializingBean;
import com.appineco.spring.beans.annotation.Autowired;
import com.appineco.spring.beans.stereotype.Component;
import javax.myannotation.PreDestroy;

// This class is marked as a bean, marked with the ability to perform additional logic
// when creating a bean and when destroying a bean
@Component
public class ProductService implements InitializingBean, DisposableBean {

  // DI
  @Autowired
  private PromotionService promotionService;

  public PromotionService getPromotionService() {
    return promotionService;
  }

  public void setPromotionService(PromotionService promotionService) {
    this.promotionService = promotionService;
  }

  // The code that should be executed in the bean after the dependencies have been injected
  // Label InitializingBean
  @Override
  public void afterPropertiesSet() {
    System.out.println("PromotionService init...");
  }

  // The method that should be executed on the bean when it is destroyed
  // Label DisposableBean
  @Override
  public void destroy() {
    System.out.println("PromotionService destroy...");
  }

  // The method that must be executed on a bean before it is destroyed
  // Label @PreDestroy
  @PreDestroy
  public void destroy2() {
    System.out.println("PromotionService @PreDestroy...");
  }
}