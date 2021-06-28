package springMVC.HandlerTools;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AbstractHandlerMapping是继承了HandlerMapping接口的抽象类
 */
public abstract class AbstractHandlerMapping implements HandlerMapping{
//    其中最核心的部分就是这个Map，保存了url和它对应的Handler执行链，注意这个url是rowUrl哦，所以匹配是个大问题
    Map<String,HandlerExecutionChain> handlerMap = new ConcurrentHashMap<>();
    @Override
    public Map<String, HandlerExecutionChain> getHandler() {
        return handlerMap;
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) {
//        这里需要实现具体的查询逻辑，主要难点有两个：一个是如何匹配需要的URL和访问的URL，第二个是有多个
//        匹配目标时，如何满足最近匹配原则，找到最合适的那个
        String requestURL = request.getRequestURI();

        HandlerExecutionChain tarHandler = searchMatchedURLAddress(requestURL);
        if(tarHandler != null){
            return tarHandler;
        }
        return null;
    }
//  匹配算法，可以参考力扣上关于正则匹配类似的题，都是hard题
    protected abstract HandlerExecutionChain searchMatchedURLAddress(String requestURL);

//  set方法不多说了
    @Override
    public void setHandler(Map<String, HandlerExecutionChain> handlerMap) {
        this.handlerMap = handlerMap;
    }
}
