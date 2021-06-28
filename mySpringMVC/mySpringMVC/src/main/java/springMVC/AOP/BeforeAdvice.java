package springMVC.AOP;
import java.lang.reflect.InvocationTargetException;

//前项通知
public class BeforeAdvice extends BaseAdviceAbstractImpl{
    public BeforeAdvice(){
        priority = 4;
    }
    @Override
    public Object execute(Chain chain) throws InvocationTargetException, IllegalAccessException {
        this.chain = chain;
        initJoinPoint();
        if(aspectMethod.getParameters().length != 0){
            Object invoke = aspectMethod.invoke(aspectObject, joinPoint);
        }else {
            Object invoke = aspectMethod.invoke(aspectObject);
        }
        return chain.proceed();
    }

}
