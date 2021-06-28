package springMVC.AOP;

import java.lang.reflect.InvocationTargetException;
//抛出异常的advice-异常通知
public class AfterThrowingAdvice extends BaseAdviceAbstractImpl{
    public AfterThrowingAdvice(){
        priority = 0;
    }
    @Override
    public Object execute(Chain chain) throws InvocationTargetException, IllegalAccessException {
        Object res;
        this.chain = chain;
        initJoinPoint();
        try {
            res = chain.proceed();
            return res;
//            异常通知放到catch里就好
        } catch (Exception e) {
            Object invoke ;
            if(aspectMethod.getParameters().length == 1){
                invoke = aspectMethod.invoke(aspectObject, joinPoint);
            }else if(aspectMethod.getParameters().length == 0){
                invoke = aspectMethod.invoke(aspectObject);
            }else {
                Object[] args = new Object[2];
                args[0] = joinPoint;
                args[1] = e;
                invoke = aspectMethod.invoke(aspectObject,args);
            }
//            注意catch完执行完逻辑，还得把异常throw了，否则后项通知也会执行哦
//            想想为什么呢？？
            throw e;
        }
    }
}
