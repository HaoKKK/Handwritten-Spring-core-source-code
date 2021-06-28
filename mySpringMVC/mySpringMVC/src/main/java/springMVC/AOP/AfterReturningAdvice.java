package springMVC.AOP;

import java.lang.reflect.InvocationTargetException;

//返回通知，只有正常返回才有
public class AfterReturningAdvice extends BaseAdviceAbstractImpl{
    public AfterReturningAdvice(){
        priority = 1;
    }
    @Override
    public Object execute(Chain chain) throws InvocationTargetException, IllegalAccessException {
//        大家有没有方法，advice需要拿到chain，但是否有chain这个成员变量其实无所谓
        this.chain = chain;
//        责任链执行
        Object res = chain.proceed();
        Object invoke;
        initJoinPoint();
//        如果不报错，程序能正常执行到这里，就开始判断参数
        if(aspectMethod.getParameters().length == 1){
            invoke = aspectMethod.invoke(aspectObject, joinPoint);
        }else if(aspectMethod.getParameters().length == 0){
            invoke = aspectMethod.invoke(aspectObject);
        }else {
            Object[] args = new Object[2];
            args[0] = joinPoint;
            args[1] = res;
            invoke = aspectMethod.invoke(aspectObject,args);
        }
        return res;
    }
}
