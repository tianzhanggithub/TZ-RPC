package tzrpc.framework.core.clientproxy.dynamicproxy;


import java.lang.reflect.Method;

public interface TzrpcProxyHandler {

    public Object invoke(Object proxy, Method method, Object[] args) throws Exception;

}
