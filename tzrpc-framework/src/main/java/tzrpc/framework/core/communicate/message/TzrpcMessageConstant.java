package tzrpc.framework.core.communicate.message;


import java.nio.charset.StandardCharsets;

/**
 * 出站，信息编码器。报文格式：
 *  header:
 *      2Byte: 魔数，TZ
 *      1Byte: TZRPC 版本号
 *      2Byte: header length，请求头长度
 *      4Byte: 报文总长度
 *      1Byte: 序列化类型
 *      1Byte: 压缩方式类型
 *      1Byte: 请求类型
 *      8Byte: 请求唯一 ID
 *  body:
 *
 */
public class TzrpcMessageConstant {

    public static final byte[] magicValue = "TZ".getBytes(StandardCharsets.UTF_8);
    public static final byte version = 1;
    public static final short headerLength = 2 + 1 + 2 + 4 + 1 + 1 + 1 + 8;
    public static final int maxFrameLength = 1024 * 1024;    // 最大帧长度 1MB
    public static final int fullLengthFieldOffset = 2 + 1 + 2;
    public static final int fullLengthFieldLength = 4;
}
