package test.aop;

import springMVC.AOP.Annotation.*;
import springMVC.AOP.JoinPoint;
import springMVC.AnnotationAndSpecialInterfaces.Aspect;

/**
 * 这部分就是test，可以做一些参考，大家也可以自己写一写测试
 * 重点吧AOP、IOC、WEB以及循环依赖之类的都写写。
 */

@Aspect
//@Component
public class doSomeThing {
    @Pointcut("execution(public void com.pojo.User.mainMethod())")
    public  void Pointcut(){}

    @Before
    public void Before(JoinPoint joinPoint){
//        System.out.println("前置方法获取代理类" + joinPoint.getThis());
        System.out.println("前置方法执行");
    }
    @After
    public void After(JoinPoint joinPoint){
//        System.out.println("后置方法获取被代理对象" + joinPoint.getTarget());
//        System.out.println("后置方法获取被代理对象" + Arrays.toString(joinPoint.getArgs()));
        System.out.println("后置方法执行");
    }
    @AfterReturning
    public void AfterReturning(JoinPoint joinPoint, Object res){
//        System.out.println("返回方法获取返回值" + res);
        System.out.println("返回方法执行");
    }
    @AfterThrowing
    public void AfterThrowing(JoinPoint joinPoint,Exception e){
        System.out.println("异常方法返回异常值" + e);
        System.out.println("异常方法执行");
    }
}
