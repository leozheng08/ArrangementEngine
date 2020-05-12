package cn.tongdun.kunpeng.api.common.config;

/**
 * 当前环境信息配置信息
 */
public interface ILocalEnvironment {

    //集群名
    public String getCluster();

    //环境名,包含：dev,dev-common,smoke,test,test-common,staging,production,sandbox
    public String getEnv();

    //应用名
    public String getAppName();

    //机房，如SH,HZ,IDNU
    public String getDc();

    //本机ip
    public String getIp();

    //是否为线下环境，线下环境包含dev,dev-common,smoke,test,test-common
    public boolean isInternalEnvironment();

    //是否为poc集群
    public boolean isPocCluster();

    //当前租户名称
    public String getTenant();
}
