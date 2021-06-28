package springMVC;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import springMVC.AnnotationAndSpecialInterfaces.RequestMapping;
import springMVC.Context.WebApplicationContext;
import springMVC.HandlerTools.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 从这里开始我们进入SpringMVC的过程，当然MVC设计的东西非常多，尤其是ModelAndView涉及到很多前端的东西，我们全都会避免
 * 在这里主要练习通过我们自己的Spring框架完成Web访问，以json数据为主。
 */
public class DispatcherServlet extends HttpServlet {
    //  只是SpringMVC框架中用来处理请求映射的，它会记录所有控制器信息
//    HandlerMappings用来记录各种HandlerMapping
    List<HandlerMapping> handlerMappings = new LinkedList<>();
//    记录适配器，典型的适配器模式
    List<HandlerAdapter> handlerAdapters = new LinkedList<>();
    @SneakyThrows
    @Override
//    init方法在Servlet初始化执行
    public void init()  {
//        1. Servlet初始化时候执行的方法init，我们通过它读取初始化参数，也就是我们的xml文件
        String contextConfigLocation = this.getServletConfig().getInitParameter("contextConfigLocation");
//        这里可以加一下,如果给的配置时xml文件,我们就解析xml文件得到扫描路径
//        如果给的是一个配置类,带有@ComponentScan("xxx")，我们就解析类得到要扫描的路径，
        /**
         * 这里可以改，加入MyAnnotationContextApplication,配置既可以是xml，也可以是配置类
         */
//        2.  准备初始化ioc容器，主要就是把HandlerMappings 和 HandlerAdapters初始化好
        prepareRefresh();
//        3. 创建通过xml文件写的webBean容器,myAnnotationContextApplication也可以.
        WebApplicationContext webApplicationContext = new WebApplicationContext(contextConfigLocation);
//        这里用来保存所有注解为@Controller的对象
        List<Object> annotationHandlerObjects;
//        获得refresh之后的annotationHandlerObjects，里面包含了所有的控制器对象。
        annotationHandlerObjects = webApplicationContext.getWebAnnotationHandlerClasses();
//        我们没有根据配置文件的Bean创建的，都是基于注解的.所以这里是空的，永远不会有对象. Like me >_<
        List<Object> beanHandlerObjects = new LinkedList<>();
        registerHandlerMappings(annotationHandlerObjects,beanHandlerObjects);
//        4. 初始化请求映射 ——> 对应的Controller ——> method，另外解决para参数以及path参数等
    }
//      注册HandlerMappings，主要是将对应的
    private void registerHandlerMappings(List<Object> annotationHandlerObjects, List<Object> beanHandlerObjects) {
//        第一部分先注册defaultAnnotationHandlerMapping，就是将annotationHandlerClasses中的类取出来，找出它
//        的方法中标有@RequestMapping的部分，找出里面的url，然后根据类创建它对应的handler，以<URL,handler>的格式放入
//        defaultAnnotationHandlerMapping中的handlerMap中
        DefaultAnnotationHandlerMapping defaultAnnotationHandlerMapping = (DefaultAnnotationHandlerMapping) handlerMappings.get(1);
//          找到该HandlerMapping中的handlerMap
        Map<String, HandlerExecutionChain> handlerMap1 = defaultAnnotationHandlerMapping.getHandler();
        doRegisterHandlerMapping(annotationHandlerObjects, defaultAnnotationHandlerMapping, handlerMap1);
//         同样的做法，不过肯定是空的
        BeanNameUrlHandlerMapping beanNameUrlHandlerMapping = (BeanNameUrlHandlerMapping) handlerMappings.get(0);
        Map<String, HandlerExecutionChain> handlerMap2 = defaultAnnotationHandlerMapping.getHandler();
        doRegisterHandlerMapping(beanHandlerObjects,beanNameUrlHandlerMapping,handlerMap2);
    }

//      这个就是先把类信息注册到handlerMap中，再把这个map注册到handlerMapping中
    private void doRegisterHandlerMapping(List<Object> handlerObjects, HandlerMapping handlerMapping, Map<String, HandlerExecutionChain> handlerMap) {
        for(Object object : handlerObjects){
            Class clazz = object.getClass();
//            遍历所有控制类，寻找它们处理响应的方法，并包装成HandlerExecutionChain，放入map中。
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for(Method declaredMethod:declaredMethods){
                declaredMethod.setAccessible(true);
                HandlerExecutionChain handler = new HandlerExecutionChain();
                if(declaredMethod.isAnnotationPresent(RequestMapping.class)){
//                    这里得到的URL可能有很多匹配表达式，当真正的需求来临，我们如何匹配呢，读者可以先考虑下？
                    String rowUrl = declaredMethod.getAnnotation(RequestMapping.class).value();
//                    这里既然遍历到这里了，我们也把需要的方法保存一下吧
                    handler.setControllerClass(clazz);
                    handler.setControllerMethod(declaredMethod);
                    handler.setExecutionHandler(object);
                    handlerMap.put(rowUrl,handler);
                }
            }
        }
//        将handlerMap放回到handlerMapping中
        handlerMapping.setHandler(handlerMap);
    }


