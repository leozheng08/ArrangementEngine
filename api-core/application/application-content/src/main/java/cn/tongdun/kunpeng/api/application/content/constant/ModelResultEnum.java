package cn.tongdun.kunpeng.api.application.content.constant;



public enum ModelResultEnum {

    BRAND_LOGO_RECOGNITION_MODEL("品牌识别模型","image_brand_logo_model_result"),
    LOGO_RECOGNITION_MODEL("LOGO识别模型","image_logo_model_result"),
    SPR_POLITICAL_MODEL("涉政模型", "image_politics_model_result"),
    VIOLENT_TERROR_MODEL("暴恐模型","image_terror_model_result")
    ;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String desc;
    private String name;

    ModelResultEnum(String desc, String name){
        this.desc = desc;
        this.name = name;
    }

}
