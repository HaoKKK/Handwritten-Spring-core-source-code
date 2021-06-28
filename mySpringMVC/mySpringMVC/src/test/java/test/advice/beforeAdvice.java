package test.advice;


public class beforeAdvice implements BaseAdvice{
    Chain chain;
    public beforeAdvice(Chain chain){
        this.chain = chain;
    }
    @Override
    public Object excute(Chain chain) {
        System.out.println("前向通知执行");
        chain.proceed();
        return null;
    }
}
