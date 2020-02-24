package cn.tongdun.kunpeng.common.data;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.common.util.KunpengStringUtils;
import cn.tongdun.tdframework.common.extension.IBizScenario;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.google.common.collect.Sets;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public abstract class AbstractFraudContext implements Serializable, ExecuteContext {

    private static final long serialVersionUID = -3320502733559293390L;
    private static final Field[] fields = AbstractFraudContext.class.getDeclaredFields();
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


    private String accountLogin;            //VelocityHelper NameList function

    private String accountMobile;           //AddressMatch function
    private String idNumber;                //AddressMatch function

    public String getIpAddress() {
        Object result = get("ipAddress");
        if (result == null) {
            result = get("ipAddr");
        }
        return (String) result;
    }      //Proxy function


    private IBizScenario bizScenario;

    private String policyUuid;
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

    private String simulatePartnerCode;
    private String simulateAppName;
    private String simulateSequenceId;

    /**
     * 是否是决策流. 默认否, 默认为策略集.
     */
    private transient boolean isDecisionFlow = false;

    /**
     * 是否是复杂对象查询
     */
    private boolean isObject = false;

    /**
     * 原始请求入参
     */
    private Map<String, String> requestParamsMap;

    /**
     * 规则引擎dubbo泛化输入参数/调用结果放在这里
     */
    private Object dubboCallResult;  // kunta的调用结果

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

    // 决策流三方接口输出变量配置决策接口直接输出的决策引擎字段
    private List<Map<String, Object>> interfaceOutputFields = new ArrayList<>();

    // 决策流模型输出变量配置决策接口直接输出的决策引擎字段
    private List<Map<String, Object>> modelOutputFields = new ArrayList<>();

    /**
     * 策略指标
     * Key: policyIndexUuid
     */
    private Map<String, Double> policyIndexMap = new ConcurrentHashMap<>(20);

    /**
     * Key: IndicatrixId，指标ID(指标平台的指标ID)
     * Value：指标的相关信息，含值、原始值、描述、详情等
     */
    private Map<String, PlatformIndexData> platformIndexMap = new ConcurrentHashMap<>(50);
    /**
     * 规则引擎运行过程中命中的详情信息，结果只是详情的一个lamba函数，需要调用一次才能获取到真正的数据
     * ruleUuid->ruleConditionUuid->DetailCallable.
     * 注意：该命中详情不包含平台指标的详情,只有function运行过程中产生的详情。
     * 没有使用guava Table的原因：线程不安全。
     */
    protected Map<String, Map<String, DetailCallable>> functionHitDetail = new ConcurrentHashMap<>(50);

    private transient PolicyResponse policyResponse;


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

    public void addSubReasonCode(SubReasonCode subReasonCode) {
        this.subReasonCodes.add(subReasonCode);
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
            String methodName = "get" + KunpengStringUtils.upperCaseFirstChar(key);
            try {
                Method method = this.getClass().getMethod(methodName);
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
                String methodName = "set" + KunpengStringUtils.upperCaseFirstChar(key);
                Method method = this.getClass().getDeclaredMethod(methodName, o.getClass());
                method.invoke(this, o);
            } catch (Exception ex) {
                // 没有找到系统字段
            }
        }

        // 设置自定义系统字段
        this.systemFiels.put(key, o);
    }

    public void appendModelOutputFields(Map<String, Object> modelOutputFields) {
        if (interfaceOutputFields != null) {
            this.modelOutputFields.add(modelOutputFields);
        }
    }

    public List<Map<String, Object>> getInterfaceOutputFields() {
        return interfaceOutputFields;
    }

    public void setInterfaceOutputFields(List<Map<String, Object>> interfaceOutputFields) {
        this.interfaceOutputFields = interfaceOutputFields;
    }

    public void appendInterfaceOutputFields(Map<String, Object> interfaceOutputFields) {
        if (interfaceOutputFields != null) {
            this.interfaceOutputFields.add(interfaceOutputFields);
        }
    }

    @Override
    public Double getPolicyIndex(String policyIndexUuid) {
        if (null == policyIndexMap) {
            return null;
        }
        return policyIndexMap.get(policyIndexUuid);
    }

    public void putPolicyIndex(String policyIndexUuid, Double indexValue) {
        policyIndexMap.put(policyIndexUuid, indexValue);
    }

    /**
     * @param platformIndexId
     * @return
     */
    @Override
    public Double getPlatformIndex(String platformIndexId) {
        if (StringUtils.isBlank(platformIndexId)) {
            return null;
        }
        PlatformIndexData platformIndexData = platformIndexMap.get(platformIndexId);
        if (null == platformIndexData) {
            return null;
        }
        return platformIndexData.getValue();
    }

    @Override
    public Double getOriginPlatformIndex(String platformIndexId) {
        if (StringUtils.isBlank(platformIndexId)) {
            return null;
        }
        PlatformIndexData platformIndexData = platformIndexMap.get(platformIndexId);
        if (null == platformIndexData) {
            return null;
        }
        return platformIndexData.getOriginalValue();
    }

    /**
     * 删除没有命中的规则的详情
     *
     * @param ruleUuid
     */
    public void removeFunctionDetail(String ruleUuid) {
        this.functionHitDetail.remove(ruleUuid);
    }
}
