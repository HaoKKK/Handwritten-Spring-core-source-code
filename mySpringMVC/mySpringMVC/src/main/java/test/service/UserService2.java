package test.service;

import test.pojo.User2;
import springMVC.AnnotationAndSpecialInterfaces.*;

@Component("userservice")
@Scope("singleton")
public class UserService2 implements BeanNameAware, InitializingBean {
    @Autowired
    private User2 user2;
    @Value("myService")
    private String serviceName;

    private String beanName;
    public void test() {
        System.out.println(user2);
    }

    public User2 getUser() {
        return user2;
    }

    public void setUser(User2 user2) {
        this.user2 = user2;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void awareBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {



    }
}
