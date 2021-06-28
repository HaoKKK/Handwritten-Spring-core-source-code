package springMVC.AOP;
import java.lang.reflect.InvocationTargetException;


/**
 * 注意各种advice的执行就是典型的责任链设计模式，一定要关注其设计精髓，我们会一步步重点注解AfterAdvice
 * 后面的类似的地方就不过多赘述
 */
//后项通知，无论方法执行失败与否都会执行
public class AfterAdvice extends BaseAdviceAbstractImpl{
//  根据通知的性质和执行位置确定优先级
    public AfterAdvice(){
        priority = 2;
    }
//    核心逻辑：通知的执行
    @Override
    public Object execute(Chain chain) throws InvocationTargetException, IllegalAccessException {
//        通知返回的结果，如果当日志打印，其实没啥结果
        Object invoke;
//        责任链执行结果
        Object res = null;
//        责任链
        this.chain = chain;
//        拿到初始化的JoinPoint
        initJoinPoint();
//       想象一下该如何保证后项通知的执行？是不是只要放到finally就可以啦！
        try {
//            责任链执行
            res = chain.proceed();
        } finally {
//            通知执行，这边只有一个参数的话我们默认就是要封装joinPoint了
            if(aspectMethod.getParameters().length == 1){
                invoke = aspectMethod.invoke(aspectObject, joinPoint);
            }else{
                invoke = aspectMethod.invoke(aspectObject);
            }
        }
        return res;
    }
}
