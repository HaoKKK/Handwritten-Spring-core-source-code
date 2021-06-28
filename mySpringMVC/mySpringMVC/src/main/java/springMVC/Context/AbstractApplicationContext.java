package springMVC.Context;

import springMVC.AOP.AnnotationAwareAspectJAutoProxyCreator;
import springMVC.AnnotationAndSpecialInterfaces.*;
import springMVC.BeanDefinition;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
    这里采用的抽象方法作用是：把两个具体实现类公用的方法进行重写，针对实现类各自的特点保留了一些抽象方法，因为我们这边只有
    两个具体实现类，所以整个继承接口关系简单，Spring源码中BeanFactory的继承关系稍微复杂一些，但具体思路是一致的。并不影响
    具体功能的实现。这里我们简单说说源码中需要重点关注的几个点：
    Spring源码中BeanFactory是所有工厂Bean的顶级父类，下面有三个最重要的子接口：ListableBeanFactory、HierarchicalBeanFactory、AutowireCapableBeanFactory
    分别让BeanFactory实现多Bean注册、可继承、可自动装配。ApplicationContext实现了前两个接口，而它握有一个DefaultListableBeanFactory实现所有接口。
 */
//注意我们具体实现代码过程中使用的一些变量名字和具体方法名字可能和源码不一样，因为我们本身就比源码简略一些，主要是理解具体的设计思想和实现方法。
public abstract class AbstractApplicationContext implements  ApplicationContext{
//    一个锁而已
    final Object startupShutdownMonitor = new Object();
    //    在源码中是一个ConfigurableListableBeanFactory，这里简单用map表示，用来存放所有的BeanDefinition
    Map<String, BeanDefinition> beanDefinitionFactory = new ConcurrentHashMap();
    //    单例工厂
     final Map<String,Object> singletonObjects = new ConcurrentHashMap<>();
    //    所有Bean的类，一是可以初始化时候挨个创建beanDefinition，二是自动配置@Autowire时候可以从里面找需要的类型，因为该注解是根据类型注入的
//    源码中实际上Autowire采用的是：封装属性到AutowiredFieldElement，注入到InjectMetaData，然后再注入一个AutowiredBeanPostProcessor中进行注入的，
//    有兴趣的同学可以看下Autowire的源码，如果需要的话，后续可以按源码写法写下，这里简单处理了下。
    List<Class> beanClassList;
    //    存放当前的BeanPostProcessor
    List<BeanPostProcessor> beanPostProcessorList = new LinkedList<>();
    //    用来解决循环依赖，不了解概念的可以百度下。
     final Map<String,Object> singletonFactories = new ConcurrentHashMap<>();
    //    用来记录@controller或者@RestController类，这两种类需要把它们额外保存在这里，主要是为了
//    对DispatchServlet进行注册，从而对request做出响应。这里我们只实现了@Controller，但是都是json输出，后面就知道了
    List<Object> webAnnotationHandlerObjects = new LinkedList<>();
    //    这里用来存放注解标记了@Aspect的切面类，希望他们被优先创造好，这里是为AOP做准备的，有被切方法的类会被动态代理成代理类
    List<Object> aspectsBeans = new LinkedList<>();
    @Override
    public void refresh() {
//       一个模板方法，完成IOC初始化
        synchronized (startupShutdownMonitor){
            prepareRefresh();
            beanDefinitionFactory = obtainFreshBeanFactory();
            prepareBeanFactory(beanDefinitionFactory);
//            这里省略了postProcessBeanFactory(beanFactory)和invokeBeanFactoryPostProcessors(beanFactory);
//            因为我们没有写这个接口，有兴趣的读者可以加上，不是很难。
            registerBeanPostProcessors(beanDefinitionFactory);
            onRefresh();
            finishBeanFactoryInitialization(beanDefinitionFactory);
            finishRefresh();
        }
    }

    @Override
    public Map<String, BeanDefinition> obtainFreshBeanFactory() {
//        创建新的BeanFactory,加载BeanDefinition
        refreshBeanFactory();
        return beanDefinitionFactory;
    }

    protected final void refreshBeanFactory(){
//        如果是基于xml文件的Bean，源码中应该是调用loadBeanDefinition(beanFactory)，
//        我们这边第一步先扫描需要我们扫描的文件夹，找到可能要注册的类
        beanClassList = scanAndLoadBeanClass();
//        根据要注册的beanClassList形成需要的的BeanDefinition
        creatFreshBeanFactory();

    }

