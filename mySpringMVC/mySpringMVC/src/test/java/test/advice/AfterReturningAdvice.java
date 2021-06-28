package test.advice;

public class AfterReturningAdvice implements BaseAdvice{
    Chain chain;
    public AfterReturningAdvice(Chain chain){
        this.chain = chain;
    }
    @Override
    public Object excute(Chain chain) {
        chain.proceed();
        System.out.println("后向返回通知执行");
        return null;
    }
}
