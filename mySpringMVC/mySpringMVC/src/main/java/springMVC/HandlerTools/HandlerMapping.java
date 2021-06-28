package springMVC.HandlerTools;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
/*
    HandlerMapping最核心的就是它的一个Map<String,HandlerExecutionChain>，这里保存了

*/
public interface HandlerMapping {
    Map<String,HandlerExecutionChain> getHandler();
    HandlerExecutionChain getHandler(HttpServletRequest request);
    void setHandler(Map<String,HandlerExecutionChain> handlerMap);
}
