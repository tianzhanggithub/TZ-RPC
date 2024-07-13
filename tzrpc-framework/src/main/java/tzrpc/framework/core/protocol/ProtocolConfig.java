package tzrpc.framework.core.protocol;

public class ProtocolConfig {

    private ProtocolEnum protocol;

    public ProtocolConfig(ProtocolEnum protocol) {
        this.protocol = protocol;
    }

    public ProtocolEnum getProtocol() {
        return this.protocol;
    }
}
