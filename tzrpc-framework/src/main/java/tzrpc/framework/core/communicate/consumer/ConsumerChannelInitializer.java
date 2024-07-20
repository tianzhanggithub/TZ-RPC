package tzrpc.framework.core.communicate.consumer;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import tzrpc.framework.core.communicate.consumer.outboundhandler.RequestEncodeHandler;

@Slf4j
public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                // 请求出站编码器
                .addLast(new LoggingHandler(LogLevel.INFO))
                .addLast(new RequestEncodeHandler());
    }
}
