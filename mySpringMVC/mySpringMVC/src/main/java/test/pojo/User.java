package test.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springMVC.AnnotationAndSpecialInterfaces.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component("testAopUser")
public class User {
    private String id;
    private String name;
    private String password;
    public  int mainMethod(int a){
        System.out.println("被代理方法执行了");
        return a;
    }
}
