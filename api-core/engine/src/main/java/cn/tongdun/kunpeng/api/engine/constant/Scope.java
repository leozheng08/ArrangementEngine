package cn.tongdun.kunpeng.api.engine.constant;

public enum Scope {

    /**
     * Application scope
     */
    APPLICATION("本应用"),

    /**
     * Partner scope
     */
    PARTNER("合作方"),

    /**
     * Global scope
     */
    GLOBAL("全局"),
    /**
     * Global Except partner scope
     */
    GLOBALEXCEPTPARTNER("全局(本合作方除外)");

    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    Scope(String displayName){
        this.displayName = displayName;
    }
}
