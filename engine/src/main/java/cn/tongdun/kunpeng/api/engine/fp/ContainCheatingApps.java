package cn.tongdun.kunpeng.api.engine.fp;

import java.util.List;

public class ContainCheatingApps {

    private List<Anomaly> installedDangerApps;
    private List<Anomaly> runningDangerApps;

    public ContainCheatingApps() {
    }

    public List<Anomaly> getInstalledDangerApps() {
        return this.installedDangerApps;
    }

    public void setInstalledDangerApps(List<Anomaly> installedDangerApps) {
        this.installedDangerApps = installedDangerApps;
    }

    public List<Anomaly> getRunningDangerApps() {
        return this.runningDangerApps;
    }

    public void setRunningDangerApps(List<Anomaly> runningDangerApps) {
        this.runningDangerApps = runningDangerApps;
    }
}
