package springMVC.Context;

import springMVC.BeanDefinition;

import java.util.List;
import java.util.Map;

public interface ApplicationContext {
//    这里采用Spring源码ApplicationContext的设计和架构，将IOC的创建分成以下七个步骤
//    （refresh是其他七大部分的入口）：

//    0. 初始化的入口，放在BeanFactory的构造方法中，标志着IOC容器的初始化开始。
    void refresh();
//    1. IOC容器的准备工作，包括记录容器的启动时间，标记状态，处理一些配置文件，
//    第一由于这块不是很重要，第二我们最终的两个实现类都是基于注解的Bean注册，所以
//    配置文件也不复杂，这块我们基本就省略了。
    void prepareRefresh();
//    2. 这个方法是重点，主要就是BeanDefinition的创建过程，如果是基于XML配置的XmlApplicationContext，会通过
//    XmlBeanDefinitionReader去读取xml配置文件，封装成BeanDefinition实例，这边是基于注解的Bean注入，所以采用的方法是
//    先拿到所有要注入Bean的BeanCLass，根据每个class的注解、属性、方法等完成BeanDefinition的注册
    Map<String, BeanDefinition> obtainFreshBeanFactory();
//    3. 源码中的功能是设置BeanFactory的类加载器，添加几个特殊的BeanPostProcessor，注册几个特殊的Bean
//    这里对我们的理解作用也没那么大，就是注意下这里面要注册一个 ApplicationContextAwareProcessor，负责
//    完成当Bean实现Aware接口后的回调功能，我们这里简化了Aware接口的操作，同时也没有啥特殊Bean，所以这里也没东西。
    void prepareBeanFactory(Map<String, BeanDefinition> beanDefinitionFactory );
//    4.这里有两个源码中有我们这边没写的方法（因为根本没用到），一个是postProcessBeanFactory(beanFactory)，还有一个是
//    invokeBeanFactoryPostProcessors(beanFactory)，分别用来添加BeanFactoryPostProcessor和唤醒调用
//    各实现类的postProcessBeanFactory(factory) 回调方法，这个BeanFactoryPostProcessor有点和后面的BeanPostProcessor
//    类似，但是一个是在BeanFactory创建的之后，一个是在Bean的初始化前后，都是Spring提供的很好的扩展点
//    5. 注册所有的BeanPostProcessor实现类
    void registerBeanPostProcessors(Map<String, BeanDefinition> beanDefinitionFactory );
//    6. 典型的模板方法设计模式，这是个钩子方法，用来拓展fresh()方法
    void onRefresh();
//    这边本来还有注册事件监听器，我们的简易版没有实现， registerListeners();不作为重点跳过了。

//    最核心的方法，注册单例非懒加载Bean
    void finishBeanFactoryInitialization(Map<String, BeanDefinition> beanDefinitionFactory);
//    结束初始化，广播事件（省略）
    void finishRefresh();
}
