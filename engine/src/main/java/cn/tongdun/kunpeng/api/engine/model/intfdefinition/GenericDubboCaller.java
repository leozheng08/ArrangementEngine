package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.util.KunpengStringUtils;
import cn.tongdun.kunpeng.common.util.LogUtil;
import cn.tongdun.kunpeng.common.util.ReasonCodeUtil;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service("genericDubboCaller")
public class GenericDubboCaller implements IGenericDubboCaller {

    // 程序执行日志
    private final static Logger logger = LoggerFactory.getLogger(GenericDubboCaller.class);

    @Autowired
    private InterfaceDefinitionCache interfaceDefinitionCache;

    @Autowired
    private GenericServiceManager genericServiceManager;

    @Override
    public boolean call(AbstractFraudContext fraudContext, InterfaceDefinitionInfo interfaceInfo, String indexUuids, String fields) {
        long beginTime = System.currentTimeMillis();
        String uuid = interfaceInfo.getUuid();
        InterfaceDefinition interfaceDefinition = interfaceDefinitionCache.get(uuid);
        if (null == interfaceDefinition) {
            return false;
        }
        String partnerCode = String.valueOf(fraudContext.getPartnerCode());
        String serviceName = interfaceDefinition.getServiceName();

        APIResult callResult;                                                  //dubbo api的调用结果
        Object result = null;                                                  //dubbo api的业务返回结果
        Map<String, Object> ruleParams = Collections.emptyMap();
        try {

            //封装dubbo调用输入参数
            Map.Entry<APIResult, List<InterfaceDefinitionParamInfo>> paramsMap = encapsulateParams(fraudContext, interfaceInfo, indexUuids, fields);

            callResult = paramsMap.getKey();
            //参数缺失,无需调用
            if (callResult == APIResult.DUBBO_API_RESULT_MISSING_PARAMETER) {
                result = callResult;
                return false;
            }

            ruleParams = Maps.newHashMapWithExpectedSize(interfaceInfo.getInputParams().size());
            for (InterfaceDefinitionParamInfo paramInfo : interfaceInfo.getInputParams()) {
                if (paramInfo.getRuleField().equals("")) {
                    continue;
                }
                ruleParams.put(paramInfo.getRuleField(), paramInfo.getValue());
            }

            final GenericService genericService = genericServiceManager.getGenericService(interfaceDefinition);

            //获取dubbo调用输入参数类型列表
            List<String> typeList = Lists.newArrayList();
            List<Object> valueList = Lists.newArrayList();
            generateTypeAndValue(interfaceInfo, typeList, valueList);
            String[] keys = typeList.stream().toArray(String[]::new);
            Object[] values = valueList.stream().toArray(Object[]::new);

            LogUtil.logInfo(logger, "dubbo泛化调用执行入参", null, "interface_method", interfaceDefinition.getName() + "-" + interfaceDefinition.getMethodName(),
                    "interfaceParamInfos", Arrays.toString(interfaceInfo.getInputParams().toArray()), "parametersType",
                    Arrays.toString(keys), "methodValues", Arrays.toString(values));

            result = genericService.$invoke(interfaceDefinition.getMethodName(), keys, values);

        } catch (Exception e) {
            String providerHost = StringUtils.defaultIfBlank(RpcContext.getContext().getRemoteHost(), "-");
            if (ReasonCodeUtil.isTimeout(e)) {          //哎呦, 超时了
                result = APIResult.DUBBO_API_RESULT_TIMEOUT;
            } else if (e.getClass() == com.alibaba.dubbo.rpc.RpcException.class) {             //哎呦, dubbo调用失败了，例如服务提供者未注册之类的错误
                result = APIResult.DUBBO_API_RESULT_EXTERNAL_CALL_ERROR;
            } else {
                result = APIResult.DUBBO_API_RESULT_INTERNAL_ERROR;                         //哎呦，我TMD也不知道发生什么错误了!
            }
            LogUtil.logWarn(logger, "generic dubbo call", interfaceDefinition.getName(), "catch exception", e,
                    "result", result);
        } finally {
            try {
                //如果dubbo返回的类是QuickJSONResult,有可能是信贷云那边卡住了
                if (result.getClass().getSimpleName().equalsIgnoreCase("QuickJSONResult")) {
                    //缓存移除,因为某些不同的外部接口可能会调用同一个dubbo服务(即ReferenceConfig),所以这里全部移除
                    LogUtil.logError(logger, "dubbo泛化调用stop异常", null, "call generic dubbo interface(stop)");
                }

                Map<String, Object> resultMap = wrapResult(fraudContext, result);
                //输入写入到Context,继而到activity
                fillDataToFraudContext(fraudContext, interfaceDefinition, ruleParams, resultMap, beginTime);

                serializeOutParamsToRuleEngine(fraudContext, interfaceInfo.isRiskServiceOutput(), interfaceInfo, interfaceDefinition, resultMap);

            } catch (Exception e) {
                LogUtil.logError(logger, "dubbo泛化调用异常", null, "call generic dubbo interface(generate result) error", e);
            }
        }
        return true;
    }

