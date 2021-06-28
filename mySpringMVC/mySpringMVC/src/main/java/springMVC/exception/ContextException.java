package springMVC.exception;

//复习知识点：java自定义异常，这个miniSpring框架的异常体系并不完善，这里只是象征性的弄了个
public class ContextException extends RuntimeException{
    public ContextException(String message) {
        super(message);
    }

    public ContextException(Throwable cause) {
        super(cause);
    }


    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
