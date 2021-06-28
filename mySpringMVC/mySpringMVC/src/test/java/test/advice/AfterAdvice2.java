package test.advice;

public class AfterAdvice2 implements BaseAdvice{
    Chain chain;
    public AfterAdvice2(Chain chain){
        this.chain = chain;
    }
    @Override
    public Object excute(Chain chain) {
        try {
            chain.proceed();
        }finally {
            System.out.println("后向通知2执行");
        }
        return null;
    }
}
