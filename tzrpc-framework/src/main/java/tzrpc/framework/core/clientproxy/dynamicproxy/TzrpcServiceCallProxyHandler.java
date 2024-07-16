package tzrpc.framework.core.clientproxy.dynamicproxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.core.clientproxy.dynamicproxy.TzrpcProxyHandler;
import tzrpc.framework.core.start.TzrpcBootstrap;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class TzrpcServiceCallProxyHandler implements TzrpcProxyHandler {

    @Autowired
    private TzrpcBootstrap init_tzrpcBootstrap;
    private static TzrpcBootstrap tzrpcBootstrap;

    private Class<?> interf;

    // 无参构造，仅用于 SpringBoot 自动注入使用
    public TzrpcServiceCallProxyHandler() {};

    public TzrpcServiceCallProxyHandler(Class<?> interf) {
        this.interf = interf;
    }

    @PostConstruct
    private void init() {
        tzrpcBootstrap = this.init_tzrpcBootstrap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        String interfName = method.getDeclaringClass().getName();
        InetSocketAddress proxyProviderAddr = tzrpcBootstrap.getProxyProvider(interfName);
        log.info("尝试调用远程方法: " + interfName + "." + method.getName() + "; 找到了提供者: " + proxyProviderAddr);
        // 通过 Netty 向远端请求服务执行结果
        // 定义线程池
        NioEventLoopGroup group = new NioEventLoopGroup();
        //  启动一个客户端需要一个辅助类
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap = bootstrap.group(group)
                    .remoteAddress(proxyProviderAddr)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(null);
                        }
                    });
            // 尝试连接服务器
            ChannelFuture channelFuture = bootstrap.connect().sync();
            // 获取 channel，并写出数据
            channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("hello Tzrpc".getBytes(StandardCharsets.UTF_8)));
            // 阻塞程序，等待接收消息
            channelFuture.channel().closeFuture().sync();
        } catch(Exception e) {
            log.error("Request Fail, 向服务提供方发送请求信息失败; ", e);
            throw new TzrpcException("request fail", "向服务提供方发送请求信息失败");
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch(Exception e) {
                log.error("Netty Fail, 关闭 Netty 服务失败; ", e);
                throw new TzrpcException("netty fail", "关闭 Netty 服务失败");
            }
        }
        return null;
    }

}
