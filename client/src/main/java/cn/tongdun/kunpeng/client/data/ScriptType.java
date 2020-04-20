package cn.tongdun.kunpeng.client.data;

/**
 * @Author: changkai.yang
 * @Date: 2020/4/20 下午4:08
 */
public enum ScriptType {
    groovy("groovy", "groovy脚本");

    private String type;
    private String desc;

    ScriptType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean isValid(String type){
        ScriptType scriptType = ScriptType.valueOf(type);
        return scriptType != null;
    }
}
