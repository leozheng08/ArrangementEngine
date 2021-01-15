package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.tongdun.ddd.common.exception.BasicErrorCode;
import cn.tongdun.ddd.common.exception.BizException;
import cn.tongdun.kunpeng.api.common.MetricsConstant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.api.common.util.KunpengStringUtils;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import cn.tongdun.tdframework.core.metrics.ITimeContext;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSONPath;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author jie
 * @date 2021/1/13
 */
@Service("genericDubboCaller")
public class GenericDubboCaller implements IGenericDubboCaller{

    private static final Logger logger = LoggerFactory.getLogger(GenericDubboCaller.class);

    private String CONFIG_KEY_TYPE = "type";
    private String CONFIG_KEY_NAME = "name";

    @Autowired
    private InterfaceDefinitionCache interfaceDefinitionCache;

    @Autowired
    private GenericServiceManager genericServiceManager;

    @Autowired
    private IMetrics prometheusMetricsImpl;

    @Override
    public boolean call(AbstractFraudContext fraudContext, DecisionFlowInterface decisionFlowInterface) {
        // 1. 缓存获取接口配置信息
        long beginTime = System.currentTimeMillis();
        InterfaceDefinition interfaceDefinition = interfaceDefinitionCache.get(decisionFlowInterface.getUuid());
        if (null == interfaceDefinition) {// TODO isValid()
            logger.warn("decisionFlowInterface call of uuid:{} interface cache is null",
                        decisionFlowInterface.getUuid());
            return false;
        }

        Object result = null;
        String providerHost = StringUtils.defaultIfBlank(RpcContext.getContext().getRemoteHost(), "-");
        ITimeContext timeContext = null;
        String[] tags = {
                MetricsConstant.METRICS_TAG_API_QPS_KEY, interfaceDefinition.getServiceName() + "." + interfaceDefinition.getMethodName()};
        try {
            // 2. 根据决策流配置信息解析入参映射并设置规则字段值,并做必填校验
            Map<String, Object> mappingParamAndValue = assignInputParamValue(fraudContext, decisionFlowInterface);

            // 3. 解析三方接口的参数类型数组及值对象数组
            DecisionFlowInterfaceCallInfo interfaceCallInfo = encapsulateDubboCallParam(mappingParamAndValue,
                                                                                        interfaceDefinition);
            // 4. 发起泛化调用
            final GenericService genericService = genericServiceManager.getGenericService(interfaceDefinition);

            // 5. 监控打点
            prometheusMetricsImpl.counter(MetricsConstant.METRICS_API_QPS_KEY, tags);
            timeContext = prometheusMetricsImpl.metricTimer(MetricsConstant.METRICS_API_RT_KEY, tags);

            logger.info(TraceUtils.getFormatTrace()
                            + "dubbo泛化调用开始 interface_method:{}-{},interfaceParamInfos:{} , DecisionFlowInterfaceCallInfo:{}",
                    interfaceDefinition.getName(), interfaceDefinition.getMethodName(),
                    Arrays.toString(decisionFlowInterface.getInputParams().toArray()), interfaceCallInfo);
            result = genericService.$invoke(interfaceDefinition.getMethodName(), interfaceCallInfo.getInputParamType(),
                                            interfaceCallInfo.getInputParamValue());
            timeContext.stop();

            logger.info(TraceUtils.getFormatTrace()
                            + "dubbo泛化调用结束 interface_method:{}-{},interfaceParamInfos:{} , DecisionFlowInterfaceCallInfo:{}",
                    interfaceDefinition.getName(), interfaceDefinition.getMethodName(),
                    Arrays.toString(decisionFlowInterface.getInputParams().toArray()), interfaceCallInfo);
        } catch (BizException e) {
            logger.warn(TraceUtils.getFormatTrace() + "generic dubbo call method:{}, provider:{} bizExcption", interfaceDefinition.getName(), providerHost, e);
            result = APIResult.DUBBO_API_RESULT_MISSING_PARAMETER;
            return false;
        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                prometheusMetricsImpl.counter(MetricsConstant.METRICS_API_TIMEOUT_KEY,tags);
                result = APIResult.DUBBO_API_RESULT_TIMEOUT;
            } else if (e.getClass() == com.alibaba.dubbo.rpc.RpcException.class) {
                prometheusMetricsImpl.counter(MetricsConstant.METRICS_API_CALL_ERROR_KEY,tags);
                result = APIResult.DUBBO_API_RESULT_EXTERNAL_CALL_ERROR;
            } else {
                prometheusMetricsImpl.counter(MetricsConstant.METRICS_API_INTERNAL_ERROR_KEY,tags);
                result = APIResult.DUBBO_API_RESULT_INTERNAL_ERROR;
            }
            logger.warn(TraceUtils.getFormatTrace() + "generic dubbo call method:{}, provider:{} catch exception result:{}", interfaceDefinition.getName(), providerHost, JSON.toJSONString(result), e);
            return false;
        } finally {
            // 6. 解析结果并处理异常
            handleResult(fraudContext, result, interfaceDefinition, decisionFlowInterface, tags);
            logger.info(TraceUtils.getFormatTrace()
                            + "dubbo泛化调用:{}-{},interfaceParamInfos:{} , end cost:{}",
                    interfaceDefinition.getName(), interfaceDefinition.getMethodName(),
                    Arrays.toString(decisionFlowInterface.getInputParams().toArray()), (System.currentTimeMillis() - beginTime));
        }
        return true;
    }

    /**
     * 集中处理dubbo返回结果
     * @param fraudContext
     * @param result
     * @param interfaceDefinition
     * @param decisionFlowInterface
     * @param tags
     */
    private void handleResult(AbstractFraudContext fraudContext, Object result, InterfaceDefinition interfaceDefinition,
                              DecisionFlowInterface decisionFlowInterface, String[] tags) {
        try {
            // 包装转换结果
            Map<String, Object> resultMap = wrapResult(result);

            // 三方地址服务状态码处理
            String serviceName = interfaceDefinition.getServiceName();

            if (!BooleanUtils.toBoolean(JsonUtil.getBoolean(resultMap,"success"))) {
                if (result == APIResult.DUBBO_API_RESULT_TIMEOUT
                    || result == APIResult.DUBBO_API_RESULT_EXTERNAL_CALL_ERROR) {
                    if (StringUtils.equals(serviceName, "cn.fraudmetrix.fuzzy.Service.intf.IAddressService")
                        || StringUtils.equals(serviceName, "cn.fraudmetrix.fuzzy.Service.intf.ICommunityService")) {
                        ReasonCodeUtil.add(fraudContext, ReasonCode.ADDRESS_SERVICE_CALL_TIMEOUT, "watson");
                        logger.info(TraceUtils.getFormatTrace() + "地址服务调用超时:{}-{} timeout:{} 50718",
                                    interfaceDefinition.getName(), interfaceDefinition.getMethodName());
                    } else {
                        ReasonCodeUtil.add(fraudContext, ReasonCode.THIRD_SERVICE_CALL_TIMEOUT, "kunta");
                        logger.info(TraceUtils.getFormatTrace() + "三方调用超时:{}-{} timeout:{} 50707",
                                    interfaceDefinition.getName(), interfaceDefinition.getMethodName(),
                                    interfaceDefinition.getTimeout());
                    }
                } else {
                    /* TODO SubReasonCode subReasonCodeObj = null;
                    // 决策流里融合了地址服务的接口，子状态码和三方子状态码区别开来
                    if (StringUtils.equals(serviceName, "cn.fraudmetrix.fuzzy.Service.intf.IAddressService")
                        || StringUtils.equals(serviceName, "cn.fraudmetrix.fuzzy.Service.intf.ICommunityService")) {
                        subReasonCodeObj = ReasonCodeUtil.addWatson(fraudContext, subReasonCodeCache, resultMap,
                                                                    interfaceName);
                    } else {
                        subReasonCodeObj = ReasonCodeUtil.addKunta(fraudContext, subReasonCodeCache, resultMap,
                                                                   interfaceName);
                    }

                    if (subReasonCodeObj != null && subReasonCodeObj.getSub_code() != null
                        && subReasonCodeObj.getSub_code().startsWith("507")) {
                        prometheusMetricsImpl.counter(MetricsConstant.METRICS_API_BIZ_ERROR_KEY, tags);
                    }*/
                }
            }
            // 输入写入到Context,继而到activity
            fillDataToFraudContext(fraudContext, interfaceDefinition, decisionFlowInterface, resultMap);

            // 三方接口结果按照配置映射输出到上下文字段中
            serializeOutParamsToRuleEngine(fraudContext, decisionFlowInterface, interfaceDefinition, result);
        } catch (Exception e) {
            prometheusMetricsImpl.counter(MetricsConstant.METRICS_API_OTHER_ERROR_KEY, tags);
            logger.error(TraceUtils.getFormatTrace()
                         + "dubbo泛化调用异常 call generic dubbo interface(generate result) error", e);
        }
    }

    /**
     * 根据出参配置将调用结果填装到上下文
     * @param fraudContext
     * @param decisionFlowInterface
     * @param interfaceDefinition
     * @param result
     */
    public void serializeOutParamsToRuleEngine(AbstractFraudContext fraudContext,
                                               DecisionFlowInterface decisionFlowInterface, InterfaceDefinition interfaceDefinition, Object result) {
        if (!(result instanceof Map)) {
            return;
        }

        Map<String, Object> resultMap = (HashMap<String, Object>) result;
        Map<String, Object> resultMapOutput = Maps.newHashMapWithExpectedSize(resultMap.size());

        try {
            for (InterfaceDefinitionParamInfo paramInfo : decisionFlowInterface.getOutputParams()) {
                String ruleParam = paramInfo.getRuleField();
                String interfaceParam = paramInfo.getInterfaceField();
                // 以下两部判断是为了兼容规则引擎定义的下划线形式的字段和kunta返回的驼峰形式
                if ("reason_code".equalsIgnoreCase(interfaceParam)) {
                    interfaceParam = "reasonCode";
                }
                if ("reason_desc".equalsIgnoreCase(interfaceParam)) {
                    interfaceParam = "reasonDesc";
                }
                // 直接拍平路径获取json值
                Object mappingValue = JSONPath.eval(result,interfaceParam);
                resultMapOutput.put(KunpengStringUtils.camel2underline(ruleParam), mappingValue);

                if (mappingValue instanceof Map) {
                    fraudContext.setObject(true);
                }
                fraudContext.setField(ruleParam, mappingValue);
            }

            // 决策流三方接口纯数据输出
            if (decisionFlowInterface.isRiskServiceOutput()) {
                if (StringUtils.isBlank(interfaceDefinition.getInterfaceId())) {
                    resultMapOutput.put("policy_set_output_service", decisionFlowInterface.getUuid());
                } else {
                    resultMapOutput.put("policy_set_output_service", interfaceDefinition.getInterfaceId());
                }
                fraudContext.appendInterfaceOutputFields(resultMapOutput);
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"dubbo泛化调用异常 接口调用的结果赋值给规则引擎字段异常", e);
        }
    }

    /**
     * dubbo 返回结果包装
     * @param result
     * @return
     */
    private Map<String, Object> wrapResult(Object result) {
        Map<String, Object> resultMap = Maps.newHashMap();
        if (result == null) {
            return resultMap;
        }
        if ((result instanceof HashMap)) {
            Map<String, Object> map = (Map) result;
            map.remove("class");
            resultMap.putAll((Map) result);
        } else if (result instanceof APIResult) {
            resultMap = ((APIResult) result).toMap();
        } else if (result instanceof Collections || result.getClass().isArray()) {
            //不支持其它集合或者数组类型
            return resultMap;
        } else if (result instanceof java.lang.String
                || result instanceof java.lang.Integer
                || result instanceof java.lang.Boolean
                || result instanceof java.lang.Double
                || result instanceof java.lang.Float) {
            resultMap.put("result", result);
        } else {        
            //其它dubbo异常情况产生的返回值一律处理成外部服务器错误501
            resultMap = APIResult.DUBBO_API_RESULT_EXTERNAL_CALL_ERROR.toMap();
            logger.info(TraceUtils.getFormatTrace() + "dubbo泛化调用返回501错误");
        }
        //以下两部判断是为了兼容规则引擎定义的下划线形式的字段和kunta返回的驼峰形式
        Object reasonCode = resultMap.remove("reason_code");
        Object reasonDesc = resultMap.remove("reason_desc");
        if (null != reasonCode && null != reasonDesc) {
            resultMap.put("reasonCode", reasonCode);
            resultMap.put("reasonDesc", reasonDesc);
        }
        return resultMap;
    }

    /***
     * 封装dubbo调用结果,并写入到activity
     *
     * @param fraudContext
     * @param interfaceDefinition
     * @param decisionFlowInterface
     * @param resultMap
     */
    private void fillDataToFraudContext(AbstractFraudContext fraudContext,
                                        InterfaceDefinition interfaceDefinition,
                                        DecisionFlowInterface decisionFlowInterface,
                                        Map<String, Object> resultMap) {
        Object object = fraudContext.getDubboCallResult();
        if (object == null) {
            object = new CallResult();
            fraudContext.setDubboCallResult(object);
        }
        //本次调用的dubbo调用结果
        CallResult callResult = (CallResult) object;

        //应用结果
        ApplicationResult applicationResult = callResult.getDubboApplicationResult(interfaceDefinition.getApplication());

        //接口结果
        InterfaceResult interfaceResult = applicationResult.getDubboInterfaceResult(interfaceDefinition.getName());

        //输入/输出参数
        InterfaceParams interfaceParams = new InterfaceParams();

        //输入参数
        Map jsonObject = new HashMap(decisionFlowInterface.getInputParams().size());
        for (InterfaceDefinitionParamInfo paramInfo : decisionFlowInterface.getInputParams()) {
            if ("".equals(paramInfo.getRuleField())) {
                continue;
            }
            jsonObject.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, paramInfo.getRuleField()), paramInfo.getValue());
        }
        interfaceParams.setInputParams(jsonObject);

        //输出参数
        interfaceParams.setOutputParams(resultMap);
        interfaceResult.addInterfaceParams(interfaceDefinition.getMethodName(), interfaceParams);
   }

    /**
     * 通过三方接口定义及映射入参值，组装泛化调用参数
     * @param mappingParamValue
     * @param interfaceDefinition
     * @return
     */
    private DecisionFlowInterfaceCallInfo encapsulateDubboCallParam(Map<String,Object> mappingParamValue,InterfaceDefinition interfaceDefinition) throws BizException{

        if(StringUtils.isEmpty(interfaceDefinition.getInputParam())){
            throw BizException.create(BasicErrorCode.PARAMS_ERROR);
        }
        // DB接口配置中的参数顺序列表
        List<Map> configParamList = JSON.parseArray(interfaceDefinition.getInputParam(),Map.class);

        DecisionFlowInterfaceCallInfo interfaceCallInfo = new DecisionFlowInterfaceCallInfo();
        if (CollectionUtils.isEmpty(configParamList)) {
            return interfaceCallInfo;
        }

        int paramCount = configParamList.size();
        String[] inputParamType = new String[paramCount];
        Object[] inputParamValue = new Object[paramCount];

        try {
            // 将映射字段值，按照层级结构转成json嵌套
            Map<String, Object> jsonTypeParamValue = JsonUtil.parsePath(mappingParamValue);
            for (int i = 0; i < paramCount; i++) {
                Map param = configParamList.get(i);
                inputParamValue[i] = jsonTypeParamValue.get(param.get(CONFIG_KEY_NAME));

                Object type = param.get(CONFIG_KEY_TYPE);
                if(type instanceof Map){
                    inputParamType[i] = (String) ((Map) type).get(CONFIG_KEY_TYPE);
                }else{
                    inputParamType[i] = (String) type;
                }
            }

            interfaceCallInfo.setInputParamType(inputParamType);
            interfaceCallInfo.setInputParamValue(inputParamValue);
        } catch (Exception e) {
            logger.error("encapsulateDubboCallParam of interfaceUuid:{}, error", interfaceDefinition.getUuid(), e);
            throw BizException.create(BasicErrorCode.PARAMS_ERROR, "接口参数配置类型抽取异常", e);
        }

        return interfaceCallInfo;
    }

    /**
     * 解析决策流三方接口入参映射并赋值
     * @param fraudContext
     * @param decisionFlowInterface
     * @return key:interface_field,value:真实值
     */
    private Map<String,Object> assignInputParamValue(AbstractFraudContext fraudContext, DecisionFlowInterface decisionFlowInterface) {
        List<InterfaceDefinitionParamInfo> inputParams = decisionFlowInterface.getInputParams();
        Map<String,Object> mappingParamValue = new HashMap<>(inputParams.size());

        // 拼接指标值组装
        String indexValueStr = getJointIndexs(fraudContext, decisionFlowInterface);

        // 拼接字段值组装
        String fieldValueStr = getJointFields(fraudContext, decisionFlowInterface);

        for (InterfaceDefinitionParamInfo paramInfo : inputParams) {
            Object value = null;
            if ("indexJoint".equalsIgnoreCase(paramInfo.getType())) {
                value = indexValueStr;
            } else if ("fieldsJoint".equalsIgnoreCase(paramInfo.getType())) {
                value = fieldValueStr;
            } else if ("input".equalsIgnoreCase(paramInfo.getType())) {
                value = paramInfo.getRuleField();
            } else {
                // TODO 确认是否获取到字段
                value = fraudContext.get(paramInfo.getRuleField());
                //对于seqId字段直接从context中取值
                String[] temp = paramInfo.getInterfaceField().split("\\.");
                if (temp[temp.length - 1].equals("seqId")) {
                    value = fraudContext.getSeqId();
                }
            }

            paramInfo.setValue(value);
            mappingParamValue.put(paramInfo.getInterfaceField(), value);

            // 必填校验
            if (paramInfo.isNecessary() && value == null) {
                logger.warn(TraceUtils.getFormatTrace() + "call generic dubbo interface:{} param:{} 不能为空", decisionFlowInterface.getName(), paramInfo.getRuleField());
                throw BizException.create(BasicErrorCode.PARAMS_ERROR, "必填参数["+paramInfo.getRuleField()+"]缺失");
            }
        }

        decisionFlowInterface.setInputParams(inputParams);
        return mappingParamValue;
    }

    /**
     * 拼接字段  值组装
     * @param fraudContext
     * @param decisionFlowInterface
     * @return
     */
    private String getJointFields(AbstractFraudContext fraudContext, DecisionFlowInterface decisionFlowInterface) {
        String fieldValueStr;
        String fields = Optional.ofNullable(decisionFlowInterface.getFields()).orElse("");
        // TODO 这逻辑理一理
        if (fields.endsWith(",")) {
            fields += " ";
        }

        String[] fieldArray = fields.split(",");
        List<String> fieldTempList = new ArrayList<>(fieldArray.length);
        for (String field : fieldArray) {
            String fieldTemp;
            if (StringUtils.isNotBlank(field)) {
                fieldTemp = String.valueOf(fraudContext.get(field) == null ? "" : fraudContext.get(field));
                fieldTempList.add(fieldTemp);
            } else {
                fieldTempList.add("");
            }
        }
        fieldValueStr = JSON.toJSONString(fieldTempList);
        return fieldValueStr;
    }

    /**
     * 指标拼接字段 值组装
     * @param fraudContext
     * @param decisionFlowInterface
     * @return
     */
    private String getJointIndexs(AbstractFraudContext fraudContext, DecisionFlowInterface decisionFlowInterface) {
        String indexUuids = Optional.ofNullable(decisionFlowInterface.getIndexUuids()).orElse("");

        // TODO 如果以逗号结尾进行split时会少一个，所以加上一个空格
        if (indexUuids.endsWith(",")) {
            indexUuids += " ";
        }

        String[] indexUuidArray = indexUuids.split(",");
        List<Double> indexTempList = new ArrayList<>(indexUuidArray.length);
        for (String uuid : indexUuidArray) {
            Double indexTemp;
            if (StringUtils.isNotBlank(uuid)) {
                if (fraudContext.getPolicyIndex(uuid) != null) {
                    indexTemp = fraudContext.getPolicyIndex(uuid);
                } else if (fraudContext.getPlatformIndex(uuid) != null) {
                    indexTemp = fraudContext.getPlatformIndex(uuid);
                } else {
                    indexTemp = Double.NaN;
                }
                indexTempList.add(indexTemp);
            } else {
                indexTempList.add(Double.NaN);
            }
        }
        return indexTempList.toString();
    }
}
