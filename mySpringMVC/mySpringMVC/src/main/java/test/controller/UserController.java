package test.controller;

import test.pojo.User;
import springMVC.AnnotationAndSpecialInterfaces.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import test.service.*;

//@Controller("userController")
public class UserController {
//    持有业务逻辑层的对象实现
    @Autowired
    UserService userService;

    @RequestMapping("/{user}/query")
    public String findUsers(HttpServletRequest request, HttpServletResponse response,
//                            名字相同的话，把这行注解去掉也能拿到param
                            @RequestParam(value = "paraName")
                            String paraName,
                            @PathVariable(value = "user")
                            String variable,
                            String test)  {
        List<User> users = userService.findUsers(paraName);
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert out != null;
//        打印一下，免得页面太空
        out.println("<h1>MVC can get paraName</h1>" + paraName);
        out.println("<h1>MVC can get PathVariable</h1>" + variable);
        out.println("<h1>MVC can get test</h1>" + test);
        out.println("<h1>MVC can get request</h1>" + request);
        out.println("<h1>MVC can get response</h1>" + response);
//        随便一返回，没啥意义
        return "s";
    }
//传入pojo
    @RequestMapping("/{user}/pojo")
    public Object findUser(User user)  {
        return user;
    }

}
