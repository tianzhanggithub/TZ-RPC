package tzrpc.framework.common.exception;

public class TzrpcException extends RuntimeException {

    public final String code;
    public final String msg;

    public TzrpcException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
