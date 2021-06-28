package test.service.impy;


import test.pojo.User;
import springMVC.AnnotationAndSpecialInterfaces.Service;
import test.service.UserService;
import java.util.LinkedList;
import java.util.List;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    public List<User> findUsers(String name) {
        System.out.println("输入的参数是" + name);
        List<User> users = new LinkedList<User>();
        users.add(new User("1","老王","123"));
        users.add(new User("2","大王","3333"));
        return users;
    }
}
