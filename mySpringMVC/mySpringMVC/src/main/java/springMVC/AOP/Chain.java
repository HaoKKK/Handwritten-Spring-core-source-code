package springMVC.AOP;
import lombok.Data;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
@Data
//责任链模式的核心类：chain
public class Chain {
//    保存了每个链节点的advice
    public List<BaseAdviceAbstractImpl> chainsImpl = new LinkedList<>();
//    目前该链条执行到的位置，记得初始为0
    int index = 0;
//    切点
    Method tarMethod;
//    目标类
    Object tarObj;
//    目标切点执行的参数
    Object[] tarArgs;
//    代理对象，主要可以个JoinPoint返回
    Object proxyObject;
//    责任链的推进
    public Object proceed() throws InvocationTargetException, IllegalAccessException {
//        责任链执行完毕，开始执行目标方法
        if(index == chainsImpl.size()){
            return tarMethod.invoke(tarObj,tarArgs);
        }else {
//            执行责任链的advice
            return chainsImpl.get(index++).execute(this);
        }
    }
//按照优先级对责任链进行排序。注意我们这里没有写@around注解的逻辑，它本质就是其他注解的总和
//    我们想想这几个注解的顺序应该是怎么样的呢？这里给大家总结了下：
//        chain.chainsImpl.add(new AfterThrowingAdvice(chain));
//        chain.chainsImpl.add(new AfterReturningAdvice(chain));
//        chain.chainsImpl.add(new AfterAdvice(chain));
//          如果有@Around的话，它的位置应该在这里
////      chain.chainsImpl.add(new RoundAdvice(chain));
//        chain.chainsImpl.add(new beforeAdvice(chain));
    public void orderChainsImpl(int begin) {
        List<BaseAdviceAbstractImpl> temp = new LinkedList<>();
        for(int i = begin;i<chainsImpl.size();i++){
            temp.add(chainsImpl.get(i));
        }
        Collections.sort(temp, new Comparator<BaseAdviceAbstractImpl>() {
            @Override
            public int compare(BaseAdviceAbstractImpl o1, BaseAdviceAbstractImpl o2) {
                return o1.priority - o2.priority;
            }
        });
        for(int i = begin;i<chainsImpl.size();i++){
            chainsImpl.set(i,temp.get(i-begin));
        }
    }
}
