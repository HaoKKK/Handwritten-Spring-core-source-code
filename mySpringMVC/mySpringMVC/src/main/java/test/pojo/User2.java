package test.pojo;


import springMVC.AnnotationAndSpecialInterfaces.Autowired;
import springMVC.AnnotationAndSpecialInterfaces.Component;
import springMVC.AnnotationAndSpecialInterfaces.InitializingBean;
import test.service.*;
@Component("user")
public class User2 implements InitializingBean {
    @Autowired
    UserService2 userService2;
    String userName;

    public UserService2 getUserService() {
        return userService2;
    }

    public void setUserService(UserService2 userService2) {
        this.userService2 = userService2;
    }

    @Override
    public void afterPropertiesSet() {
        userName = "xxx";
    }
}
