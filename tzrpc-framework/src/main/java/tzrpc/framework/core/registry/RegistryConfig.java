package tzrpc.framework.core.registry;

public class RegistryConfig {

    private RegistryEnum registry;
    private String addr;

    public RegistryConfig(RegistryEnum registry, String addr) {
        this.registry = registry;
        this.addr = addr;
    }

    public RegistryEnum getRegistry(){
        return registry;
    }

    public String getAddr(){
        return addr;
    }
}
