package springMVC.HandlerTools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//该适配器主要用来处理继承XX接口实现controller，我们没有实现
public class HttpRequestHandlerAdapter implements HandlerAdapter{

    @Override
    public boolean supports(Class clazz) {
//        主要用来判断是否继承xxx接口，我们也没写，早就Deprecated了
        return false;
    }

    @Override
    public Object handle(HandlerExecutionChain mappedHandler, HttpServletRequest req, HttpServletResponse resp) {
        return null;
    }


}
