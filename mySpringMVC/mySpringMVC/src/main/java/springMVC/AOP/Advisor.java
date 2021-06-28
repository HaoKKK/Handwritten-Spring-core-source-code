package springMVC.AOP;
import springMVC.AOP.Annotation.After;
import springMVC.AOP.Annotation.AfterReturning;
import springMVC.AOP.Annotation.AfterThrowing;
import springMVC.AOP.Annotation.Before;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;


public class Advisor {
//    advisor是切面类的包装对象，里面有各种通知
    private List<BaseAdviceAbstractImpl> AdviceList = new LinkedList<>();
//    切面类信息
    private Class<?> aspectClass;
//    切面类对象信息
    private Object aspectsBean;
//    被切对象
    private Object tarBean;
//   切点方法
    Method tarMethod;
//    将内部的方法打包成advice
    public void createAdvice(){
        BaseAdviceAbstractImpl advice;
        for(Method method : aspectClass.getDeclaredMethods()){
//            根据不同的注解创建不同的advic
            if(method.isAnnotationPresent(Before.class)){
                advice = new BeforeAdvice();
            }else if(method.isAnnotationPresent(After.class)){
                advice = new AfterAdvice();
            }else if(method.isAnnotationPresent(AfterReturning.class)){
                advice = new AfterReturningAdvice();
            }else if(method.isAnnotationPresent(AfterThrowing.class)){
                advice = new AfterThrowingAdvice();
            }else {
                continue;
            }
//            封装保存advice
            advice.setTarBean(tarBean);
            advice.setAspectMethod(method);
            advice.setAspectObject(aspectsBean);
            AdviceList.add(advice);
        }
    }

    public List<BaseAdviceAbstractImpl> getAdviceList() {
        return AdviceList;
    }

    public Class<?> getAspectClass() {
        return aspectClass;
    }

    public void setAspectClass(Class<?> beanClass) {
        this.aspectClass = beanClass;
    }

    public Object getAspectsBean() {
        return aspectsBean;
    }

    public void setAspectsBean(Object aspectsBean) {
        this.aspectsBean = aspectsBean;
    }

    public Object getTarBean() {
        return tarBean;
    }

    public void setTarBean(Object tarBean) {
        this.tarBean = tarBean;
    }

    public Method getTarMethod() {
        return tarMethod;
    }

    public void setTarMethod(Method myMethod) {
        this.tarMethod = myMethod;
    }

}
