package com.appineco.spring.application;

import com.appineco.spring.beans.BeanFactory;
import com.appineco.spring.context.ApplicationContext;

public class Main {

  public static void main(String[] args){
    new Main();
  }

  public Main(){
    try {
      // 1. We launch the bean creation factory if we use only the factory
      System.out.println("===== ONLY FACTORY =====");
      testBeanFactory();
      // 2. We start the application context, this is how a Spring application starts
      System.out.println("===== FROM CONTEXT =====");
      testContext();
    }catch (ReflectiveOperationException e){
      e.printStackTrace();
    }
  }

  // 1. The process of starting a bean factory if you use only the bean factory
  void testBeanFactory() throws ReflectiveOperationException{
    // Create a factory object
    BeanFactory beanFactory = new BeanFactory();
    // Adding custom post processors
    beanFactory.addPostProcessor(new CustomPostProcessor());

    // Let's start creating beans
    beanFactory.instantiate("com.appineco.spring.application");
    // Filling the bean with properties
    beanFactory.populateProperties();
    beanFactory.injectBeanNames();
    // Bean initialization
    beanFactory.initializeBeans();

    // Checking that the beans have been created
    System.out.println("!!! CHECK HAS STARTED !!!");

    ProductService productService = (ProductService) beanFactory.getBean("productService");
    System.out.println("Checking that the ProductService bean exists: " + productService);

    PromotionService promotionService = productService.getPromotionService();
    System.out.println("Checking that the PromotionService bean exists: " + promotionService);
    System.out.println("Promotion service bean name: " + promotionService.getBeanName());

    System.out.println("PromotionService bean class" + promotionService.getClass());

    System.out.println("!!! CHECK ENDED !!!");

    // We close the factory and destroy the beans
    beanFactory.close();
  }

  // 2. The process of starting an application context. It is through this that the context is called and the bean factory is created
  void testContext() throws ReflectiveOperationException{
    ApplicationContext applicationContext = new ApplicationContext("com.appineco.spring.application");
    applicationContext.close();
  }
}
