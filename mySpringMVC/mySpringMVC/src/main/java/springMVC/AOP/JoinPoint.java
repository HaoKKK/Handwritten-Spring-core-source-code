package springMVC.AOP;
//JoinPoint，AOP很重要的一部分，可以在通知期间获得很多信息
public class JoinPoint {
    Object tarBean;
    Object[] tarBeanArgs;
    Object thisProxyObject;

    public Object getTarget() {
        return tarBean;
    }

    public void setTarBean(Object tarBean) {
        this.tarBean = tarBean;
    }

    public Object[] getArgs() {
        return tarBeanArgs;
    }

    public void setTarBeanArgs(Object[] tarBeanArgs) {
        this.tarBeanArgs = tarBeanArgs;
    }

    public Object getThis() {
        return thisProxyObject;
    }

    public void setThisProxyObject(Object thisProxyObject) {
        this.thisProxyObject = thisProxyObject;
    }
}
