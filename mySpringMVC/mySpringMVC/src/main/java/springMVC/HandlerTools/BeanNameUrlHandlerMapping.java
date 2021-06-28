package springMVC.HandlerTools;


import java.util.Map;


public class BeanNameUrlHandlerMapping extends AbstractHandlerMapping{
    public void beanNameSpecialMethod(){
        System.out.println("处理xml配置相关Mapping的特殊方法，这里已经弃用，没有");
    }

    @Override
    protected HandlerExecutionChain searchMatchedURLAddress(String requestURL) {
        return null;
    }

}
