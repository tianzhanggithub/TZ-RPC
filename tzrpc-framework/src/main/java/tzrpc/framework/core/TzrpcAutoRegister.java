package tzrpc.framework.core;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import tzrpc.framework.annotation.TzrpcProvider;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.common.util.CollectionUtil;
import tzrpc.framework.common.util.TextUtil;
import tzrpc.framework.core.ServiceConfig;
import tzrpc.framework.core.protocol.ProtocolConfig;
import tzrpc.framework.core.protocol.ProtocolEnum;
import tzrpc.framework.core.registry.RegistryConfig;
import tzrpc.framework.core.registry.RegistryEnum;

import java.util.*;

@Slf4j
@Component
public class TzrpcAutoRegister implements CommandLineRunner {

    private final ApplicationContext applicationContext;

    @Autowired
    public TzrpcAutoRegister(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception{
        List<ServiceConfig<?, ?>> serviceConfig = loadServiceConfig();
        start(serviceConfig);
    }

    private List<ServiceConfig<?, ?>> loadServiceConfig() {
        // 加载所有携带 @TzrpcProvider 注解的 Bean，并打印加载结果信息
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(TzrpcProvider.class);
        log.info("TzrpcAutoRegister.run 加载 Provider 列表 --> ");
        // 对所有 Provider，注册到 Tzrpc 中去
        // map.key = 需要被代理的接口，为了防止一个接口被多个类实现
        Map<Class<?>, ServiceConfig<?, ?>> services = new HashMap<>();
        for(Object provider : providers.values()) {
            TzrpcProvider annotation = provider.getClass().getAnnotation(TzrpcProvider.class);
            Class<?>[] interfs = annotation.proxy();
            // 每一个需要被代理的接口，都注册到 Tzrpc 中去
            for(Class<?> interf : interfs) {
                log.info(TextUtil.tab + interf.getName() + " --> " + provider.getClass().getName());
                ServiceConfig<?, Object> serviceConfig = new ServiceConfig<>(interf, provider);
                if(services.get(interf) != null) {
                    log.error("TzrpcAutoRegister.run --> 注册 TZRPC 服务失败");
                    throw new TzrpcException("TzrpcAutoRegister.run register fail", "接口 " + interf.getName() + " 存在重复的实现 Provider");
                }
                services.put(interf, serviceConfig);
            }
        }
        return new ArrayList<>(services.values());
    }

    private void start(List<ServiceConfig<?, ?>> serviceConfig) throws Exception{
        TzrpcBootstrap.getInstance()
                .applicationName("test-tzrpc-server")
                .registry(new RegistryConfig(RegistryEnum.ZOOKEEPER, "127.0.0.1:2181"))
                .protocol(new ProtocolConfig(ProtocolEnum.JDK))
                .publish(serviceConfig)
                .start();
    }
}
