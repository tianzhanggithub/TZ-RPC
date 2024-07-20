package tzrpc.framework.core.enumtype;

import tzrpc.framework.common.exception.TzrpcException;

public enum SerializeEnum {

    JDK((byte)0);

    private byte code;

    SerializeEnum(byte code) {
        this.code = code;
    }

    public static SerializeEnum parseFromName(String name) {
        for(SerializeEnum obj : SerializeEnum.values()) {
            if(obj.name().equals(name))
                return obj;
        }
        throw new TzrpcException("enum parse fail", "没有找到对应的 Serialize 方式: " + name);
    }

    public static SerializeEnum parseFromCode(byte code) {
        for(SerializeEnum obj : SerializeEnum.values()) {
            if(obj.code == code)
                return obj;
        }
        throw new TzrpcException("enum parse fail", "没有找到对应的 Serialize 方式: " + code);
    }

    public byte getCode() {
        return code;
    }
}
