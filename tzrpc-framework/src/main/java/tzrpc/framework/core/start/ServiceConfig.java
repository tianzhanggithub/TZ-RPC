package tzrpc.framework.core.start;

import lombok.extern.slf4j.Slf4j;
import tzrpc.framework.common.exception.TzrpcException;

@Slf4j
public class ServiceConfig<F, P> {

    private final Class<F> interf;     // 需要被代理的接口
    private final P proxy;             // 接口实现类

    public ServiceConfig(Class<F> interf, P proxy) {
        if(interf == null || proxy == null) {
            log.error("ServiceConfig.ServiceConfig --> 无效的代理服务配置; interf = {}; proxy = {}", interf, proxy);
            throw new TzrpcException("param invalid", "无效的代理服务!");
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
