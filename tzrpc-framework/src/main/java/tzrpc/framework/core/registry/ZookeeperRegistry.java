package tzrpc.framework.core.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ZookeeperRegistry implements Registry {

    private static final String metaKey = "/tzrpc-meta";
    private static final String providersKey = metaKey + "/providers";
    private static final String consumersKey = metaKey + "/consumers";
    private static final int timeout = 10000;     // 超时时间: 10s

    private final String addr;             // 地址
    private ZooKeeper zooKeeper;           // zookeeper 服务实例

    public ZookeeperRegistry(String addr) throws Exception{
        this.addr = addr;
        buildZookeeper();
    }


    private void buildZookeeper() throws Exception {
        CountDownLatch barrier = new CountDownLatch(1);
        this.zooKeeper = new ZooKeeper(addr, timeout, event -> {
            // 只有连接成功，方可方放行
            if(event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                log.info("ZookeeperRegistry.buildZookeeper --> Zookeeper 服务端连接成功");
                barrier.countDown();
            }
        });
        barrier.await();
    }

    private void createNode(String path, byte[] data, List<ACL> acl, CreateMode createMode) throws Exception {
        if(zooKeeper.exists(path, false) == null) {
            zooKeeper.create(path, data,  acl, createMode);
        }
    }

    @Override
    public void maintainMeta() throws Exception {
        createNode(metaKey, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        createNode(providersKey, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        createNode(consumersKey, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        log.info("ZookeeperRegistry.maintainMeta --> 维护 Zookeeper 元数据完毕");
    }

}
