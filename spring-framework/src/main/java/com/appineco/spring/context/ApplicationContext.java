package com.appineco.spring.context;

import com.appineco.spring.beans.BeanFactory;
import com.appineco.spring.context.event.ContextClosedEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

// Context container. Adds the ability to send and process events between beans.
// BeanFactory is usually not used by itself, as additional functionality is needed
// Therefore, BeanFactory is called through the creation of an ApplicationContext, which will create
// the factory, call all implementation methods and can also attach event handlers between beans
public class ApplicationContext {
  // First, we need to create a bean factory and initialize the creation of beans
  // Create a factory object
  private BeanFactory beanFactory = new BeanFactory();

  public ApplicationContext(String basePackage) throws ReflectiveOperationException{
    System.out.println("******Context is under construction******");

    // When initializing the application context, all factory methods will be called to create the bean, except one
    beanFactory.instantiate(basePackage);
    beanFactory.populateProperties();
    beanFactory.injectBeanNames();
    beanFactory.initializeBeans();
  }

  // Adding additional event-related logic to the bean closing stage
  public void close() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    beanFactory.close();

    // We check the beans for the presence of the ApplicationListener label - an event listener
    for(Object bean : beanFactory.getSingletons().values()) {
      // If the bean has implemented ApplicationListener<>
      if (bean instanceof ApplicationListener) {
        // We take interfaces with generics. Information about object types is stored in class metadata.
        // Otherwise, we would not be able to find out the type of the generic, since it turns into Object during compilation
        for(Type type: bean.getClass().getGenericInterfaces()){
          if(type instanceof ParameterizedType){
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // We extract the type (E) of the first argument from the generic <E> (in our case it is the only one, otherwise <E, T, P>, etc.)
            Type firstParameter = parameterizedType.getActualTypeArguments()[0];
            // If the generic type is ContextClosedEvent.class, then we execute the methods that listen to these events
            if(firstParameter.equals(ContextClosedEvent.class)){
              Method method = bean.getClass().getMethod("onApplicationEvent", ContextClosedEvent.class);
              method.invoke(bean, new ContextClosedEvent());
            }
          }
        }
      }
    }
  }
}
