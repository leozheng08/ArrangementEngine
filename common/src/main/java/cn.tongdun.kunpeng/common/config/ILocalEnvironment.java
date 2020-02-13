package cn.tongdun.kunpeng.common.config;

/**
 * 当前环境信息
 */
public interface ILocalEnvironment {

    public String getCluster();
    public String getEnv();
    public String getAppName();
    public String getDc();
    public String getIp();
    public boolean isInternalEnvironment();
}
