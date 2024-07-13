package tzrpc.framework.core;

public class ServiceConfig<T, O> {

    private Class<T> interf;     // 需要被代理的接口
    private O proxy;             // 接口实现类

    public ServiceConfig(Class<T> interf, O proxy) {
        this.interf = interf;
        this.proxy = proxy;
    }
}
