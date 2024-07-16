package tzrpc.framework.core.start;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.common.util.NetUtil;
import tzrpc.framework.core.protocol.ProtocolEnum;
import tzrpc.framework.core.registry.core.Registry;
import tzrpc.framework.core.serviceproxy.ServiceProxy;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TzrpcBootstrap {

    @Autowired
    private Registry registry;    // 服务注册中心
    @Autowired
    private TzrpcBootstrapConfig bootstrapConfig;   // Tzrpc 核心配置文件

    private Map<String, ServiceProxy<?, ?>> serviceProxy;    // 本应用所有注册的服务，key: 接口全限定名
    private ProtocolEnum protocol;    // 本应用使用的通信协议

    // 设置本应用的注册服务列表
    public TzrpcBootstrap setServiceProxy(List<ServiceProxy<?, ?>> proxies) {
        if(this.serviceProxy != null) {
            throw new TzrpcException("config fail", "TzrpcBootstrap-serviceProxy 仅可被配置一次");
        }
        this.serviceProxy = Objects.requireNonNull(proxies).stream()
                .collect(Collectors.toMap(
                        v -> v.getInterf().getName(),
                        v -> v
                ));
        return this;
    }

    // 设置本应用的通信协议
    public TzrpcBootstrap setProtocol(ProtocolEnum protocol) {
        if(this.protocol != null) {
            throw new TzrpcException("config fail", "TzrpcBootstrap-Protocol 仅可被配置一次");
        }
        this.protocol = protocol;
        return this;
    }

    // 应用启动
    public void start() throws Exception {
        log.info("TzrpcBootstrap.start --> 原神，启动！");
        // 1. 调用注册中心的 "维护元数据" 方法，检查(保证)注册中心元数据的完整性
        this.registry.maintainMeta();
        // 2. 向注册中心 注册所有服务，暴露地址为本应用服务器地址
        this.registry.registerService(new ArrayList<>(this.serviceProxy.values()), NetUtil.getIp(), bootstrapConfig.getPort());
        // 3. 启动 Netty 服务器
        startNetty();
    }

    // 启动 Netty 服务
    private void startNetty() {
        log.info("TzrpcBootstrap.startNetty --> 启动 Netty 服务");
        // 1. 创建 eventLoop，boss 只负责处理请求，worker 执行具体任务
        EventLoopGroup boss = new NioEventLoopGroup(bootstrapConfig.getEventLoopWorkerThread());
        EventLoopGroup worker = new NioEventLoopGroup(bootstrapConfig.getEventLoopWorkerThread());
        try {
            // 创建一个引导程序
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 配置 Netty 服务器
            serverBootstrap = serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 核心，需要添加很多入站和出站的 Handler
                            socketChannel.pipeline().addLast(null);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(bootstrapConfig.getPort()).sync();
            channelFuture.channel().closeFuture().sync();
        } catch(Exception e) {
            throw new TzrpcException("netty fail", e.getMessage());
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch(Exception e) {
                throw new TzrpcException("netty shutdown fail", e.getMessage());
            }
        }
    }

    // 给定一个 api 接口，通过注册中心拿到一个可用的 服务提供者地址
    public InetSocketAddress getProxyProvider(String interf) throws Exception {
        return registry.discoverService(interf);
    }
}
