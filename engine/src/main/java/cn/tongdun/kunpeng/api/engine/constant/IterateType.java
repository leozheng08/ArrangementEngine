package cn.tongdun.kunpeng.api.engine.constant;

/**
 * @Author: liang.chen
 * @Date: 2020/4/17 下午2:42
 */
public enum IterateType {

    ANY("任一"),
    ALL("全部");

    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    IterateType(String displayName){
        this.displayName = displayName;
    }
}