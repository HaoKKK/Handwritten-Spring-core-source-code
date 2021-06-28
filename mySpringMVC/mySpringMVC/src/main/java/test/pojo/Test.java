package test.pojo;


import springMVC.Context.WebApplicationContext;

import java.lang.reflect.InvocationTargetException;

public class Test {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String contextConfigLocation = "classpath:SpringMVC.xml";
        WebApplicationContext webApplicationContext = new WebApplicationContext(contextConfigLocation);
        User user = (User)webApplicationContext.getBean("testAopUser");
        user.mainMethod(2);
    }
}
