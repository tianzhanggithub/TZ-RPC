package tzrpc.framework.core.registry.core;

import tzrpc.framework.core.serviceproxy.ServiceProxy;

import java.net.InetSocketAddress;
import java.util.List;

public interface Registry{

    /**
     * 维护该注册中心的元数据
     * 例如: Zookeeper 作为注册中心，需要检查 tzprc-meta 节点是否持久存在，其下的 provider、consumer 节点是否持久存在
     *      如果不是，则需要创建或更新
     * @throws Exception 任何异常
     */
    void maintainMeta() throws Exception;

    /**
     * 向注册中心注册服务
     * @param services 需要注册的服务列表
     * @param hostIp   本机暴露的 IP
     * @param port     本机暴露的 端口
     * @throws Exception 任何异常
     */
    void registerService(List<ServiceProxy<?, ?>> services, String hostIp, int port) throws Exception ;

    /**
     * 发现服务提供者
     * @param interf 需要的接口全类名
     * @return       服务提供者的地址
     */
    InetSocketAddress discoverService(String interf) throws Exception;
}
