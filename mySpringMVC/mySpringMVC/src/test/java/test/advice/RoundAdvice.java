package test.advice;

public class RoundAdvice implements  BaseAdvice{
    Chain chain;
    public RoundAdvice(Chain chain){
        this.chain = chain;

    }
    @Override
    public Object excute(Chain chain) {
        try {
            System.out.println("环绕通知前置方法");
            chain.proceed();
        } catch (Exception e){
            System.out.println("环绕通知异常方法");
            throw e;
        }finally {
            System.out.println("环绕通知后置方法");
        }
        System.out.println("环绕通知后置返回方法");
        return null;
    }
}
