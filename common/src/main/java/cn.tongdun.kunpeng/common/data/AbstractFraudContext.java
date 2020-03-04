package cn.tongdun.kunpeng.common.data;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.client.data.RiskRequest;
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
import java.util.function.Supplier;

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

    /*************系统级入参（由合作方传的参数，但不需要字段管理中定义） start******************/
    /**
     * 合作方编码，必传
     */
    private String partnerCode;

    /**
     * app的secretKey，必传
     */
    private String secretKey;

    /**
     * 事件id，必传
     */
    private String eventId;

    /**
     * 服务类型。比如基础版avalon，信贷云creditcloud,默认服务类型是专业版professional
     */
    private String serviceType;

    /**
     * 交易流水
     */
    private String seqId;

    /**
     * 链路跟踪相关的requestId
     */
    private String requestId;

    /**
     * 事件发生时间，如果合作方有传，以合作方为准，如果没传则取seqId中的时间戳
     */
    private Date eventOccurTime;

    /**
     * 测试标记
     */
    private boolean testFlag = false;

    /**
     * 如果async=true时，规则引擎运行不设超时时间。
     */
    private transient boolean async = false;

    /**
     * 返回详细类型，包含有：application_id,geoip,device,device_all,attribution,hit_rule_detail,hit_rule_detail_v3,credit_score。可以指定去掉不需要返回的内容：-hit_rules,-policy_set_name,-policy_set,-policy_name,-output_fields
     */
    private String respDetailType;

    /**
     * 507、508的重试调用
     */
    private boolean recall = false;

    /**
     * 507、508的重试调用对应的原seqId
     */
    private String recallSeqId;



    /**
     *  仿真的合作方编码
     */
    private String simulationPartner;

    /**
     * 仿真的appName
     */
    private String simulationApp;

    /**
     * 仿真的seqId
     */
    private String simulationSeqId;
    private String simulationUuid;
    private String tdSampleDataId;

    /*************系统级入参 end******************/


    /**
     * 根据合作方传的secretKey，从缓存中取得app_name
     */
//    private String appName;


    /**
     * 当前调用的策略uuid,根据合作方传的三要素partner_code,event_id,secret_key取得策略的运行版本的uuid，
     * 或根据再加个policy_version四要素取得策略版本的uuid
     */
    private String policyUuid;

    /**
     * 根据app_name，从缓存取得appType
     */
