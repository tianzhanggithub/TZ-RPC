package tzrpc.framework.core.registry.zookeeper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import tzrpc.framework.common.util.TextUtil;

import javax.annotation.PostConstruct;

@Data
@Configuration
@AutoConfigureBefore(ZookeeperRegistry.class)
@ConfigurationProperties(prefix = "tzrpc.registry.zookeeper")
@Slf4j
public class ZookeeperRegistryConfig{

    // zookeeper 作为注册中心的元数据配置，不可更改
    private final String rootPath = "/tzrpc-meta";
    private final String providerPath = rootPath + "/providers";
    private final String consumerPath = rootPath + "/consumers";

    // 用户可定义配置
    private int timeout = 10000;     // 超时时间: 10s
    private String address = "127.0.0.1:2181";       // zookeeper 地址
    private int port = 2181;

    @PostConstruct
    private void init() {
        log.info("注册中心选择: Zookeeper , 参数列表: ");
        log.info(TextUtil.tab + "address = " + address);
        log.info(TextUtil.tab + "port = " + port);
        log.info(TextUtil.tab + "timeout = " + timeout);
    }
}
