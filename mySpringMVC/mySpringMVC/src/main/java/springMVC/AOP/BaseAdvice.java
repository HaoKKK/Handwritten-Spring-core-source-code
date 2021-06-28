package springMVC.AOP;

import java.lang.reflect.InvocationTargetException;
//基础advice接口
public interface BaseAdvice {
    Object execute(Chain chain) throws InvocationTargetException, IllegalAccessException;
}