    /**
     * 封装dubbo调用所需的参数信息
     *
     * @param fraudContext
     * @param interfaceInfo
     * @param indexUuids
     * @param fields
     * @return
     */
    private Map.Entry<APIResult, List<InterfaceDefinitionParamInfo>> encapsulateParams(AbstractFraudContext fraudContext, InterfaceDefinitionInfo interfaceInfo, String indexUuids, String fields) {
        List<InterfaceDefinitionParamInfo>  inputParams = interfaceInfo.getInputParams();
        if (null == inputParams) {
            return new AbstractMap.SimpleEntry(APIResult.DUBBO_API_RESULT_MISSING_PARAMETER, Collections.EMPTY_MAP);
        }
        //TODO indexUuids fields暂时未处理

        try {
            for (InterfaceDefinitionParamInfo paramInfo : inputParams) {
                Object value = null;
                if (paramInfo.getType().equalsIgnoreCase("input")) {
                    value = paramInfo.getRuleField();
                } else {
                    value = fraudContext.get(paramInfo.getRuleField());
                    //对于sequenceId字段直接从context中取值
                    String[] temp = paramInfo.getInterfaceField().split("\\.");
                    if (temp[temp.length - 1].equals("sequenceId")) {
                        value = fraudContext.getSequenceId();
                    }
                    if (temp[temp.length - 1].equals("appName")) {
                        value = fraudContext.getAppName();
                    }
                }
                boolean isNecessary = paramInfo.isNecessary();

                if (isNecessary && value == null) {
                    logger.warn("generic dubbo call missing necessary params : {}, rulefield : {} ", paramInfo.getInterfaceField(), paramInfo.getRuleField());
                    return new AbstractMap.SimpleEntry(APIResult.DUBBO_API_RESULT_MISSING_PARAMETER, Collections.EMPTY_MAP);
                }
                paramInfo.setValue(value);
            }
        } catch (Exception e) {
            logger.error("call generic dubbo interface error", e);
        }
        interfaceInfo.setInputParams(inputParams);
        return new AbstractMap.SimpleEntry(APIResult.DUBBO_API_RESULT_SUCCESS, inputParams);
    }

    /***
     * 获取接口的参数值
     *
     * @param interfaceInfo
     * @return
     */
    private void generateTypeAndValue(InterfaceDefinitionInfo interfaceInfo, List<String> typeList, List<Object> valueList) {
        List<InterfaceDefinitionParamInfo> inputParams = interfaceInfo.getInputParams();

        Map<String, Map<String, Object>> map = Maps.newHashMap();
        for (InterfaceDefinitionParamInfo paramInfo : inputParams) {
            String interfaceType = paramInfo.getInterfaceType();
            String interfaceField = paramInfo.getInterfaceField();
            Object value = paramInfo.getValue();
            if (interfaceField.indexOf(".") != -1) {
                String[] temp = interfaceField.split("\\.");
                String key = temp[0];
                Map<String, Object> objMap;
                if (map.containsKey(key)) {
                    objMap = map.get(key);
                } else {
                    objMap = Maps.newHashMap();
                }
                objMap.put(temp[1], value);
                map.put(interfaceType, objMap);
            }
        }
        for (InterfaceDefinitionParamInfo paramInfo : inputParams) {
            String interfaceType = paramInfo.getInterfaceType();
            Object value = paramInfo.getValue();
            if (!map.containsKey(interfaceType)) {
                typeList.add(interfaceType);
                valueList.add(value);
            }
        }
        map.keySet().forEach(key -> {
            typeList.add(key);
            valueList.add(map.get(key));
        });
    }

