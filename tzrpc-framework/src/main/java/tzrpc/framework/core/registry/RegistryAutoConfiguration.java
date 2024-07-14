package tzrpc.framework.core.registry;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tzrpc.framework.core.registry.zookeeper.ZookeeperRegistry;
import tzrpc.framework.core.registry.zookeeper.ZookeeperRegistryAutoConfiguration;

@Configuration
@Import({ZookeeperRegistryAutoConfiguration.class})
public class RegistryAutoConfiguration {
}
