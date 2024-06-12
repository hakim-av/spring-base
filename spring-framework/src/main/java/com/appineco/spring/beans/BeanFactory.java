package com.appineco.spring.beans;

import com.appineco.spring.beans.annotation.Autowired;
import com.appineco.spring.beans.config.BeanPostProcessor;
import com.appineco.spring.beans.stereotype.Component;
import com.appineco.spring.beans.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.myannotation.PreDestroy;

/**
 * Factory for creating beans. This is exactly how the entire stage of creating beans occurs in Spring
 *
 * Life cycle:
 * 1. Instantiate - The process of creating bean objects and filling Maps with beans and their name
 * 2. Populate Properties - Executing DI, populating the bean with its properties and dependencies
 * 3. Awareness - Adding additional capabilities to the bean to inform it about its environment
 *    3.1. BeanNameAware - We give the bean a chance to find out his name
 *    3.2. BeanFactoryAware - We give the bean the opportunity to find out the factory that spawned it
 * 4. PostProcessors (Before Initialization) - Executing logic that must be executed before the bean is initialized
 *    a) @PostConstruct from Javax - Another opportunity to execute logic before the bean is initialized
 *    4.1 Initialization - Directly initializing the bean
 * 5. PostProcessors (After Initialization) - Executing the logic that needs to be executed after the bean is initialized
 * 6. Destroying (Disposable Bean) - Executing the logic that needs to be executed during bean destruction
 *    -a) @javax.myannotation.PreDestroy from Javax - Executing the logic that must be executed before the bean is destroyed
 *
 */
public class BeanFactory {

  // Beans storage
  private Map<String, Object> singletons = new HashMap<>();

  // Finds a bean by name
  public Object getBean(String beanName) {
    return singletons.get(beanName);
  }

  // List of post processors
  private List<BeanPostProcessor> postProcessors = new ArrayList<>();

  // Adds a post processor
  public void addPostProcessor(BeanPostProcessor postProcessor) {
    postProcessors.add(postProcessor);
  }

  // Returns a map of beans
  public Map<String, Object> getSingletons() {
    return singletons;
  }

  // Scanning a package for beans
  public void instantiate(String basePackage) {
    try{
      ClassLoader classLoader = ClassLoader.getSystemClassLoader();

      // Replacing dots in package names with slashes
      String path = basePackage.replace('.', '/');

      // Using the classloader, we convert the passed path into a URL collection with addresses.
      // In this case there will be only one object in the collection
      Enumeration<URL> resources = classLoader.getResources(path);

      // We go through the found URLs
      while (resources.hasMoreElements()) {
        URL resource = resources.nextElement();
        // Converting the URL path into a file
        File file = new File(resource.toURI());

        // We scan our url file for the presence of other files inside
        for (File classFile : file.listFiles()) {
          String fileName = classFile.getName();
          System.out.println(fileName); // ProductService.class, PromotionService.class ...

          // Select a file only if it has a .class extension
          if (fileName.endsWith(".class")) {
            String className = fileName.substring(0, fileName.lastIndexOf("."));
            // Take the class of this file
            Class classObject = Class.forName(
                basePackage + "." + className
            );
            // Scanning a file class for annotations
            if (classObject.isAnnotationPresent(Component.class)
                || classObject.isAnnotationPresent(Service.class)) {
              System.out.println("Component: " + classObject); // Component: class com.appineco.application.ProductService ...
              // We initialize the creation of an object by class, where there are annotations
              Object instance = classObject.newInstance();
              // Set the bean name
              String beanName = className.substring(0, 1).toLowerCase() + className.substring(1);

              // We put the initialized bean in the collection of singletons
              singletons.put(beanName, instance);
            }
          }
        }
      }
    } catch (IOException
             | URISyntaxException
             | ClassNotFoundException
             | IllegalAccessException
             | InstantiationException e) {
      e.printStackTrace();
    }
  }

  // DI process. Injecting beans where they are needed
  public void populateProperties() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    System.out.println("==populateProperties==");

    // We go through all available beans
    for (Object object : singletons.values()) {
      // We go through the declared fields of each bean, even private ones
      for (Field field : object.getClass().getDeclaredFields()) {
        // Select the field where the Autowired annotation is present
        if (field.isAnnotationPresent(Autowired.class)) {

          // We check the type of annotated field with the beans we have to see if it is possible to use DI
          for (Object dependency : singletons.values()) {
            // We find the bean that suits
            if (dependency.getClass().equals(field.getType())) {
              // Create a setter for this field (this is needed in case the field is private)
              String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
              System.out.println("Setter name = " + setterName); // Setter name = setPromotionService
              // We take the newly created setter and make a class method out of it
              Method setter = object.getClass().getMethod(setterName, dependency.getClass());
              // We insert our bean from the collection of beans into the current bean. Injection by type
              setter.invoke(object, dependency);
            }
          }
        }
      }
    }
  }

  // We iterate through our singleton beans, check to see if the bean implements the BeanNameAware interface, and call the setter
  public void injectBeanNames() {
    for (String name : singletons.keySet()) {
      Object bean = singletons.get(name);

      if (bean instanceof BeanNameAware) {
        ((BeanNameAware) bean).setBeanName(name);
      }
    }
  }

  // Intervention in the bean creation phase. We iterate through our singleton beans and call the necessary methods
  public void initializeBeans() {
    for (String name : singletons.keySet()) {
      Object bean = singletons.get(name);

      // Calling methods that must be executed before the bean is initialized
      for (BeanPostProcessor postProcessor : postProcessors) {
        postProcessor.postProcessBeforeInitialization(bean, name);
      }

      // We check whether the bean implements the InitializingBean interface; if so, then call the afterPropertiesSet() method
      if (bean instanceof InitializingBean) {
        ((InitializingBean) bean).afterPropertiesSet();
      }

      // Calling methods that should be called after the bean is initialized
      for (BeanPostProcessor postProcessor : postProcessors) {
        postProcessor.postProcessAfterInitialization(bean, name);
      }
    }
  }

  // Interfering with the bean destruction process
  public void close() {
    for (Object bean : singletons.values()) {
      for (Method method : bean.getClass().getMethods()) {
        // Method that must be called before destroying the bean
        if (method.isAnnotationPresent(PreDestroy.class)) {
          try {
            method.invoke(bean);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }
      // Call a method, if any, that performs additional logic during destruction
      if (bean instanceof DisposableBean) {
        ((DisposableBean) bean).destroy();
      }
    }
  }

}
