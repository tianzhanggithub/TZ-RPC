package tzrpc.framework.core.start;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tzrpc")
public class TzrpcBootstrapConfig{

    private String appName = "tzrpc-application";    // 应用名称
    private int port = 8863;                         // RPC 应用端口
    private int eventLoopBossThread = 2;             // eventLoop boss 部分的线程数
    private int eventLoopWorkerThread = 10;          // eventLoop worker 部分的线程数
    private long remoteCallTimeout = 3000;           // netty 远程调用超时时间, 毫秒
    private String protocol = "TZRPC";               // 采取的协议
    private String compress = "GZIP";                // 压缩方式
    private String serialize = "JDK";               // 序列化方式，默认 JDK 序列化

}
