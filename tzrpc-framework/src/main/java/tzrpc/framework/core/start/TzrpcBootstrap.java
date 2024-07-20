package tzrpc.framework.core.start;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.common.util.NetUtil;
import tzrpc.framework.core.enumtype.ProtocolEnum;
import tzrpc.framework.core.registry.core.Registry;
import tzrpc.framework.core.serviceproxy.ServiceProxy;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TzrpcBootstrap {

    @Autowired
    private Registry registry;    // 服务注册中心
    @Autowired
    private TzrpcBootstrapConfig bootstrapConfig;   // Tzrpc 核心配置文件

    private Map<String, ServiceProxy<?, ?>> serviceProxy;    // 本应用所有注册的服务，key: 接口全限定名
    private ProtocolEnum protocol;    // 本应用使用的通信协议

    // 设置本应用的注册服务列表
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

    // 设置本应用的通信协议
    public TzrpcBootstrap setProtocol(ProtocolEnum protocol) {
        if(this.protocol != null) {
            throw new TzrpcException("config fail", "TzrpcBootstrap-Protocol 仅可被配置一次");
        }
        this.protocol = protocol;
        return this;
    }

    // 应用启动
    public void start() throws Exception {
        log.info("TzrpcBootstrap.start --> 原神，启动！");
        // 1. 调用注册中心的 "维护元数据" 方法，检查(保证)注册中心元数据的完整性
        this.registry.maintainMeta();
        // 2. 向注册中心 注册所有服务，暴露地址为本应用服务器地址
        this.registry.registerService(new ArrayList<>(this.serviceProxy.values()), NetUtil.getIp(), bootstrapConfig.getPort());
    }

    // 给定一个 api 接口，通过注册中心拿到一个可用的 服务提供者地址
    public InetSocketAddress getProxyProvider(String interf) throws Exception {
        return registry.discoverService(interf);
    }
}
