# 手摸手教你写Spring框架！
你是否是刚学习SpringMVC框架的初学者，或者想要学习其中更多的源码知识应对面试？

你是否每次鼓起勇气点开Spring源码，总被其中一个套一个的方法和接口绕晕？

你是否看了很多大牛的Spring源码讲解博客和视频，仍然觉得源码是空中楼阁，非常抽象？

你是否只能靠硬背去勉强记忆Spring源码设计的一些核心，但总是无法吸收。Bean生命周期、IOC初始化、AOP原理等等？背了又忘...忘了又背

当你对着源码无从下手时，不妨自己写一个！
本项目受众为希望快速理解（不只是记忆）和掌握Spring核心设计思想和设计模式的初学者，或虽然会用但面对源码完全无从下眼的CRUD从业者。
本项目将在保留Spring原有设计思想的基础上，以一种更为简化和容易理解的方式写出Spring的核心功能，帮助新手快速理解Spring源码的实现，用代码说话。
本项目将详细的讲解每一行代码的用途、设计思想、在源码中的体现，甚至相比源码简化的点也会详细阐述，方便读者理解，也方便读者大大后续自己阅读源码。
本项目希望能提高读者大大阅读源码的能力，修炼内功，而不止Spring！
# 项目内容
本项目主要包括Spring核心的IOC和AOP，基本的MVC，不包括视图解析，ORM层目前也没有，因为Spring自带的ORM已经很少使用，主要还是Mybatis，如果读者大大觉得需要，可以补充。Spring的事务管理也将会在后续有时间的情况下加以补充。
1. IOC初始化的全流程，以及如何根据@ComponentScan或者配置文件进行包扫描，如何读取配置文件内容，如何找到要扫描的目录；包括@Controller、@Service、@Component（@repository省略了，但核心是一样的）逻辑实现，Bean的注册全过程：BeanDefinition的注册、Aware接口、后置处理器，Bean的实例化等等以及它们的执行顺序、简化版的@Autowire如何自动注入属性，如何解决循坏依赖等等。
2. AOP面向切面编程，如何将责任链模式有效的利用到AOP的编程中，如何构建AdvisorCreator、如何在Creator中注册各种切面类，如何根据切面类将其中的各种切面方法封装成Advice，如何找到每个Advice的准确切点位置，如何将符合条件的通知封装成责任链，如何实现对要被切对象的识别，动态代理，如何根据JoinPoint拿到运行过程中各种信息，如何对各种Advice在责任链中进行排序，责任链在AOP执行中不同通知各自是如何执行的。如何将AOP的整个流程和IO融合在一起。
3. @Controller和@RequestMapping进行简单的web测试。包括DispatchServlet的实现、如何拦截请求，如何分配请求，找到要执行的位置，如何匹配URL地址，尤其是带正则表达的匹配逻辑。还有如何实现就近匹配原则，如何根据各种注解：@RequestParam、@PathVarible拿到web请求过程中的各种信息，如何拿到请求的req和resp，如何根据请求的参数直接对pojo进行封装等等。
# 运行要求
该项目最好在IDEA 2020.2以上环境运行
JDK8以上
需要的其他包和版本都在MAVEN中
包括：lombok、fastjon、cglib、junit、dom4j等，具体版本见MAVEN
# 运行步骤
本项目主要用于学习，只要连接上TOMCAT，直接运行即可，可以打着断点根据注释进行一步一步学习
# 测试
本项目测试都在java包下的test里，注意是main下的java包下的test！！！，读者大大想测试可以写在除mySpringMVC的任何包下，只要配置好正确的扫描路径即可完成测试
# 致谢和参考
Spring源码
博文：Spring IOC 容器源码分析：https://javadoop.com/post/spring-ioc
博文：Spring AOP 源码解析：https://javadoop.com/post/spring-aop-source

本菜鸡的第一个小demo，有啥写的不好的地方欢迎联系或者直接push上来代码。
