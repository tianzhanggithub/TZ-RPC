package tzrpc.framework.core.communicate.consumer.outboundhandler;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import tzrpc.framework.common.exception.TzrpcException;
import tzrpc.framework.core.communicate.message.TzrpcMessageBody;
import tzrpc.framework.core.communicate.message.TzrpcMessageConstant;
import tzrpc.framework.core.communicate.message.TzrpcRequestMessage;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;


@Slf4j
public class RequestEncodeHandler extends MessageToByteEncoder<TzrpcRequestMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, TzrpcRequestMessage tzrpcRequestMessage, ByteBuf byteBuf) throws Exception {
        log.info("消费者 - 请求出站 - 编码器 - 开始编码");
        // 魔数值 TZ
        byteBuf.writeBytes(TzrpcMessageConstant.magicValue);
        // 版本号
        byteBuf.writeByte(TzrpcMessageConstant.version);
        // 头部长度
        byteBuf.writeShort(TzrpcMessageConstant.headerLength);
        // 报文总长度，需要计算
        byte[] body = encodeBody(tzrpcRequestMessage);
        byteBuf.writeInt(TzrpcMessageConstant.headerLength + body.length);
        // 序列化类型
        byteBuf.writeByte(tzrpcRequestMessage.getSerialize());
        // 压缩方式类型
        byteBuf.writeByte(tzrpcRequestMessage.getCompress());
        // 请求类型
        byteBuf.writeByte(tzrpcRequestMessage.getRequestType());
        // 请求唯一 id
        byteBuf.writeLong(tzrpcRequestMessage.getRequestId());
        // body
        byteBuf.writeBytes(body);
        log.info("消费者 - 请求出站 - 编码器 - 编码结束");
    }

    private byte[] encodeBody(TzrpcRequestMessage message) {
        TzrpcMessageBody body = message.getBody();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oops = new ObjectOutputStream(baos);
            oops.writeObject(body);
            return baos.toByteArray();
        } catch(Exception e) {
            log.error("RequestEncodeHandler.encodeBody --> 请求 Body 序列化失败");
            throw new TzrpcException("serialize fail", "请求 Body 序列化失败");
        }
    }
}
