package com.hitol.rpc.framework.util;

import java.net.InetAddress;

public class IPUtil {

    public static String localIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {

        }
        return "127.0.0.1";

    }

}
