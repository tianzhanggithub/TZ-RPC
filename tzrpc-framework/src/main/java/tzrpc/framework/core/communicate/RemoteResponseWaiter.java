package tzrpc.framework.core.communicate;

import lombok.extern.slf4j.Slf4j;
import tzrpc.framework.common.exception.TzrpcException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class RemoteResponseWaiter {

    private static Map<Long, CompletableFuture<Object>> waiters = new HashMap<>();

    public static void addWaiter(Long id, CompletableFuture<Object> waiter) {
        if(id == null || id < 0 || waiter == null)
            throw new TzrpcException("system fail", "注册远端响应等待器时，参数不允许为空");
        waiters.put(id, waiter);
    }

    public static void remoteAnswer(Long id, Object msg) {
        if(id == null || id < 0)
            throw new TzrpcException("system fail", "收到远端响应时，id 无效");
        CompletableFuture<Object> waiter = waiters.get(id);
        if(waiter == null)
            throw new TzrpcException("system fail", "收到远端响应时，没有找到对应的响应等待器");
        waiters.remove(id);
        waiter.complete(msg);
    }

}
