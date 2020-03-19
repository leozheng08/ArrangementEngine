package cn.tongdun.kunpeng.api.infrastructure.config;

import cn.tongdun.kunpeng.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.common.util.NetWorkUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalEnvironment implements ILocalEnvironment{

    private static final Logger logger = LoggerFactory.getLogger(LocalEnvironment.class);

    @Value("${CLUSTER}")
    private String cluster;

    @Value("${ENV}")
    private String env;

    @Value("${APPNAME}")
    private String appName;

    @Value("${APP_PORT}")
    private int appPort;

    @Value("${DC}")
    private String dc;

    //当前租户名称
    @Value("${tenant:default}")
    private String tenant;

    private String ip;

    @Value("${cluster.poc.name:}")
    private String pocClusterName;

    @Override
    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    @Override
    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getAppPort() {
        return appPort;
    }

    public void setAppPort(int appPort) {
        this.appPort = appPort;
    }

    @Override
    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    @Override
    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Override
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean isPocCluster() {
        return StringUtils.equals(cluster,pocClusterName);
    }

    public String getPocClusterName() {
        return pocClusterName;
    }

    public void setPocClusterName(String pocClusterName) {
        this.pocClusterName = pocClusterName;
    }

    public void init() {
        try {
            ip = obtainLocalIpAddress();
        } catch (Exception e) {
            logger.error("获取本机ip出现异常", e);
        }
    }

    private String obtainLocalIpAddress(){
        String localIpAddress = NetWorkUtil.getLANIP("127.0.0.1");
        return localIpAddress;
    }

    @Override
    public String toString() {
        return "LocalEnvironment{" +
                "cluster='" + cluster + '\'' +
                ", env='" + env + '\'' +
                ", appName='" + appName + '\'' +
                ", appPort=" + appPort +
                ", dc='" + dc + '\'' +
                ", ip='" + ip + '\'' +
                ", pocClusterName='" + pocClusterName + '\'' +
                '}';
    }


    /**
     * 是否为线下环境
     * @return
     */
    @Override
    public boolean isInternalEnvironment() {
        if(StringUtils.isBlank(env)){
            return true;
        }
        if (StringUtils.equalsIgnoreCase(env, "internal")) {
            return true;
        }
        if (StringUtils.equalsIgnoreCase(env, "test")) {
            return true;
        }
        if (StringUtils.equalsIgnoreCase(env, "dev")) {
            return true;
        }
        if (StringUtils.equalsIgnoreCase(env, "smoke")) {
            return true;
        }
        if (StringUtils.equalsIgnoreCase(env, "dev-common")) {
            return true;
        }
        if (StringUtils.equalsIgnoreCase(env, "test-common")) {
            return true;
        }
        return false;
    }

}
