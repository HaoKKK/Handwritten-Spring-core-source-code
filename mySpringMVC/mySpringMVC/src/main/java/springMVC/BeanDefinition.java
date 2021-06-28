package springMVC;
//这里就是IOC中最重要的类：BeanDefinition的组成
public class BeanDefinition {
    private Class beanClass;
    private String scope;
    private boolean isLazy;
    private boolean isPostProcessor;
    public boolean isPostProcessor() {
        return isPostProcessor;
    }

    public void setPostProcessor(boolean postProcessor) {
        isPostProcessor = postProcessor;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }
}

//这里我拷贝了Spring源码中的BeanDefinition接口供大家参考，由于我们的框架也没有FactoryBean的逻辑（虽然这并不复杂，需要的话作者可以补充）
//还有一些继承和Bean依赖depends-on关系等等，实际的BeanDefinition要复杂得多。但我们只要把核心思想学到，再复杂也没啥区别，无非就是功能的拓展罢了
/**
 * public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {
 *
 *     String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
 *     String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;
 *
 *     // 比较不重要，直接跳过吧
 *     int ROLE_APPLICATION = 0;
 *     int ROLE_SUPPORT = 1;
 *     int ROLE_INFRASTRUCTURE = 2;
 *
 *     // 设置父 Bean，这里涉及到 bean 继承，不是 java 继承。请参见附录的详细介绍
 *     // 一句话就是：继承父 Bean 的配置信息而已
 *     void setParentName(String parentName);
 *
 *     // 获取父 Bean
 *     String getParentName();
 *
 *     // 设置 Bean 的类名称，将来是要通过反射来生成实例的
 *     void setBeanClassName(String beanClassName);
 *
 *     // 获取 Bean 的类名称
 *     String getBeanClassName();
 *
 *
 *     // 设置 bean 的 scope
 *     void setScope(String scope);
 *
 *     String getScope();
 *
 *     // 设置是否懒加载
 *     void setLazyInit(boolean lazyInit);
 *
 *     boolean isLazyInit();
 *
 *     // 设置该 Bean 依赖的所有的 Bean，注意，这里的依赖不是指属性依赖(如 @Autowire 标记的)，
 *     // 是 depends-on="" 属性设置的值。
 *     void setDependsOn(String... dependsOn);
 *
 *     // 返回该 Bean 的所有依赖
 *     String[] getDependsOn();
 *
 *     // 设置该 Bean 是否可以注入到其他 Bean 中，只对根据类型注入有效，
 *     // 如果根据名称注入，即使这边设置了 false，也是可以的
 *     void setAutowireCandidate(boolean autowireCandidate);
 *
 *     // 该 Bean 是否可以注入到其他 Bean 中
 *     boolean isAutowireCandidate();
 *
 *     // 主要的。同一接口的多个实现，如果不指定名字的话，Spring 会优先选择设置 primary 为 true 的 bean
 *     void setPrimary(boolean primary);
 *
 *     // 是否是 primary 的
 *     boolean isPrimary();
 *
 *     // 如果该 Bean 采用工厂方法生成，指定工厂名称。对工厂不熟悉的读者，请参加附录
 *     // 一句话就是：有些实例不是用反射生成的，而是用工厂模式生成的
 *     void setFactoryBeanName(String factoryBeanName);
 *     // 获取工厂名称
 *     String getFactoryBeanName();
 *     // 指定工厂类中的 工厂方法名称
 *     void setFactoryMethodName(String factoryMethodName);
 *     // 获取工厂类中的 工厂方法名称
 *     String getFactoryMethodName();
 *
 *     // 构造器参数
 *     ConstructorArgumentValues getConstructorArgumentValues();
 *
 *     // Bean 中的属性值，后面给 bean 注入属性值的时候会说到
 *     MutablePropertyValues getPropertyValues();
 *
 *     // 是否 singleton
 *     boolean isSingleton();
 *
 *     // 是否 prototype
 *     boolean isPrototype();
 *
 *     // 如果这个 Bean 是被设置为 abstract，那么不能实例化，
 *     // 常用于作为 父bean 用于继承，其实也很少用......
 *     boolean isAbstract();
 *
 *     int getRole();
 *     String getDescription();
 *     String getResourceDescription();
 *     BeanDefinition getOriginatingBeanDefinition();
 * }
 */
