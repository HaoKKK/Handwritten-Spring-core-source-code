package test.aop;

import springMVC.AOP.Annotation.*;
import springMVC.AnnotationAndSpecialInterfaces.Aspect;
import springMVC.AnnotationAndSpecialInterfaces.Component;

@Aspect
@Component
public class doSomeThing2 {
    @Pointcut("execution(public void com.pojo.User.mainMethod())")
    public  void Pointcut(){}

    @Before
    public void Before(){
        System.out.println("前置方法2执行");
    }
    @After
    public void After(){
        System.out.println("后置方法2执行");
    }
    @AfterReturning
    public void AfterReturning(){
        System.out.println("返回方法2执行");
    }
    @AfterThrowing
    public void AfterThrowing(){
        System.out.println("异常方法2执行");
    }
}