    private void prepareRefresh() {
//        第一个我们没有写根据bean配置文件的ioc注册，只是为了撑起MVC的架构，
//        构造多个Mappings，实际第一个一直是空的。
//        注意这里我们没写加入自定义Mapping和Adapter的逻辑，默认阉割掉了
        BeanNameUrlHandlerMapping beanNameUrlHandlerMapping = new BeanNameUrlHandlerMapping();
        DefaultAnnotationHandlerMapping defaultAnnotationHandlerMapping = new DefaultAnnotationHandlerMapping();
        handlerMappings.add(beanNameUrlHandlerMapping);
        handlerMappings.add(defaultAnnotationHandlerMapping);
//      注册Adapters前两个其实已经deprecated了,主要用的就是第三个
        handlerAdapters.add(new HttpRequestHandlerAdapter());
        handlerAdapters.add(new SimpleControllerHandlerAdapter());
        handlerAdapters.add(new AnnotationMethodHandlerAdapter());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        进行请求的分发和处理,MVC中最核心的方法
        doDispatch(req,resp);
    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        找到对应的Handler,这里的handler只有类信息和方法信息，由于ContextApplication那里没有帮
//        我们初始化handler具体的对象信息
        HandlerExecutionChain mappedHandler = getHandler(req);
//        找不到说明地址写错了，这边简单输出个404
        Object handleRes = null;
        if(mappedHandler == null){
            try {
                resp.getWriter().println("<h1> 404 NOT FOUND</h1>");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            //        拿到能处理对应类的适配器
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getControllerClass());
            if(ha != null) {
                handleRes = ha.handle(mappedHandler, req, resp);
            }
        }
//      这里我们统一成RestController的输出，直接将返回对象转换成json字符串
        String resJson = JSON.toJSONString(handleRes);
//        我们没有视图解析器，这边直接打印出来了，也可以选择打印到网页:
//        PrintWriter writer = resp.getWriter();
//        writer.write(resJson);
        System.out.println("打印返回值漏");
        System.out.println(resJson);
    }

    private HandlerAdapter getHandlerAdapter(Class<?> controllerClass) {
//        获取适配器,逐一遍历,找到支持controllerClass(源码中是支持handler,思想是一样的)
        for(HandlerAdapter ha: handlerAdapters){
            if(ha.supports(controllerClass)){return ha;}
        }
        return null;
    }
//      找到合适的执行链
//    这里说一下为啥是责任链模式,因为Spring中的Filter的存在可以拦截request请求,所以这里采用了责任链
//    虽然我们这里没有写filter(sorry),但是责任链的思想和构造在AOP那里体现的淋漓尽致,它们本质上一样的,我们保留了
//    AOP的部分,大家可以从AOP部分看到Spring使用责任链模式实现动态代理或者filter的巧妙
    public HandlerExecutionChain getHandler(HttpServletRequest req){
        for(HandlerMapping hm : handlerMappings){
//            寻找合适的handler
            HandlerExecutionChain handler = hm.getHandler(req);
            if(handler != null){
                return handler;
            }
        }
        return null;
    }
}
