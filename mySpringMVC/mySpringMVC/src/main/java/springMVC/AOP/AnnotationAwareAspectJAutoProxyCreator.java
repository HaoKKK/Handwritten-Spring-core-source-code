package springMVC.AOP;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import springMVC.AOP.Annotation.Pointcut;
import springMVC.AnnotationAndSpecialInterfaces.Aspect;
import springMVC.AnnotationAndSpecialInterfaces.BeanPostProcessor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 下面是AOP部分，底下的容器是AOP和核心容器AnnotationAwareAspectJAutoProxyCreator。
 * AOP部分可以说是Spring部分最难的部分，尤其是和IOC的初始化耦合在一起，如何构建责任链，如何选择代理对象、
 * 如何判断该对象是否该被代理、如何构造Advisor，如何构造Advice等等。
 * 我们也会在不影响整个思路和设计方式基础上，适当做一些简化（毕竟SpringAOP内容还是很多，有的逻辑也确实没必要写，需要可以补充）
 * 如果读者大大能写出更好的代码，欢迎联系！！
 */

public class AnnotationAwareAspectJAutoProxyCreator implements BeanPostProcessor {
//    保存了所有的切面类
    List<Object> aspectsBeans;
//    保存了切点方法和它对应的执行链，AOP的核心部分
    HashMap<Method,Chain> chains = new HashMap<>();
    public AnnotationAwareAspectJAutoProxyCreator(List<Object> aspectsBeans){
        this.aspectsBeans = aspectsBeans;
    }

//    BeforeInitialization没有啥操作，直接放过去了
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

//    AfterInitialization是重点，判断是否需要代理并实现动态代理！
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
//        首先默认返回结果就是原来创建的bean
        Object res = bean;
//        如果它本身就是切面类，那我们根本不用考虑他的aop问题，直接返回
//        Spring源码中也是类似的逻辑，不过他判断的特殊类很多，都是直接返回，我们不用管它。
        if(bean.getClass().isAnnotationPresent(Aspect.class)){
            return res;
        }
//        如果需要加强就要把res换成bean的代理类，不是的话就直接返回
        if(needStrength(bean)){
//            获得所有加强bean的切面类并封装成advisor
//            注意这个Advisor就是切面类包装的，读者可以想想都要包装哪些内容？
            Advisor[] usefulAspectsClass = getAllAspects(bean);
            /**
             我们的AOP做了如下简化：一个切面类通过@pointCut确定切点，@Before、@After...就不带参数了
             这样一个切面类就切一个切点，比较容易理解。也符合我们使用AOP的常规用法。分开写也不会太难，就是有点繁琐。
             */
//            找出所有能切这个切点的advice并封装成chain，注意如果有多个需要合并成一条chain
            convertToChain(usefulAspectsClass);
//            如果有接口可以采用Proxy的动态代理
            if(bean.getClass().getInterfaces().length != 0){
                InvocationHandler myHandler = new myHandler(chains,bean);
                res = Proxy.newProxyInstance(getClass().getClassLoader(), bean.getClass().getInterfaces(), myHandler);
            }else {
//              否则采用cglib动态代理;
//               在Spring源码中可以指定选择CGLIB动态代理，这里我们方便起见就按默认原则规定了
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(bean.getClass());
                CgLibInterceptor cgLibInterceptor = new CgLibInterceptor();
                cgLibInterceptor.chains = chains;
                cgLibInterceptor.tarBean = bean;
                enhancer.setCallback(cgLibInterceptor);
                res = enhancer.create();
            }
        }

        return res;
    }
//比较不好理解，别走神啦！
    private void convertToChain(Advisor[] usefulAspectsClass) {
        for(Advisor advisor : usefulAspectsClass){
//            拿到advisor能增强的方法
            Method key = advisor.getTarMethod();
//            从现有的责任链池中取出之前匹配上该key的chain（如果有的话）
            Chain chain = chains.getOrDefault(key,new Chain());
//            把advisor中所有的advice都拿出来
            List<BaseAdviceAbstractImpl> adviceList = advisor.getAdviceList();
//            这里为什么要记录插入时候的size呢？因为之后要对advice排序，不同advice执行顺序是完全不同的
            int begin = chain.chainsImpl.size();
//            每条责任链要包含所有的advice，每个advice要拥有这条责任链，advice拿责任链这个操作当执行execute操作再给它
            chain.chainsImpl.addAll(adviceList);
//            把新加入的链条排序
            chain.orderChainsImpl(begin);
//            责任链拿到目标对象和要增强的方法
            chain.setTarObj(advisor.getTarBean());
            chain.setTarMethod(advisor.getTarMethod());
//            把新链子放到chains中
            chains.put(key,chain);
        }
    }
