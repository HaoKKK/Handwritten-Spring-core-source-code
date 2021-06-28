package springMVC.HandlerTools;


import springMVC.AnnotationAndSpecialInterfaces.*;
import springMVC.exception.ContextException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//最核心的adapter，主要就是调用对应的handler去执行request需求，都是基于注解的
public class AnnotationMethodHandlerAdapter implements HandlerAdapter{
//    适配器模式：判断是否适配
    public boolean supports(Class clazz){
        Method[] declaredMethods = clazz.getDeclaredMethods();
//        在实际的MVC中是这么判断的，因为它有很多adapters，到这里是最后一个，默认是基于注解的
//        我们在这里首先可以肯定handler.getControllerClass一定是由@Controller注解的
//        所以本来是可以直接返回true的，这一块只是根据SpringMVC的设计思想加入的，
//        我们不妨在这里判断下是否有@RequesMapping注解的方法
//        if (declaredMethods.length == 0){return false;}
//        return true;
        for(Method method:declaredMethods){
            method.setAccessible(true);
            if(method.isAnnotationPresent(RequestMapping.class)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Object handle(HandlerExecutionChain mappedHandler, HttpServletRequest req, HttpServletResponse resp) {
        //这里一定要在MEAVEN配置之后才能通过getName拿到方法的参数名。默认是拿不到参数名字的
        Parameter[] parameters = mappedHandler.getControllerMethod().getParameters();
//        获得req请求带的参数，注意Value默认是个数组
        Map<String, String[]> parameterMap = req.getParameterMap();
//        接下里就是针对req中每个参数，给方法中的参数输入数值,这里遍历的时候从方法参数遍历，根据它是否有注解、什么样的
//        注解来决定它参数注入的方式。
//        注意这里有个小技巧，无论是@RequestMapping/@PathVariable/@CookieValue/RequestHeader,它们的类型其实
//        都是String，也就是说，除了String/HttpServletRequest/HttpServletResponse类型，其他类型都是pojo，
//        pojo需要按照变量类型和名字自动封装成对象
//        但是这里我们给了限制，如果你传了pojo，就不要再传其他信息了。
        if(parameters.length == 1 && !parameters[0].getType().equals(String.class) && !parameters[0].getType().equals(HttpServletRequest.class) &&
                !parameters[0].getType().equals(HttpServletResponse.class)){
//            说明是pojo
            Class<?> pojoType = parameters[0].getType();
            Object pojo = null;
            try {
//                创建出需要的pojo
                pojo = pojoType.getConstructor().newInstance();
//                找到pojo所有的属性准备装配,这里说明下，做的还是一层的pojo装配，没有写多层装配的逻辑，以后可以补充：
//                单层装配的话要求输入的pojo所有属性都是String且没有其他，直接装配即可
                Field[] pojoFields = pojoType.getDeclaredFields();
                for(Map.Entry<String,String[]> entry:parameterMap.entrySet()){
                    for(Field pojoField: pojoFields){
                        if(pojoField.getName().equals(entry.getKey())){
                            pojoField.setAccessible(true);
                            pojoField.set(pojo,entry.getValue()[0]);
                        }
                    }
                }

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
//            try {
//                assert pojo != null;
//                resp.getWriter().println(pojo.toString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return pojo;
        }
//        下面是正常参数，没有pojo
        Object[] args = new Object[parameters.length];
        int index = 0;
        for(Parameter parameter:parameters){
//            如果参数有注解@RequestParam说明要从请求中取参数
            if(parameter.isAnnotationPresent(RequestParam.class)){
                String reqParamName = "";
//                通过RequestParam的value获得我们想拿的参数的key
                if(!parameter.getAnnotation(RequestParam.class).value().equals("")){
                    reqParamName = parameter.getAnnotation(RequestParam.class).value();
                }
                //获得到该请求参数的数值，这里是个数组，我们这边统一简化下，都是STRING
                if(parameterMap.containsKey(reqParamName)){
                    String[] reqValues = parameterMap.get(reqParamName);
                    args[index++] = reqValues[0];
                }else {
//                    拿不到就默认空，注意我们没有写默认reqParamName = parameter.getName()这个逻辑
//                    所以这个注解不能默认空参数
                    args[index++] = "";
                }
//                这个注解说明参数要从URL的访问路径中拿
            }else if(parameter.isAnnotationPresent(PathVariable.class)){
//                1.拿到RequestMapping上面的请求地址
                String rowURL = ( mappedHandler.getControllerMethod().getAnnotation(RequestMapping.class)).value();
//                2. 需要拿到的的参数名称
                String needVariable = parameter.getAnnotation(PathVariable.class).value();
//                3. 拿到request真正访问的地址
                String URL = req.getRequestURI();
//                4. 根据请求地址和真正访问的地址建立参数映射，囊括所有能通过PathVariable取出的参数
//                只要是大括号包括的都是可以被取出来的，rowUrl是写在@RequestMapping的，URL是访问的真正URL
                Map<String,String> accessibleStr = SearchAccessibleStr(rowURL,URL);
                String value = "";
                if(!accessibleStr.containsKey(needVariable)){
                    throw  new ContextException("寻求参数异常");
                }
//                取出对应的value
                value = accessibleStr.get(needVariable);
                args[index++] = value;
//                如果有@CookieValue注解，我们就去cookies拿cookie
            }else if(parameter.isAnnotationPresent(CookieValue.class)){
                Cookie[] cookies = req.getCookies();
                String needCookie = parameter.getAnnotation(CookieValue.class).value();
                String cookieVal = "";
//                遍历拿需要而cookie即可，无需多言
                for(Cookie cookie:cookies){
                    if(cookie.getName().equals(needCookie)){
                        cookieVal = cookie.getValue();
                        break;
                    }
                }
                args[index++] = cookieVal;
//                如果是@RequestHeader注解，我们就去拿请求头中对应的信息。
            }else if(parameter.isAnnotationPresent(RequestHeader.class)){
                String needHeader = parameter.getAnnotation(RequestHeader.class).value();
                String headerValue = req.getHeader(needHeader);
                args[index++] = headerValue;
//                需要拿到request
            }else if(parameter.getType().equals(HttpServletRequest.class)){
                args[index++] = req;
//                需要拿到response
            }else if(parameter.getType().equals(HttpServletResponse.class)){
                args[index++] = resp;
            }else {
//                啥注解都没有还往里传，估计是根据名称匹配，类似于@RequestParam的默认版
                String paraName = parameter.getName();
                if(parameterMap.containsKey(paraName)){
                    String[] reqValues = parameterMap.get(paraName);
                    args[index] = reqValues[0];
                }else {
                    args[index] = "";
                }
//                这里把index++放出来是因为不管能否匹配到，这个参数到此为止，匹配不到就跳过去了
                index++;
            }
        }
        /*
        这里我们没有考虑传入的是model或者modelAndView之类的，因为我们没有写视图解析器，也不往前端给大家带了
        主要就是理解SpringMVC的web核心逻辑和实现方式，不可能和源码完全一致，那样也就失去手写的意义了
         */
//        到此为止，所有参数都给他准备好了。拿到要执行requestMapping的方法
        Method method = mappedHandler.getControllerMethod();
        method.setAccessible(true);
//        这里得到的就是方法的返回值，注意我们这里都是RestController风格，所以其实返回的就是个字符串
        String res = null;
        try {
            res = (String) method.invoke(mappedHandler.getExecutionHandler(), args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
//            这里我们就没做视图解析器模块了，后期直接当JSON数据条输出算了
        return res;
    }
//  注意，在这里我们将拿出来访问地址中我们需要的变量，这里的地址不能携带**，怕没有办法匹配
    private Map<String, String> SearchAccessibleStr(String rowURL, String url) {
//        注意这里一层一层是对应的，一旦中间有了**就乱套了
        String[] rowUrlList = rowURL.split("/");
        String[] urlList = url.split("/");
//        记录最终结果
        Map<String,String> res = new ConcurrentHashMap<>();
//        逐层遍历查找{}匹配
        if (rowUrlList.length != urlList.length){return res;}
        for(int i =0;i<urlList.length;i++){
            if(rowUrlList[i].length() >=2 && rowUrlList[i].charAt(0) == '{' && rowUrlList[i].charAt(rowUrlList[i].length()-1) == '}'){
                res.put(rowUrlList[i].substring(1,rowUrlList[i].length()-1),urlList[i]);
            }
        }
        return res;
    }


}
