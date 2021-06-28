package test.advice;

public class AfterReturningAdvice2 implements BaseAdvice{
    Chain chain;
    public AfterReturningAdvice2(Chain chain){
        this.chain = chain;
    }
    @Override
    public Object excute(Chain chain) {
        chain.proceed();
        System.out.println("后向返回通知2执行");
        return null;
    }
}