//    private String appType;

    /**
     * 根据eventId，从缓存取得eventType
     */
    private String eventType;

    /**
     * 业务场景，用于按tenant、business、partnerCode三个维度实现扩展点
     */
    private IBizScenario bizScenario;

    /**
     * 是否是决策流. 默认否, 默认为策略集.
     */
    private transient boolean isDecisionFlow = false;

    /**
     * 规则仿真
     */
    private boolean simulation = false;

    /**
     * 是否是复杂对象查询
     */
    private boolean isObject = false;

    /**
     * 原始请求入参Map
     */
    private Map<String, String> requestParamsMap;

    private RiskRequest riskRequest;

    /**
     * 本次请求适用的字段列表
     */
    private List<IFieldDefinition> fieldDefinitions = new ArrayList<>();

    /**
     * 系统字段
     */
    private Map<String, Object> systemFields = new ConcurrentHashMap<>();

    /**
     * 扩展字段
     */
    private Map<String, Object> customFields = new ConcurrentHashMap<String, Object>();

    /**
     * 异常子码
     */
    private ConcurrentHashSet<SubReasonCode> subReasonCodes = new ConcurrentHashSet();

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

    /**
     * 策略运行结果，信息较为完整，决策结果应答时会再从此对象选取部分输出到RiskResponse中
     */
    private transient PolicyResponse policyResponse;


    /*************外部接口返回结果 start******************/

    /**
     * 调外部接口返回的结果
     */
    private Map<String,Object> externalReturnObj = new ConcurrentHashMap<>() ;

    private Map<String,Supplier> externalFieldValues = new ConcurrentHashMap<>();

    /**
     * 外部的服务实例
     */
    private Map<String,Object> externalService = new ConcurrentHashMap<>() ;

    /**
     * 规则引擎dubbo泛化输入参数/调用结果放在这里
     */
    private Object dubboCallResult;  // kunta的调用结果

    /**
     * 决策流三方接口输出变量配置决策接口直接输出的决策引擎字段
     */
    private List<Map<String, Object>> interfaceOutputFields = new ArrayList<>();

    /**
     * 决策流模型输出变量配置决策接口直接输出的决策引擎字段
     */
    private List<Map<String, Object>> modelOutputFields = new ArrayList<>();

    /**
     * 设备指纹信息
     */
    private Map<String, Object> deviceInfo = new HashMap<String, Object>();






    /*************外部接口返回结果 end******************/


    /**
     * 添加异常子码及对应外部系统的原因码
     * @param subReasonCode
     * @param extCode
     */
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
     * 添加异常子码
     * @param subReasonCode
     */
    public void addSubReasonCode(SubReasonCode subReasonCode) {
        this.subReasonCodes.add(subReasonCode);
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

        setField(key,o);
    }

    // 向FraudContext里的属性字段赋值
    @Override
    public void setField(String key, Object value) {
        if (StringUtils.isBlank(key) || null == value) {
            return;
        }
        if (key.startsWith("ext_")) {// 扩展字段
            this.customFields.put(key, value);
        } else { // 系统字段
            this.systemFields.put(key, value);
        }
    }

    /**
     * 查询顺序是：FraudContext里的系统字段Map-》扩展字段-》Map属性字段-》设备指纹Map
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

        Object sysVal = systemFields.get(key);
        if (sysVal != null) {
            return sysVal;
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

        if (null != deviceInfo) {
            Object val = deviceInfo.get(key);
            if (val != null) {
                return val;
            }
        }

        return getExternalFieldValues(key);
    }

    public String getFieldToString(String key){
        Object value = get(key);
        if(value == null){
            return null;
        }
        return value.toString();
    }


    @Override
    public Object getField(String name) {
        return get(name);
    }

    /**
     * 取得策略指标的结果值
     * @param policyIndexUuid
     * @return
     */
    @Override
    public Double getPolicyIndex(String policyIndexUuid) {
        if (null == policyIndexMap) {
            return null;
        }
        return policyIndexMap.get(policyIndexUuid);
    }

    /**
     * 设置策略指标的结果值
     * @param policyIndexUuid
     * @param indexValue
     */
    public void putPolicyIndex(String policyIndexUuid, Double indexValue) {
        policyIndexMap.put(policyIndexUuid, indexValue);
    }

    /**
     * 取得平台指标结果值
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

    /**
     * 取得平台指标原始结果值
     * @param platformIndexId
     * @return
     */
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


    /**
     * 规则执行过程中的用于保存由function产生的详情，不含指标平台产生的详情。
     * 目前分线程执行只在子策略的维度，在没有通用规则之前都是线程安全的，因为一个规则只会属于一个子策略。
     *
     * @param ruleUuid
     * @param conditionUuid
     * @param detailCallable
     */
    @Override
    public void saveDetail(String ruleUuid, String conditionUuid, DetailCallable detailCallable) {
        Map<String, DetailCallable> conditionMap = functionHitDetail.get(ruleUuid);
        if (null == conditionMap) {
            conditionMap = new ConcurrentHashMap<>(2);
            functionHitDetail.put(ruleUuid, conditionMap);
        }
        conditionMap.put(conditionUuid, detailCallable);
    }



    public void addExternalObj(String key,Object obj){
        externalReturnObj.put(key,obj);
    }

    public <T> T getExternalReturnObj(String key, Class<T> calss){
        return (T) externalReturnObj.get(key);
    }

    public Object getExternalFieldValues(String key){
        Supplier supplier = externalFieldValues.get(key);
        if(supplier == null){
            return null;
        }
        return supplier.get();
    }

    public void addExternalService(String key,Object obj){
        externalService.put(key,obj);
    }

    public <T> T getExternalService(String key,Class<T> calss){
        return (T)externalService.get(key);
    }


    public String getIpAddress() {
        Object result = get("ipAddress");
        if (result == null) {
            result = get("ipAddr");
        }
        return (String) result;
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

}
