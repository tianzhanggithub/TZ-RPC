package tzrpc.framework.core.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.core.start.ServiceConfig;
import tzrpc.framework.core.registry.core.Registry;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class ZookeeperRegistry implements Registry{

    private final ZookeeperRegistryConfig config;
    private ZooKeeper zooKeeper;           // zookeeper 服务实例

    @Autowired
    public ZookeeperRegistry(ZookeeperRegistryConfig config) throws Exception{
        this.config = config;
        buildZookeeper();
    }

    private void buildZookeeper() throws Exception {
        CountDownLatch barrier = new CountDownLatch(1);
        this.zooKeeper = new ZooKeeper(config.getAddress(), config.getTimeout(), event -> {
            // 只有连接成功，方可方放行
            if(event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                log.info("ZookeeperRegistry.buildZookeeper --> Zookeeper 服务端连接成功");
                barrier.countDown();
            }
        });
        barrier.await();
    }

    private void createNodeIfNotExist(String path, byte[] data, List<ACL> acl, CreateMode createMode) throws Exception {
        if(this.zooKeeper.exists(path, false) == null) {
            this.zooKeeper.create(path, data, acl, createMode);
        }
    }

    @Override
    public void maintainMeta() throws Exception {
        createNodeIfNotExist(config.getRootPath(), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        createNodeIfNotExist(config.getProviderPath(), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        createNodeIfNotExist(config.getConsumerPath(), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        log.info("ZookeeperRegistry.maintainMeta --> 维护 Zookeeper 元数据完毕");
    }

    @Override
    public void registerProvider(List<ServiceConfig<?, ?>> services) throws Exception {
        for(ServiceConfig<?, ?> service : Objects.requireNonNull(services)) {
            String interf = service.getInterf().getName();
            // 先创建 接口 持久节点
            String servicePath = config.getProviderPath() + "/" + interf;
            createNodeIfNotExist(servicePath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            // 然后创建 具体服务提供者 临时节点
        }
    }
}
