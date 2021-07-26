package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CacheKeyGenerator {

    /**
     * scope(适用范围) -> fieldName(字段名) -> WrappedGroovyObject(包含编译后groovy对象)
     * scope(适用范围)包含：
     * 全局全部应用事件类型:   "all" + "all" + "all";
     * 全局全部应用指定事件类型:   "all" + "all" + context.getEventType();
     * 全局全部合作方指定应用全部事件类型:   "all" + "context.getAppName" + "all";
     * 局全部合作全部应用指定应用指定事件类型:   "all" + "context.getAppName" + context.getEventType();
     * 指定合作方全部应用全部事件类型  context.getPartnerCode() + "all" + "all"
     * 指定合作方全部应用指定事件类型  context.getPartnerCode() + "all" + context.getEventType();
     * 指定合作方指定应用全部事件类型  context.getPartnerCode() +"context.getAppName" + "all";
     * 指定合作方指定应用指定事件类型  context.getPartnerCode() +"context.getAppName" + context.getEventType();
     *
     * @param wrappedGroovyObject
     * @return
     */

    public static List<String> getkey(WrappedGroovyObject wrappedGroovyObject) {
        List<String> keys = new ArrayList<>();
        if (Objects.nonNull(wrappedGroovyObject)) {
            /**
             * 全部合作方全部事件类型:   "all" + "all";
             */
            if ("all".equals(wrappedGroovyObject.getPartnerCode()) &&
                    "all".equals(wrappedGroovyObject.getEventType())

            ) {
                String key = "all-" + "all";
                keys.add(key);
                return keys;
            }
            /**
             * 全部合作方指定事件类型:   "all" +  context.getEventType();
             */
            if ("all".equals(wrappedGroovyObject.getPartnerCode()) &&
                    !"all".equals(wrappedGroovyObject.getEventType())

            ) {
                String[] eventTypes = StringUtils.split(wrappedGroovyObject.getEventType(), ",");
                for (String retval : eventTypes) {
                    String key = "all-" + retval;
                    keys.add(key);
                }
                return keys;
            }


            /**
             * 指定合作方全部事件类型  context.getPartnerCode() + "all"
             */
            if (!"all".equals(wrappedGroovyObject.getPartnerCode()) &&
                    "all".equals(wrappedGroovyObject.getEventType())

            ) {
                String[] partnerCodes = StringUtils.split(wrappedGroovyObject.getPartnerCode(), ",");
                for (String retval : partnerCodes) {
                    String key = retval + "-all";
                    keys.add(key);
                }
                return keys;
            }

            /**
             * 指定合作方指定事件类型  context.getPartnerCode() + context.getEventType();
             */
            if (!"all".equals(wrappedGroovyObject.getPartnerCode()) &&
                    !"all".equals(wrappedGroovyObject.getEventType())

            ) {
                String[] partnerCodes = StringUtils.split(wrappedGroovyObject.getPartnerCode(), ",");
                String[] eventTypes = StringUtils.split(wrappedGroovyObject.getEventType(), ",");
                for (String retval : partnerCodes) {
                    for (String eventType : eventTypes) {
                        String key = retval + "-" + eventType;
                        keys.add(key);
                    }
                }
                return keys;
            }


        }

        return keys;
    }
}
