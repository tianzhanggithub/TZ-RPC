package tzrpc.framework.common.util;

import lombok.extern.slf4j.Slf4j;
import tzrpc.framework.common.exception.TzrpcException;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Slf4j
public class NetUtil{

    public static String getIp() {
        return getLocalIp();
    }

    private static String getLocalIp() {
        String localIp = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        localIp = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log.error("NetUtil.getLocalIp --> 获取本机局域网 IP 失败; ", e);
            throw new TzrpcException("net fail", "获取本机局域网 IP 失败");
        }
        return localIp;
    }
}
