package test.advice;

public class AfterThrowingAdvice2 implements BaseAdvice{
    Chain chain;
    public AfterThrowingAdvice2(Chain chain){
        this.chain = chain;
    }
    @Override
    public Object excute(Chain chain) {
        try {
            chain.proceed();
        } catch (Exception e) {
            System.out.println("异常通知2执行");
            throw  e;
        }
        return null;
    }
}
