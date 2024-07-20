package tzrpc.framework.core.enumtype;

import tzrpc.framework.common.exception.TzrpcException;

public enum CompressEnum {

    GZIP((byte)0);

    private byte code;

    CompressEnum(byte code) {
        this.code = code;
    }

    public static CompressEnum parseFromName(String name) {
        for(CompressEnum obj : CompressEnum.values()) {
            if(obj.name().equals(name))
                return obj;
        }
        throw new TzrpcException("enum parse fail", "没有找到对应的 Compress 方式: " + name);
    }

    public static CompressEnum parseFromCode(byte code) {
        for(CompressEnum obj : CompressEnum.values()) {
            if(obj.code == code)
                return obj;
        }
        throw new TzrpcException("enum parse fail", "没有找到对应的 Compress 方式: " + code);
    }

    public byte getCode() {
        return code;
    }
}
