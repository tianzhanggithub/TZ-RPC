package tzrpc.framework.core.clientproxy.dynamicproxy;

import lombok.extern.slf4j.Slf4j;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.core.clientproxy.dynamicproxy.TzrpcDynamicProxy;

@Slf4j
public class ClientProxyGenerator {

    public static <T> T generate(Class<T> interf) {
        try {
            return (T) TzrpcDynamicProxy.newProxyInstance(interf);
        } catch(Exception e) {
            log.error("ClientProxyGenerator.generate --> 生成 Tzrpc 动态代理对象时失败; ", e);
            throw new TzrpcException("proxy fail", "生成 Tzrpc 动态代理对象时失败");
        }
    }

}
