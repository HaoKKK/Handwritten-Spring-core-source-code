package test.advice;

public class AfterThrowingAdvice implements BaseAdvice{
    Chain chain;
    public AfterThrowingAdvice(Chain chain){
        this.chain = chain;
    }
    @Override
    public Object excute(Chain chain) {
        try {
            chain.proceed();
        } catch (Exception e) {
            System.out.println("异常通知执行");
            throw  e;
        }
        return null;
    }
}
