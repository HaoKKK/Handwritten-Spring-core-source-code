package test.advice;

public class RoundAdvice2 implements  BaseAdvice{
    Chain chain;
    public RoundAdvice2(Chain chain){
        this.chain = chain;

    }
    @Override
    public Object excute(Chain chain) {
        try {
            System.out.println("环绕通知2前置方法");
            chain.proceed();
        } catch (Exception e){
            System.out.println("环绕通知2异常方法");
            throw e;
        }finally {
            System.out.println("环绕通知2后置方法");
        }
        System.out.println("环绕通知2后置返回方法");
        return null;
    }
}
