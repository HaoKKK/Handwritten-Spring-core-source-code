package test.advice;

public class AfterAdvice implements BaseAdvice{
    Chain chain;
    public AfterAdvice(Chain chain){
        this.chain = chain;
    }
    @Override
    public Object excute(Chain chain) {
        try {
            chain.proceed();
        }finally {
            System.out.println("后向通知执行");
        }
        return null;
    }
}
