package test.advice;

import java.util.LinkedList;
import java.util.List;

public class Chain {
    public List<BaseAdvice> chainsImpl = new LinkedList<>();
    int index = 0;
    public Object proceed(){
        if(index == chainsImpl.size()){
            System.out.println("目标方法执行");
            return 1;
        }else {
            return chainsImpl.get(index++).excute(this);
        }
    }
}
