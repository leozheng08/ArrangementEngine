package cn.tongdun.kunpeng.api.engine.fp;

import java.util.Map;

public interface FpClient {

    ContainCheatingApps getContainCheatingApps(Map<String, Object> deviceInfo, String platform);
}
