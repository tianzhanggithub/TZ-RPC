package tzrpc.framework.core.enumtype;

import tzrpc.framework.common.exception.TzrpcException;

public enum RequestEnum {

    COMMON((byte)0);

    private byte code;

    RequestEnum(byte code) {
        this.code = code;
    }

    public static RequestEnum parseFromName(String name) {
        for(RequestEnum obj : RequestEnum.values()) {
            if(obj.name().equals(name))
                return obj;
        }
        throw new TzrpcException("enum parse fail", "没有找到对应的 Request 类型: " + name);
    }

    public static RequestEnum parseFromCode(byte code) {
        for(RequestEnum obj : RequestEnum.values()) {
            if(obj.code == code)
                return obj;
        }
        throw new TzrpcException("enum parse fail", "没有找到对应的 Request 类型: " + code);
    }

    public byte getCode() {
        return code;
    }
}
