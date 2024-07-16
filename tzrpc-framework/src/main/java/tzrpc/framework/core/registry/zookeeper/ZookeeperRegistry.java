package tzrpc.framework.core.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.core.serviceproxy.ServiceProxy;
import tzrpc.framework.core.registry.core.Registry;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.Random;
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

    private void createNodeIfNotExistSimple(String path, CreateMode createMode) throws Exception {
        createNodeIfNotExist(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
    }

    @Override
    public void maintainMeta() throws Exception {
        createNodeIfNotExistSimple(config.getRootPath(), CreateMode.PERSISTENT);
        createNodeIfNotExistSimple(config.getProviderPath(), CreateMode.PERSISTENT);
        createNodeIfNotExistSimple(config.getConsumerPath(), CreateMode.PERSISTENT);
        log.info("ZookeeperRegistry.maintainMeta --> 维护 Zookeeper 元数据完毕");
    }

    @Override
    public void registerService(List<ServiceProxy<?, ?>> services, String hostIp, int port) throws Exception {
        for(ServiceProxy<?, ?> service : Objects.requireNonNull(services)) {
            String interf = service.getInterf().getName();
            // 先创建 接口 持久节点
            String interfPath = config.getProviderPath() + "/" + interf;
            createNodeIfNotExistSimple(interfPath, CreateMode.PERSISTENT);
            String servicePath = interfPath + "/" + hostIp + ":" + port;
            // 然后创建 具体服务提供者 临时节点
            createNodeIfNotExistSimple(servicePath, CreateMode.EPHEMERAL);
        }
        log.info("ZookeeperRegistry.registerProvider --> 向注册中心注册 RPC 服务列表完毕; 本机地址 = {}:{};", hostIp, port);
    }

    @Override
    public InetSocketAddress discoverService(String interf) throws Exception {
        String interfPath = config.getProviderPath() + "/" + interf;
        if(zooKeeper.exists(interfPath, null) == null) {
            log.error("ZookeeperRegistry.discoverService --> 没有找到任何有效的服务根节点");
            throw new TzrpcException("discover fail", "没有找到任何有效的服务根节点");
        }
        List<String> provider = zooKeeper.getChildren(interfPath, null);
        if(provider == null || provider.size() == 0) {
            log.error("ZookeeperRegistry.discoverService --> 没有找到任何有效的服务提供者: " + interf);
            throw new TzrpcException("discover fail", "没有找到任何有效的服务提供者: " + interf);
        }
        // TODO... 假装有负载均衡，实际这里随机一个
        int sn = new Random().nextInt(provider.size());
        String address = provider.get(sn);
        String[] addrSplit;
        if(address == null || (addrSplit = address.split(":")).length != 2)
            throw new TzrpcException("find provider fail", "无效的服务提供方地址: " + address);
        return new InetSocketAddress(addrSplit[0], Integer.parseInt(addrSplit[1]));
    }
}
