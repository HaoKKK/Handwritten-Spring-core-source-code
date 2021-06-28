package springMVC.Context;

import lombok.Data;
import springMVC.XML.XmlParser;
import springMVC.exception.ContextException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;


//  WebApplicationContext使我们写的两个具体的ApplicationContext实现类之一，其主要使用过SpringMVC.xml配置文件来读取我们要扫描的类的位置
//  主要练习我们对配置文件读取的代码能力，这个也是我们SpringMVC默认的容器。
@Data
public class WebApplicationContext extends AbstractApplicationContext{
//    配置文件的具体位置，我们要知道，它的初始路径是resources
    private String contextConfigLocation;
//    在初始化时候，对于@Controller这种特殊的部件（MVC要把它包装成Handler），我们就保存下来
    public List<Object> getWebAnnotationHandlerClasses() {
        return webAnnotationHandlerObjects;
    }
    public WebApplicationContext(String contextConfigLocation) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        this.contextConfigLocation = contextConfigLocation;
//        调用初始化方法refresh()
        refresh();
    }
//    两个实现类最大的区别就是获取要扫描类位置的方法，所以这里各自重写了scanAndLoadBeanClass
    @Override
    protected List<Class> scanAndLoadBeanClass() {
        List<Class> beanClassList = new LinkedList<>();
//        这里采用dom4j的XmlParse工具对配置文件进行解析，由于传参格式是Classpath：XXXXX,所以这里先把ClassPath去掉
        String basePackage = XmlParser.getBasePackage(contextConfigLocation.split(":")[1]);
//        支持通过逗号分隔传入多个扫描包参数，这里把各个包分开
        String[] basePackages = basePackage.split(",");
//        拿到默认的应用类加载器
        ClassLoader appClassLoader = WebApplicationContext.class.getClassLoader();
//            这个path是java文件的位置，我们需要它找到字节码文件的位置，并解析得到类信息
        for(String path:basePackages){
//            替换分隔符
            path = path.replace(".", "/");
//            拿到java文件路径对应的字节码文件
            URL url = appClassLoader.getResource(path);
            assert url != null;
//                找到字节码文件的位置
            File file = new File(url.getFile());
//                这里file有可能是个包，有可能是文件，采用递归方法对其进行解析
            resolvingFileToBeanClassList(file,beanClassList,appClassLoader);
        }
        if(beanClassList.size() == 0){
            throw new ContextException("未找到能实例化的class");
        }
        return beanClassList;
    }

}
