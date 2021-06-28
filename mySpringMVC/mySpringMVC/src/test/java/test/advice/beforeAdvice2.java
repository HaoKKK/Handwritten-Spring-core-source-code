package test.advice;


public class beforeAdvice2 implements BaseAdvice{
    Chain chain;
    public beforeAdvice2(Chain chain){
        this.chain = chain;
    }
    @Override
    public Object excute(Chain chain) {
        System.out.println("前向通知2执行");
        chain.proceed();
        return null;
    }
}
