package springMVC.HandlerTools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//适配器的接口，主要用来实现判断该适配器能否处理该HandlerExecutionChain（其实就是controller类）
public interface HandlerAdapter {
    boolean supports(Class handler);

    Object handle(HandlerExecutionChain mappedHandler, HttpServletRequest req, HttpServletResponse resp);
}
