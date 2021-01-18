package cn.tongdun.kunpeng.api.engine.model.dictionary;

/**
 * @Author: liuq
 * @Date: 2020/5/26 10:18 上午
 */
public enum DictionaryEnum {

    /**
     * dict中的字段描述
     */
    StarkDeviceResult("设备指纹结果获取类型"),

    MailParamKey("租户传递邮箱字段key"),

    SwitchOnKafkaKey("是否发送kafka数据key"),

    PhoneSwitch("是否调用手机号三方接口key"),

    SubReasonCodeCacheData("三方状态码配置");


    private String desc;
    DictionaryEnum(String desc){
        this.desc=desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
