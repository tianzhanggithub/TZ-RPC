package tzrpc.framework.core.clientproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.core.start.TzrpcBootstrap;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

@Slf4j
@Component
public class TzrpcServiceCallProxyHandler implements TzrpcProxyHandler {

    @Autowired
    private TzrpcBootstrap init_tzrpcBootstrap;
    private static TzrpcBootstrap tzrpcBootstrap;

    private Class<?> interf;

    // 无参构造，仅用于 SpringBoot 自动注入使用
    public TzrpcServiceCallProxyHandler() {};

    public TzrpcServiceCallProxyHandler(Class<?> interf) {
        this.interf = interf;
    }

    @PostConstruct
    private void init() {
        tzrpcBootstrap = this.init_tzrpcBootstrap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        String interfName = method.getDeclaringClass().getName();
        String proxyProviderAddr = tzrpcBootstrap.getProxyProvider(interfName);
        log.info("尝试调用远程方法: " + interfName + "." + method.getName() + "; 找到了提供者: " + proxyProviderAddr);
        return null;
    }

}
