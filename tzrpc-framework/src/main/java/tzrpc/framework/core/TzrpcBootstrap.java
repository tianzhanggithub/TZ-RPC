package tzrpc.framework.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.core.protocol.ProtocolConfig;
import tzrpc.framework.core.registry.Registry;
import tzrpc.framework.core.registry.RegistryConfig;
import tzrpc.framework.core.registry.RegistryFactory;

import java.util.Collection;

@Slf4j
public class TzrpcBootstrap {

    private static TzrpcBootstrap instance;

    private String applicationName;
    private Collection<ServiceConfig<?, ?>> services;
    private ProtocolConfig protocolConfig;
    private RegistryConfig registryConfig;
    private Registry registry;

    private TzrpcBootstrap() {};

    public static TzrpcBootstrap getInstance() {
        if(instance == null) {
            synchronized (TzrpcBootstrap.class) {
                if(instance == null)
                    instance = new TzrpcBootstrap();
            }
        }
        return instance;
    }

    public TzrpcBootstrap applicationName(String name) {
        this.applicationName = name;
        return this;
    }

    public TzrpcBootstrap publish(Collection<ServiceConfig<?, ?>> services) {
        this.services = services;
        return this;
    }

    public TzrpcBootstrap registry(RegistryConfig registry) {
        this.registryConfig = registry;
        return this;
    }

    public TzrpcBootstrap protocol(ProtocolConfig protocol) {
        this.protocolConfig = protocol;
        return this;
    }

    public void start() throws Exception{
        log.info("TzrpcBootstrap.start --> 原神，启动！");
        log.info("注册到: " + this.registryConfig.getRegistry());
        log.info("使用协议: " + this.protocolConfig.getProtocol());
        this.registry = buildRegistry();
        this.registry.maintainMeta();
    }

    // 构建注册中心
    private Registry buildRegistry() throws Exception{
        if(this.registryConfig == null || this.registryConfig.getRegistry() == null || StringUtils.isBlank(this.registryConfig.getAddr())){
            log.error("TzrpcBootstrap.buildRegistry --> 注册中心未正确配置; registry = {}", this.registryConfig);
            throw new TzrpcException("fail", "注册中心未正确配置");
        }
        Registry registry = RegistryFactory.product(this.registryConfig);
        return registry;
    }
}
