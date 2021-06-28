package springMVC.HandlerTools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//我们也没有实现，已经被淘汰
public class SimpleControllerHandlerAdapter implements HandlerAdapter{

    @Override
    public boolean supports(Class clazz) {
//        Deprecated,主要判断是否实现Controller接口，我们没写
        return false;
    }

    @Override
    public Object handle(HandlerExecutionChain mappedHandler, HttpServletRequest req, HttpServletResponse resp) {
        return null;
    }
}
