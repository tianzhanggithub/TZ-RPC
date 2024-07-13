package tzrpc.framework.core.registry;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import tzrpc.framework.common.exception.TzrpcException;


@Slf4j
public class RegistryFactory{

    public static Registry product(RegistryConfig config) throws Exception{
        switch(config.getRegistry()) {
            case ZOOKEEPER:
                return new ZookeeperRegistry(config.getAddr());
            default:
                log.error("RegistryFactory.product --> 没有找到有效的注册中心; config = {};", JSON.toJSONString(config));
                throw new TzrpcException("build fail", "没有找到有效的注册中心");
        }
    }

}
