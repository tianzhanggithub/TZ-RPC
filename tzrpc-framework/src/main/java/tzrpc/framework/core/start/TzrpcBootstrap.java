package tzrpc.framework.core.start;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.common.util.NetUtil;
import tzrpc.framework.core.protocol.ProtocolEnum;
import tzrpc.framework.core.registry.core.Registry;
import tzrpc.framework.core.serviceproxy.ServiceProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TzrpcBootstrap {

    @Autowired
    private Registry registry;
    @Autowired
    private TzrpcBootstrapConfig bootstrapConfig;

    private Map<String, ServiceProxy<?, ?>> serviceProxy;
    private ProtocolEnum protocol;

    public TzrpcBootstrap setServiceProxy(List<ServiceProxy<?, ?>> proxies) {
        if(this.serviceProxy != null) {
            throw new TzrpcException("config fail", "TzrpcBootstrap-serviceProxy 仅可被配置一次");
        }
        this.serviceProxy = Objects.requireNonNull(proxies).stream()
                .collect(Collectors.toMap(
                        v -> v.getInterf().getName(),
                        v -> v
                ));
        return this;
    }

    public TzrpcBootstrap setProtocol(ProtocolEnum protocol) {
        if(this.protocol != null) {
            throw new TzrpcException("config fail", "TzrpcBootstrap-Protocol 仅可被配置一次");
        }
        this.protocol = protocol;
        return this;
    }

    // 启动
    public void start() throws Exception {
        log.info("TzrpcBootstrap.start --> 原神，启动！");
        this.registry.maintainMeta();
        this.registry.registerService(new ArrayList<>(this.serviceProxy.values()), NetUtil.getIp(), bootstrapConfig.getPort());
    }

    public String getProxyProvider(String interf) throws Exception {
        return registry.discoverService(interf);
    }
}
