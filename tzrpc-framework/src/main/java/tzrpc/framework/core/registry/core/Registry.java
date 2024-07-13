package tzrpc.framework.core.registry.core;

import tzrpc.framework.core.start.ServiceConfig;

import java.util.List;

public interface Registry{

    /**
     * 维护该注册中心的元数据
     * 例如: Zookeeper 作为注册中心，需要检查 tzprc-meta 节点是否持久存在，其下的 provider、consumer 节点是否持久存在
     *      如果不是，则需要创建或更新
     */
    void maintainMeta() throws Exception;

    void registerProvider(List<ServiceConfig<?, ?>> services) throws Exception ;
//
//    void registerConsumer();
}