    protected  void creatFreshBeanFactory(){
        for(Class beanClass:beanClassList){
//            new一个新的BeanDefinition
            BeanDefinition beanDefinition = new BeanDefinition();
//            首先我们对于该类还不知道它的注解具体是哪个，也不知道他到底有没有名字，所以我们要给他默认的名字：首字母小写
            String beanName = beanClass.getSimpleName().substring(0,1).toLowerCase() + beanClass.getSimpleName().substring(1);
//            调用refreshBeanName方法确定真正的名字
            beanName = refreshBeanName(beanClass,beanName);
//            默认Bean是单例模式，可以通过Scope注解进行修改
            String scope = "singleton";
//            如果是原型模式，这边从注解中读出来
            if(beanClass.isAnnotationPresent(Scope.class)){
                Scope scopeAnnotation = (Scope) beanClass.getAnnotation(Scope.class);
                scope = scopeAnnotation.value();
            }
//            默认不是懒加载
            boolean isLazy = false;
//            如果是懒加载，则设为true，这些都是BeanDefinition的主要组成部分
            if(beanClass.isAnnotationPresent(Lazy.class)){
                isLazy = true;
            }
//            该Bean是不是一个BeanPostProcessor
            boolean isPostProcessor = false;
            if(BeanPostProcessor.class.isAssignableFrom(beanClass)){
                isPostProcessor = true;
            }
//            注册BeanDefinition的各种属性，实际BeanDefinition组成要比我们这里复杂一些，
            beanDefinition.setPostProcessor(isPostProcessor);
            beanDefinition.setBeanClass(beanClass);
            beanDefinition.setLazy(isLazy);
            beanDefinition.setScope(scope);
//            将创建好的beanDefinition存放到beanDefinitionFactory
            beanDefinitionFactory.put(beanName,beanDefinition);
        }
    }

    protected String refreshBeanName(Class beanClass, String beanName){
//        注意下：因为我们写的Spring没有写ORM的逻辑，所以@Repository注解也没有，这个无所谓的，反正这几个注解底层都没得区别
//        根据不同注解拿到可能重写的BeanName
        if(beanClass.isAnnotationPresent(Component.class)){
            Component component = (Component) beanClass.getAnnotation(Component.class);
            if(!component.value().equals("")){beanName = component.value();}
        }else if(beanClass.isAnnotationPresent(Service.class)){
            Service service = (Service) beanClass.getAnnotation(Service.class);
            if(!service.value().equals("")){beanName = service.value();}
        }else if(beanClass.isAnnotationPresent(Controller.class)){
            Controller controller = (Controller) beanClass.getAnnotation(Controller.class);
            if(!controller.value().equals("")){beanName = controller.value();}
        }
        return beanName;
    }

//    这个扫描方法主要是因为实现类有基于配置文件确定扫描路径和基于注解确定扫描路径两种，所以留给他们自己实现
    protected abstract List<Class> scanAndLoadBeanClass();

    @Override
    public void prepareRefresh() {
//      一些springIOC注册前的准备工作，不是很重要：记录一下启动时间，标记一些状态，这里我们省略了。
        System.out.println("SpringIOC准备初始化");
    }


    @Override
    public void prepareBeanFactory(Map<String, BeanDefinition> beanDefinitionFactory) {
//设置 BeanFactory 的类加载器，添加几个 BeanPostProcessor，手动注册几个特殊的 bean,我们没啥特殊的Bean，这就pass了
    }

