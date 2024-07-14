package tzrpc.framework.core.serviceproxy;

import lombok.extern.slf4j.Slf4j;
import tzrpc.framework.common.exception.TzrpcException;

import java.util.Objects;

@Slf4j
public class ServiceProxy<F, P> {

    private final Class<F> interf;     // 需要被代理的接口
    private final P proxy;             // 接口实现类

    public ServiceProxy(Class<F> interf, P proxy) {
        if(interf == null || proxy == null) {
            log.error("ServiceProxy.ServiceProxy --> 无效的代理服务配置; interf = {}; proxy = {}", interf, proxy);
            throw new TzrpcException("config fail", "无效的代理服务!");
        }
        if(!interf.isInterface()) {
            log.error("ServiceProxy.ServiceProxy --> 被代理者必须是一个 Interface; interf = {}; proxy = {}", interf, proxy);
            throw new TzrpcException("config fail", "被代理者必须是一个 Interface");
        }
        this.interf = interf;
        this.proxy = proxy;
    }

    public Class<F> getInterf() {
        return this.interf;
    }

    public P getProxy() {
        return this.proxy;
    }
}
