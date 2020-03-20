package cn.tongdun.kunpeng.api.engine.model.constant;

/**
 * @Author: liang.chen
 * @Date: 2020/3/13 下午6:04
 */
public enum DomainEventTypeEnum {


    BATCH_REMOVE("批量删除"),
    BATCH_CREATE("批量创建"),
    BATCH_UPDATE("批量修改"),
    BATCH_ACTIVATE("批量激活"),
    BATCH_DEACTIVATE("批量关闭"),

    CREATE("创建"),
    IMPORT("导入"),
    UPDATE("修改"),
    ACTIVATE("激活"),
    DEACTIVATE("关闭"),
    REMOVE("删除"),
    RECOVER("恢复"),
    SUSPEND("暂停"),
    TERMINATE("停止"),
    SORT("排序"),
    SWITCH_DECISION_MODE("切换决策模式");



    private String desc;

    DomainEventTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
