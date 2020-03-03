package cn.tongdun.kunpeng.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.util.IPAddressUtil;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网路有关的工具类
 * Created by chenchanglong on 16/6/21.
 */
public class NetWorkUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetWorkUtil.class);

    /**
     * 获取设置的局域网IP，获取失败返回默认IP
     */
    public static final String getLANIP(String defaultIP){
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                if (ni.isLoopback()) {
                    continue;
                }
                if (!ni.isUp()) {
                    continue;
                }
                if (ni.isVirtual()) {
                    continue;
                }
                while (ips.hasMoreElements()) {
                    InetAddress inetAddress = ips.nextElement();
                    String hostAddress = inetAddress.getHostAddress();
                    if (IPAddressUtil.isIPv6LiteralAddress(hostAddress)) {
                        continue;
                    } else {
                        return hostAddress;
                    }
                }
            }
        } catch (SocketException e) {
            logger.error(e.getMessage());
        }

        return defaultIP;
    }
}
