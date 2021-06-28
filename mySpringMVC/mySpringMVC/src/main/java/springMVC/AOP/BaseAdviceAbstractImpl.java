package springMVC.AOP;

import lombok.Data;

import java.lang.reflect.Method;
//advice的的抽象实现，所有的advice都是基于它的，重点关注下它里面都有哪些成员变量
@Data
public  abstract class BaseAdviceAbstractImpl implements BaseAdvice{
//    优先级变量。不同通知的优先级是不一样的，执行顺序也不一样，优先级也是排序的理论依据
    int priority = 0;
//    所有的advice手中都得有目前的执行链
    Chain chain;
//    目标bean，也就是被增强的bean
    Object tarBean;
//    切面类对象，我们需要用它调用通知方法。
    Object aspectObject;
//    通知方法
    Method aspectMethod;
//    AOP中用来封装执行过程一些信息的JoinPoint
    JoinPoint joinPoint = new JoinPoint();
    public void initJoinPoint(){
//        获取被包装对象的传入参数
        Object[] tarArgs = chain.getTarArgs();
        joinPoint.setTarBeanArgs(tarArgs);
        joinPoint.setTarBean(tarBean);
        joinPoint.setThisProxyObject(chain.getProxyObject());
    }
}
