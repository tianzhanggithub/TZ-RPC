package tzrpc.framework.core.clientproxy.dynamicproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.core.communicate.consumer.NetRequestExecutor;
import tzrpc.framework.core.start.TzrpcBootstrap;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

@Slf4j
@Component
public class TzrpcServiceCallProxyHandler implements TzrpcProxyHandler {

    @Autowired
    private TzrpcBootstrap init_tzrpcBootstrap;
    private static TzrpcBootstrap tzrpcBootstrap;
    @Autowired
    private NetRequestExecutor init_netRequestExecutor;
    private static NetRequestExecutor netRequestExecutor;

    private Class<?> interf;

    // 无参构造，仅用于 SpringBoot 自动注入使用
    public TzrpcServiceCallProxyHandler() {};

    public TzrpcServiceCallProxyHandler(Class<?> interf) {
        this.interf = interf;
    }

    @PostConstruct
    private void init() {
        tzrpcBootstrap = this.init_tzrpcBootstrap;
        netRequestExecutor = this.init_netRequestExecutor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        Object response = netRequestExecutor.remoteExecute(method, args);
        return null;
    }

}