    @Override
    public void registerBeanPostProcessors(Map<String, BeanDefinition> beanDefinitionFactory) {
//        注册BeanPostProcessors，因为它们是在其他Bean注册前提前注册的。
        for(String beanName: beanDefinitionFactory.keySet()){
            BeanDefinition beanDefinition = beanDefinitionFactory.get(beanName);
            if(beanDefinition.isPostProcessor()){
                beanPostProcessorList.add((BeanPostProcessor) getBean(beanName));
            }
//            切面类我们也优先注册了，方便行动,如果某个类的注解有aspect，先创建了，并且放到aspectsBean容器里
//            这个主要是为了AOP做准备，真正的Spring源码也是先注册切面类，但切面类不是在这里注册的，有兴趣可以看下
            if(beanDefinition.getBeanClass().isAnnotationPresent(Aspect.class)){
                aspectsBeans.add(getBean(beanName));
            }
        }
//        将所有的BeanPostProcessor和切面类注册完毕后，我们创建需要的AopCreator
//        AnnotationAwareAspectJAutoProxyCreator是源码中的原名，这个容器它本质上就是一个BeanPostProcessor，
//        它的作用是在每个Bean创建的时候，如果切面类有切点覆盖到了该Bean的方法，那么该Bean实际返回的就是代理类而不是真实的Bean
        AnnotationAwareAspectJAutoProxyCreator Creator = new AnnotationAwareAspectJAutoProxyCreator(aspectsBeans);
//        Creator也添加进beanPostProcessorList
        beanPostProcessorList.add(Creator);

    }
    @Override
    public void onRefresh() {
//        钩子方法，留着扩展，典型的模板设计方法。
    }
    @Override
    public void finishBeanFactoryInitialization(Map<String, BeanDefinition> beanDefinitionFactory) {
//        对于每个beanDefinition我们要注册真正的Bean
        for (String beanName:beanDefinitionFactory.keySet()){
            BeanDefinition beanDefinition = beanDefinitionFactory.get(beanName);
//              当beanDefinition是单例模式并且它不是懒加载，我们就可以在初始化中调用getBean(beanName)
            if(beanDefinition.getScope().equals("singleton") && !beanDefinition.isLazy()){
                getBean(beanName);
            }
        }
    }
    @Override
    public void finishRefresh() {
//        我们没有设计广播事件，所以这里也没有逻辑。
        System.out.println("SpringIOC的初始化结束");
    }
    public Object getBean(String beanName){
//        最核心的方法来了，getBean用以通过beanName拿到真正的bean实例！
//        拿到对应的beanDefinition
        BeanDefinition beanDefinition = beanDefinitionFactory.get(beanName);
//        判断是不是原型模式，原型模式就不从bean工厂中拿了，直接给它个新的。
        if(beanDefinition.getScope().equals( "prototype")){
//            创建一个新的给他
            return doCreateBean(beanName, beanDefinition);
        }else {
//            从单例池，也就是beanFactory拿，拿不到（没有的话）在创建
//            这里采用两个单例池，一个是SingleObjects，一个是singletonFactories，主要是为了防止循环依赖
//            Spring源码中有一模一样的设计思想，不过采用的是三级缓存。
//            我们这边SingleObjects是创建成功Bean的单例池，singletonFactories是已经创建，还未属性注入的bean
//            Spring源码中多了一个正在创建的单例池，有兴趣可以看下
//            那么多级缓存是如何避免循坏依赖呢？简单说就是将正在创建或者还未注入属性的对象提前暴露，供其他创建中对象使用。
            if(singletonObjects.containsKey(beanName)){
                return singletonObjects.get(beanName);
            }else if(singletonFactories.containsKey(beanName)){
                return singletonFactories.get(beanName);
            }
            else {
                return doCreateBean(beanName,beanDefinition);
            }
        }
    }
    public Object doCreateBean(String beanName,BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getBeanClass();
        try {
//            1. 通过空参构造器实例化
            Object bean = clazz.getDeclaredConstructor().newInstance();
//            先放到初始化池中，防止循环依赖：
            singletonFactories.put(beanName,bean);
//            2. 依赖注入.注入属性，采用autowire注入
//            注意注意！源码中Autowire是通过封装到AutowiredBeanPostProcessor依赖注入的，
//            我们这里简化了这块的操作，直接在doCreateBean里就注入了，所以我们的@Autowire只能注入属性
//            实际Spring源码要复杂一些
            Field[] fields = clazz.getDeclaredFields();
            for(Field field:fields){
                if(field.isAnnotationPresent(Autowired.class)){
                    field.setAccessible(true);
//                    注意防止循环注入,注意Autowired应该按类型注入而不是名字注入
                    Class<?> type = field.getType();
//                    根据需要的类型从beanClassList中找到对应的类，从而通过反射找到对应的beanName；
                    String name = getMatchedClassBeanName(type);
                    /**
                     * 这里我们说个问题，虽然我们这里默认就是byType进行装配，byName逻辑按道理更好写，直接拿到属性名字去beanFactory找就行了
                     * 但是jvm在生成字节码文件的时候，并不会保留变量的名字，而是采用默认的arg0、arg1...我们这里在MAVEN配置了          <compilerArgs>
                     *             <arg>-parameters</arg>
                     *           </compilerArgs>
                     * 但Spring源码底层并没有做这个配置，采用了其他更巧妙的方法，这里有兴趣的同学可以去看看源码，不展开阐述了。
                     * */
//                    通过beanName注入属性
                    field.set(bean,getBean(name));
                }
//                Value在源码中和Autowire中挺像的，有兴趣可以看下，这里就先简单处理了
                else if(field.isAnnotationPresent(Value.class)){
                    String filedName = field.getAnnotation(Value.class).value();
                    field.setAccessible(true);
                    field.set(bean,filedName);
                }
            }
//            3. 实现Aware接口的bean需要在此发挥作用了
//            Spring中常用的Aware接口有：
//            BeanNameAware：能够获取bean的名称，即是id
//            BeanFactoryAware：获取BeanFactory实例
//            ApplicationContextAware：获取ApplicationContext
//            MessageSourceAware：获取MessageSource
//            这里我们只做了BeanNameAware做演示，实际上逻辑都是差不多的，读者可以关注下这里的逻辑顺序：
//            我们是：创建Bean——属性注入——aware接口调用——postProcessor的before方法——初始化方法——postProcessor的after方法
//            这是Bean生命周期中非常重要的顺序，也是面试常考点（我们没包括destroy）
            if(bean instanceof BeanNameAware){
                ((BeanNameAware) bean).awareBeanName(beanName);
            }
//            5. 初始化之前XXXXXXXXXXXXX
            if(!beanDefinition.isPostProcessor()){
                for(BeanPostProcessor beanPostProcessor: beanPostProcessorList){
                    bean = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
                }
            }
//            4. 初始化操作（这边主要是基于注解的初始化操作）
            if(bean instanceof InitializingBean){
                ((InitializingBean) bean).afterPropertiesSet();
            }
//            5. 初始化之后XXXXXXXXXXXXX
            if(!beanDefinition.isPostProcessor()){
                for(BeanPostProcessor beanPostProcessor: beanPostProcessorList){
                    bean = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
                }
            }

//            删掉初始化池中的bean
            singletonFactories.remove(beanName);
//            将创建好的bean放到单例池中
            singletonObjects.put(beanName,bean);
//            如果它是controller注解的，我们要存起来给MVC框架使用
            if(bean.getClass().isAnnotationPresent(Controller.class)){
                webAnnotationHandlerObjects.add(bean);
            }
            return bean;
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getMatchedClassBeanName(Class<?> type) {
        String name = "";
//           查找合适的类型，找到对应的BeanName
        Class curClass;
        for(Map.Entry<String,BeanDefinition> beanEntry:beanDefinitionFactory.entrySet()){
            curClass = beanEntry.getValue().getBeanClass();
//            注意这里的逻辑，为了实现autowire的多态性，我们在自动注入的时候，不仅要判别两个类相不相同，
//            如果当前类是需要类（接口）的子类，我们也应该自动注入，这样Service就可以被ServiceImpl自动注入
//            这里也可以看出来，我们的Autowire相比于源码确实比较简单，如果有多个类都满足type.isAssignableFrom(curClass)该怎么办呢？
//            Autowire本身也可以byName匹配，这也是源码中比较复杂的原因。
            if(curClass.equals(type) || type.isAssignableFrom(curClass)){
                name = beanEntry.getKey();
                break;
            }
        }
        return name;
    }
    //这个方法是把java文件读出来，找到对应的字节码文件，加载类，保存类信息到beanClassList
    public void resolvingFileToBeanClassList(File file, List<Class> beanClassList, ClassLoader classLoader) {
//        这里需要指出一个问题，我们指定给Spring的路径一定是java工程文件下的，java文件是不能被类加载器直接加载的，一定是编译成的字节码文件
//        所以我们要从java文件找到字节码文件(.class)文件，再用类加载器加载,这里进来的file就是字节码文件的位置，而不是Java文件的位置
//        如果file是个文件夹，那么就递归调用
        if(file.isDirectory()){
            File[] files = file.listFiles();
//            对文件内的每个文件递归调用
            for(File f: files){
                resolvingFileToBeanClassList(f,beanClassList,classLoader);
            }
        }else {
//            获取字节码文件的全路径文件
            String absolutePath = file.getAbsolutePath();
//            全路径太长了，我们截取classes后面的就行
            absolutePath = absolutePath.substring(absolutePath.indexOf("classes"), absolutePath.indexOf(".class")).substring(8);
//            把.分隔符换成\\分隔符
            absolutePath = absolutePath.replace("\\", ".");
//                通过类加载器加载需要的bean的类
            Class<?> clazz = null;
            try {
//                这也可以用class.forname(全类名)来实现，就不用手动调用classLoader了
//                还记得我们初始化时候可以选择注册BeanFactory的类加载器吗？Spring可以支持使用自定义的类加载器进行加载
                clazz = classLoader.loadClass(absolutePath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            assert clazz != null;
//            加载的类结束后，我们可以拿到类上面的注释，如果没有Bean的注释，我们就不要把它放进beanClassList里面了
            if(!clazz.isAnnotationPresent(Component.class) &&
                    !clazz.isAnnotationPresent(Service.class)&&
                    !clazz.isAnnotationPresent(Controller.class)
            ){return;}
            beanClassList.add(clazz);
        }
    }
}
