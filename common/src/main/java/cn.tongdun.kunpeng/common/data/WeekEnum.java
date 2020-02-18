package cn.tongdun.kunpeng.common.data;

/**
 * Created by coco on 17/3/22.
 */
public enum WeekEnum {
    星期一("Monday"),星期二("Tuesday"),星期三("Wednesday"),星期四("Thursday"),星期五("Friday"),星期六("Saturday"),星期日("Sunday");

    WeekEnum(String weekEn) {
        this.weekEn = weekEn;
    }

    private String weekEn;

    public String getWeekEn() {
        return weekEn;
    }

    public void setWeekEn(String weekEn) {
        this.weekEn = weekEn;
    }

    public static boolean containKey(String week) {
        for (WeekEnum weekEnum : WeekEnum.values()) {
            if (weekEnum.weekEn.toLowerCase().contains(week.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public static String getWeekZh(String weekEn) {
        for (WeekEnum weekEnum : WeekEnum.values()) {
            if (weekEnum.weekEn.toLowerCase().contains(weekEn.toLowerCase())){
                return weekEnum.name();
            }
        }
        return "星期八";
    }
}
