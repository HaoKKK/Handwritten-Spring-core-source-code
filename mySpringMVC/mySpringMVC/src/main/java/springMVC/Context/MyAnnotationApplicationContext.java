package springMVC.Context;
import springMVC.AnnotationAndSpecialInterfaces.*;
import springMVC.exception.ContextException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

//提醒下，我们这里并没有把AnnotationApplicationContext加入到MVC设计架构中，MVC是采用WebApplicationContext，通过配置SpringMVC的xml文件确认扫描路径的。
//这里后续可能会扩展进来，问题不大，这里的AnnotationContext的功能是完备的，只需要加入到DispatchServlet初始化参数，然后确定配置扫描类即可。
public class MyAnnotationApplicationContext extends AbstractApplicationContext{
    private final Class configClass;

    public List<Object> getWebAnnotationHandlerClasses() {
        return webAnnotationHandlerObjects;
    }
    public List<Object> getAspectsBeans(){return aspectsBeans;}
    public MyAnnotationApplicationContext(Class configClass) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.configClass = configClass;
        refresh();
    }


    @Override
    protected List<Class> scanAndLoadBeanClass() {
        List<Class> beanClassList = new LinkedList<>();
//        找到需要的Class,扫描
//        configClass.isAnnotationPresent(Component.class)最好先判断有没有,
//        这边默认是通过Component注解来实现扫描路径确定，也可以通过解析XML文件，像WebApplicationContext，这里可以做一些补充
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
//        拿到路径（注意这里只能拿到一个路径，待优化），结合WebApplicationContext，这块应该很好写，读者可以试试！
        String path = componentScanAnnotation.value();
        path = path.replace(".","/");
//        拿到app的classloader，用来找到java文件对应的字节码文件。
        ClassLoader classLoader = componentScanAnnotation.getClass().getClassLoader();
        URL url = classLoader.getResource(path);
        assert url != null;
        File file = new File(url.getFile());
        resolvingFileToBeanClassList(file,beanClassList,classLoader);
        if(beanClassList.size() == 0){
            throw new ContextException("未找到能实例化的class");
        }
        return beanClassList;
    }

}