//这个方法就是把所有切面Bean封装成advisor
    private Advisor[] getAllAspects(Object bean) {
        List<Advisor> advisors = new LinkedList<>();
//        遍历所有的切面对象
        for(Object o:aspectsBeans){
//            获取类信息
            Class<?> clazz = o.getClass();
//            如果切面类支持bean的任意方法就可以封装，由于我们这边一个切面类统一一个切点
//            所以这里直接返回切点即可。
//            如果不限制这个条件其实写起来也不复杂，就是既得返回切点、也得返回具体的切面类的通知
            Method tarMethod = support(clazz,bean);
//            不为空
            if(tarMethod != null){
//                封装advisor
                Advisor advisor = new Advisor();
//                切面对象
                advisor.setAspectsBean(o);
//                切面对象的类信息
                advisor.setAspectClass(clazz);
//                目标bean
                advisor.setTarBean(bean);
//                切点
                advisor.setTarMethod(tarMethod);
//                把里面所有的advice都创建好
                advisor.createAdvice();
//                加入advisors中
                advisors.add(advisor);
            }
        }
//        这就是list转数组....没哈
        Advisor[] res = new Advisor[advisors.size()];
        for(int i =0;i<res.length;i++){
            res[i] = advisors.get(i);
        }
        return res;
    }

    private boolean needStrength(Object bean) {
//        判断某个Bean是否需要增强的逻辑：
//        通过遍历所有的aspectsBean，如果某个支持这个Bean就返回true
        for(Object o:aspectsBeans){
            Class<?> c = o.getClass();
            if(support(c,bean) != null){
                return true;
            }
        }
        return false;
    }
//      support判别aspectsBeanClass支持哪个bean的方法，不支持就返回null
    private Method support(Class<?> c, Object bean) {
//        获取所有的切面类方法
        Method[] methods  = c.getDeclaredMethods();
        String methodValue = "";
//        遍历的意义是找Pointcut，看看是否支持bean中的某个方法
        for(Method method : methods){
            if(method.isAnnotationPresent(Pointcut.class)){
//                获取切点表达式
                methodValue = method.getAnnotation(Pointcut.class).value();
                /**
                 * 这里做一下自我批评....这个methodValue取得太草率了，就是把方法名拿出来了，实际上应该把包、类名、方法名、参数列表都做对比
                 * 这个后续我会做修改，时间紧就先这样放着了。
                 */
                methodValue = methodValue.substring(methodValue.lastIndexOf(".") + 1,methodValue.length()-3);
            }
        }
        if(methodValue.equals("")){return null;}
        Method[] tarMethods = bean.getClass().getDeclaredMethods();
//        同名方法就认为对了.....这个逻辑后续会修改，读者大大们知道这个思路就行了
        for(Method tarMethod : tarMethods){
            if(tarMethod.getName().equals(methodValue)){
                return tarMethod;
            }
        }
        return null;
    }

}
//cglib动态代理
class CgLibInterceptor implements MethodInterceptor{
    HashMap<Method,Chain> chains;
    Object tarBean;
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if(chains.containsKey(method)){
            Chain chain = chains.get(method);
            chain.setTarObj(tarBean);
            chain.setTarArgs(objects);
            chain.setProxyObject(this);
            chain.index = 0;
            return chain.proceed();
        }
        return method.invoke(tarBean,objects);
    }
}
//jdk动态代理
class myHandler implements InvocationHandler{
    Object tarBean;
    HashMap<Method,Chain> chains;
    public myHandler(HashMap<Method, Chain> map,Object tarBean){
        this.chains = map;
        this.tarBean = tarBean;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(chains.containsKey(method)){
            Chain chain = chains.get(method);
            chain.setTarObj(tarBean);
            chain.setTarArgs(args);
            chain.setProxyObject(this);
            chain.index = 0;
            return chain.proceed();
        }
        return method.invoke(tarBean,args);
    }
}
