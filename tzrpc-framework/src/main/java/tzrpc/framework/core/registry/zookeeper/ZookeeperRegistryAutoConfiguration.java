package tzrpc.framework.core.registry.zookeeper;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(name = "tzrpc.registry.type", havingValue = "Zookeeper", matchIfMissing = false)
@Import({ZookeeperRegistryConfig.class, ZookeeperRegistry.class})
public class ZookeeperRegistryAutoConfiguration {
}
