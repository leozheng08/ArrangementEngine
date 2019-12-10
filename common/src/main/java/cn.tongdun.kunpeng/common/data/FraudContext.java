package cn.tongdun.kunpeng.common.data;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.google.common.collect.Sets;
import lombok.Data;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class FraudContext implements Serializable, Cloneable {

    private static final long serialVersionUID = -3320502733559293390L;
    private static final Field[] fields = FraudContext.class.getDeclaredFields();
    private static final Set<String> fieldNames = new HashSet<>(fields.length / 3);
    private static final String IP_ADDRESS_DOMESTIC = "ipAddress";
    private static final String IP_ADDRESS_OVERSEA = "ipAddr";

    static {
        Set<String> includeTypes = Sets.newHashSet("int", "integer", "string", "double", "long", "float", "date", "boolean");
        for (Field field : fields) {
            String simTypeName = field.getType().getSimpleName();
            if (includeTypes.contains(simTypeName.toLowerCase())) {
                fieldNames.add(field.getName());
            }
        }
    }

    /**
     * 服务类型。比如基础版avalon，信贷云creditcloud
     */
    private String serviceType;
    private String partnerCode;
    private String appName;
    private String appType;
    private String secretKey;
    private String eventId;
    private String eventType;
    private String sequenceId;
    private String requestId;
    private Date eventOccurTime;
    private boolean testFlag = false;
    private long requestTraceStartTime;//trace开始时间
    private transient boolean async = false;

    /**
     * 规则仿真
     */
    private boolean simulation = false;

    /**
     * 507、508的重试调用
     */
    private boolean recall = false;

    /**
     * 是否是决策流. 默认否, 默认为策略集.
     */
    private transient boolean isDecisionFlow = false;



    /**
     * 原始请求入参
     */
    private Map<String, String> requestParamsMap;

    /**
     * 系统字段
     */
    private Map<String, Object> systemFiels = new ConcurrentHashMap<>();

    /**
     * 扩展字段
     */
    private Map<String, Object> customFields = new ConcurrentHashMap<String, Object>();

    /**
     * 异常子码
     */
    private ConcurrentHashSet<SubReasonCode> subReasonCodes = new ConcurrentHashSet();

    private Map<String, Object> deviceInfo = new HashMap<String, Object>();

    /**
     * context业务数据
     */
    private Map<String, Object> bizData = new ConcurrentHashMap<String, Object>();



    public void addSubReasonCode(SubReasonCode subReasonCode, SubReasonCode.ExtCode extCode) {
        boolean contain = false;
        for (SubReasonCode src : this.subReasonCodes) {
            if (src.equals(subReasonCode)) {
                if (null != src.getExt_code() && null != extCode) {
                    if (!src.getExt_code().contains(extCode)) {
                        src.getExt_code().add(extCode);
                    }
                }
                contain = true;
                break;
            }
        }
        if (!contain) {
            if (null != subReasonCode && null != extCode) {
                subReasonCode.addExtCode(extCode);
                this.subReasonCodes.add(subReasonCode);
            }
        }
    }


    /**
     * 查询顺序是：FraudContext里的系统字段Map-》设备指纹Map-》扩展字段-》Map属性字段
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        if (key == null) {
            return null;
        }

        if ("null".equals(key)) {
            return "null";
        }

        Object sysVal = systemFiels.get(key);
        if (sysVal != null) {
            return sysVal;
        }

        if (null != deviceInfo) {
            Object val = deviceInfo.get(key);
            if (val != null) {
                return val;
            }
        }

        Object customVal = customFields.get(key);
        if (customVal != null) {
            return customVal;
        }

        if (fieldNames.contains(key)) {
            String methodName = "get" + cn.tongdun.kunpeng.common.util.StringUtils.upperCaseFirstChar(key);
            try {
                Method method = this.getClass().getDeclaredMethod(methodName);
                Object value = method.invoke(this);
                if (value != null) {
                    return value;
                }
            } catch (Exception e) {
                // ignore
            }
        }

        return null;
    }

    // 向FraudContext里的属性字段赋值
    public void setFieldValue(String key, Object value) {
        if (StringUtils.isBlank(key) || null == value) {
            return;
        }
        if (key.startsWith("ext_")) {// 扩展字段
            this.customFields.put(key, value);
        } else { // 系统字段
            this.systemFiels.put(key, value);
        }
    }

    public void set(String key, Object o) {
        if (StringUtils.isBlank(key) || null == o) {
            return;
        }

        if (fieldNames.contains(key)) {
            try {
                String methodName = "set" + cn.tongdun.kunpeng.common.util.StringUtils.upperCaseFirstChar(key);
                Method method = this.getClass().getDeclaredMethod(methodName, o.getClass());
                method.invoke(this, o);
            } catch (Exception ex) {
                // 没有找到系统字段
            }
        }

        // 设置自定义系统字段
        this.systemFiels.put(key, o);
    }

    public FraudContext shallowCopy() {
        try {
            return (FraudContext) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone", e);
        }
    }

    public FraudContext deepCopy() {
        return SerializationUtils.clone(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }




}
