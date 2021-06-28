package test.pojo;


import springMVC.AnnotationAndSpecialInterfaces.BeanPostProcessor;
import springMVC.AnnotationAndSpecialInterfaces.Component;

@Component("beanPostProcessor")
public class BeanPostProcessorImp implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
//        System.out.println("初始化方法执行之后");
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
//        System.out.println("初始化方法执行之前");
        return bean;
    }
}
