package test;

import test.configuration.ApplicationConfig;
import test.pojo.User;
import org.junit.Test;
import springMVC.Context.MyAnnotationApplicationContext;
import springMVC.Context.WebApplicationContext;
import springMVC.XML.XmlParser;
import test.advice.*;

import java.lang.reflect.InvocationTargetException;

public class test {
    @Test
    public void test() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        MyAnnotationApplicationContext myAnnotationApplicationContext = new MyAnnotationApplicationContext(ApplicationConfig.class);

    }
    @Test
    public void test2() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        WebApplicationContext applicationContext = new WebApplicationContext("classpath:SpringMVC.xml");
        User testAopUser = (User)applicationContext.getBean("testAopUser");
        testAopUser.mainMethod(1);
        System.out.println("************");
        testAopUser.mainMethod(2);
        System.out.println("************");
        testAopUser.mainMethod(3);
    }
    @Test
    public void readXml(){
        String basePackage = XmlParser.getBasePackage("SpringMVC.xml");
        System.out.println(basePackage);
    }


    @Test
    public void testScanXml() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        User user = new User();
        Object u = new User();
        System.out.println(u.getClass());
    }
    @Test
    public void pathMatching() {
        Chain chain = new Chain();
        chain.chainsImpl.add(new AfterThrowingAdvice(chain));
        chain.chainsImpl.add(new AfterReturningAdvice(chain));
        chain.chainsImpl.add(new AfterAdvice(chain));
//        chain.chainsImpl.add(new RoundAdvice(chain));
        chain.chainsImpl.add(new beforeAdvice(chain));

        chain.chainsImpl.add(new AfterThrowingAdvice2(chain));
        chain.chainsImpl.add(new AfterReturningAdvice2(chain));
        chain.chainsImpl.add(new AfterAdvice2(chain));
//        chain.chainsImpl.add(new RoundAdvice2(chain));
        chain.chainsImpl.add(new beforeAdvice2(chain));


        chain.proceed();
    }
}