    private Map<String, Object> wrapResult(AbstractFraudContext context, Object result) {
        Map<String, Object> resultMap = Maps.newHashMap();
        if (result == null) {
            return resultMap;
        }
        if ((result instanceof HashMap)) {       //接口方法返回的是一个Map
            Map<String, Object> map = (Map) result;
            map.remove("class");
            resultMap.putAll((Map) result);
        } else if (result instanceof APIResult) {
            resultMap = ((APIResult) result).toMap();
        } else if (result instanceof Collections || result.getClass().isArray()) { //不支持其它集合或者数组类型
            return resultMap;
        } else if (result instanceof java.lang.String
                || result instanceof java.lang.Integer
                || result instanceof java.lang.Boolean
                || result instanceof java.lang.Double
                || result instanceof java.lang.Float) {
            resultMap.put("result", result);
        } else {        //其它dubbo异常情况产生的返回值一律处理成外部服务器错误501
            resultMap = APIResult.DUBBO_API_RESULT_EXTERNAL_CALL_ERROR.toMap();
            LogUtil.logInfo(logger, "dubbo泛化调用返回501错误", null, "errorMessage", "incorrect generic dubbo result");
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
     * @param inputParamsMap
     * @param resultMap
     */
    private void fillDataToFraudContext(AbstractFraudContext fraudContext,
                                        InterfaceDefinition interfaceDefinition,
                                        Map<String, Object> inputParamsMap,
                                        Map<String, Object> resultMap,
                                        long beginTime) {

    }

    public void serializeOutParamsToRuleEngine(AbstractFraudContext fraudContext, boolean isRiskServiceOutput,
                                               InterfaceDefinitionInfo interfaceInfo, InterfaceDefinition interfaceDefinition, Object result) {
        if (!(result instanceof Map)) {
            return;
        }

        Map<String, Object> resultMap = (HashMap<String, Object>) result;
        Map<String, Object> resultMapOutput = Maps.newHashMapWithExpectedSize(resultMap.size());
        List<InterfaceDefinitionParamInfo> paramInfoList = getOutParams(interfaceInfo);

        Map<String, Object> outputMap = Maps.newHashMapWithExpectedSize(paramInfoList.size());
        try {
            for (InterfaceDefinitionParamInfo paramInfo : paramInfoList) {
                String ruleParam = paramInfo.getRuleField();
                String interfaceParam = paramInfo.getInterfaceField();
                String[] interfaceParams = interfaceParam.split("\\.");
                // 以下两部判断是为了兼容规则引擎定义的下划线形式的字段和kunta返回的驼峰形式
                if ("reason_code".equalsIgnoreCase(interfaceParam)) {
                    interfaceParam = "reasonCode";
                }
                if ("reason_desc".equalsIgnoreCase(interfaceParam)) {
                    interfaceParam = "reasonDesc";
                }
                if (interfaceParams.length == 1) {
                    outputMap.put(ruleParam, resultMap.get(interfaceParam));
                    resultMapOutput.put(KunpengStringUtils.camel2underline(ruleParam), resultMap.get(interfaceParam));
                } else {
                    Object interfaceResult = resultMap.get(interfaceParams[0]);
                    if (null == interfaceResult) {
                        continue;
                    }
                    try {
                        interfaceResult = JSON.parseObject(JSON.toJSONString(interfaceResult));
                    } catch (Exception e) {
                        LogUtil.logError(logger, "dubbo泛化调用异常", null, "序列化结果异常", e);
                    }

                    JSONObject resultObjectOutput = resultMapOutput.get(interfaceParams[0]) == null ? new JSONObject((JSONObject) interfaceResult) : (JSONObject) resultMapOutput.get(interfaceParams[0]);
                    if (paramInfo.isArray()) {
                        LogUtil.logInfo(logger, "三方调用有数组返回", paramInfo.getInterfaceField(), "partnerCode",
                                fraudContext.getPartnerCode());
                        boolean firstSet = true;
                        int i = 1;
                        for (; i < interfaceParams.length - 1; i++) {
                            if (interfaceResult instanceof JSONObject) {
                                if (firstSet) {
                                    interfaceResult = resultObjectOutput.get(interfaceParams[i]);
                                    firstSet = false;
                                } else {
                                    interfaceResult = ((JSONObject) interfaceResult).get(interfaceParams[i]);
                                }

                            }
                        }
                        Object[] resultArray = null;
                        if (interfaceResult instanceof JSONArray) {
                            JSONArray array = (JSONArray) interfaceResult;
                            resultArray = new Object[array.size()];
                            for (int j = 0; j < array.size(); j++) {
                                String value = ((JSONObject) array.get(j)).getString(interfaceParams[i]);
                                resultArray[j] = value;
                                if (!KunpengStringUtils.camel2underline(ruleParam).equals(interfaceParams[i])) {
                                    ((JSONObject) array.get(j)).put(KunpengStringUtils.camel2underline(ruleParam), value);
                                    ((JSONObject) array.get(j)).remove(interfaceParams[i]);
                                }

                            }
                            resultMapOutput.put(interfaceParams[0], resultObjectOutput);
                        }
                        if (resultArray != null) {
                            outputMap.put(ruleParam, resultArray);
                        }
                    } else {
                        JSONObject resultObject = (JSONObject) interfaceResult;
                        int i = 1;
                        for (; i < interfaceParams.length - 1; i++) {
                            if (i == 1) {
                                resultObject = resultObjectOutput.getJSONObject(interfaceParams[i]);
                            } else {
                                resultObject = resultObject.getJSONObject(interfaceParams[i]);
                            }

                        }
                        if (resultObject != null) {
                            outputMap.put(ruleParam, resultObject.get(interfaceParams[i]));
                            if (!KunpengStringUtils.camel2underline(ruleParam).equals(interfaceParams[i])) {
                                resultObject.put(KunpengStringUtils.camel2underline(ruleParam),
                                        resultObject.get(interfaceParams[i]));
                                resultObject.remove(interfaceParams[i]);
                            }

                        }
                        resultMapOutput.put(interfaceParams[0], resultObjectOutput);
                    }
                }
            }
            if (isRiskServiceOutput) {// 决策流三方接口纯数据输出
                if (StringUtils.isBlank(interfaceDefinition.getInterfaceId())) {
                    resultMapOutput.put("policy_set_output_service", interfaceInfo.getUuid());
                } else {
                    resultMapOutput.put("policy_set_output_service", interfaceDefinition.getInterfaceId());
                }
                fraudContext.appendInterfaceOutputFields(resultMapOutput);
            }
            //TODO




            // for logging purpose only
            if (logger.isDebugEnabled()) {
                JSONObject resultObject = new JSONObject();
                for (Map.Entry<String, Object> entry : fraudContext.getSystemFiels().entrySet()) {
                    resultObject.put(entry.getKey(), entry.getValue());
                }
            }

        } catch (Exception e) {
            LogUtil.logError(logger, "dubbo泛化调用异常", null, "接口调用的结果赋值给规则引擎字段异常", e);
        }
    }


    /***
     * 获取接口输入输出参数
     *
     * @param interfaceInfo
     * @return
     */
    private List<InterfaceDefinitionParamInfo> getOutParams(InterfaceDefinitionInfo interfaceInfo) {
        List<InterfaceDefinitionParamInfo> paramInfoList = new CopyOnWriteArrayList<>();
        try {
            for(InterfaceDefinitionParamInfo paramInfo : interfaceInfo.getOutputParams()) {
                String ruleParamName = paramInfo.getRuleField();
                String interfaceParamName = paramInfo.getInterfaceField();
                boolean isArray = paramInfo.isArray();
                InterfaceDefinitionParamInfo outputParamInfo = new InterfaceDefinitionParamInfo();
                outputParamInfo.setRuleField(ruleParamName);
                outputParamInfo.setInterfaceField(interfaceParamName);
                outputParamInfo.setArray(isArray);
                paramInfoList.add(outputParamInfo);
            }
        } catch (Exception e) {
            LogUtil.logError(logger, "dubbo泛化调用异常", null, "获取接口输入输出参数异常", e);
        }
        return paramInfoList;
    }

}
