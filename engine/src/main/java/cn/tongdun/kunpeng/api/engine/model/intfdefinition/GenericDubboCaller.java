package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.util.JsonUtil;
import cn.tongdun.kunpeng.common.util.KunpengStringUtils;
import cn.tongdun.kunpeng.common.util.ReasonCodeUtil;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
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
    public boolean call(AbstractFraudContext fraudContext, DecisionFlowInterface decisionFlowInterface) {
        long beginTime = System.currentTimeMillis();
        String uuid = decisionFlowInterface.getUuid();
        InterfaceDefinition interfaceDefinition = interfaceDefinitionCache.get(uuid);
        if (null == interfaceDefinition) {
            return false;
        }
        APIResult callResult;                                                  //dubbo api的调用结果
        Object result = null;                                                  //dubbo api的业务返回结果
        Map<String, Object> ruleParams = Collections.emptyMap();
        try {

            //封装dubbo调用输入参数
            Map.Entry<APIResult, List<InterfaceDefinitionParamInfo>> paramsMap = encapsulateParams(fraudContext, decisionFlowInterface);

            callResult = paramsMap.getKey();
            //参数缺失,无需调用
            if (callResult == APIResult.DUBBO_API_RESULT_MISSING_PARAMETER) {
                result = callResult;
                return false;
            }

            ruleParams = Maps.newHashMapWithExpectedSize(decisionFlowInterface.getInputParams().size());
            for (InterfaceDefinitionParamInfo paramInfo : decisionFlowInterface.getInputParams()) {
                if (paramInfo.getRuleField().equals("")) {
                    continue;
                }
                ruleParams.put(paramInfo.getRuleField(), paramInfo.getValue());
            }

            final GenericService genericService = genericServiceManager.getGenericService(interfaceDefinition);

            //获取dubbo调用输入参数类型列表
            List<String> typeList = Lists.newArrayList();
            List<Object> valueList = Lists.newArrayList();
            generateTypeAndValue(decisionFlowInterface, typeList, valueList);
            String[] keys = typeList.stream().toArray(String[]::new);
            Object[] values = valueList.stream().toArray(Object[]::new);

            logger.info("dubbo泛化调用执行入参 interface_method:" + interfaceDefinition.getName() + "-" + interfaceDefinition.getMethodName() +
                    "interfaceParamInfos" + Arrays.toString(decisionFlowInterface.getInputParams().toArray()) + "parametersType" +
                    Arrays.toString(keys) + "methodValues" + Arrays.toString(values));

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
            logger.warn("generic dubbo call " + interfaceDefinition.getName() + " catch exception result:" + JSONObject.toJSONString(result), e);
        } finally {
            try {
                //如果dubbo返回的类是QuickJSONResult,有可能是信贷云那边卡住了
                if (result.getClass().getSimpleName().equalsIgnoreCase("QuickJSONResult")) {
                    //缓存移除,因为某些不同的外部接口可能会调用同一个dubbo服务(即ReferenceConfig),所以这里全部移除
                    logger.error("dubbo泛化调用stop异常 call generic dubbo interface(stop)");
                }

                Map<String, Object> resultMap = wrapResult(fraudContext, result);
                //输入写入到Context,继而到activity
                fillDataToFraudContext(fraudContext, interfaceDefinition, ruleParams, resultMap, beginTime);

                serializeOutParamsToRuleEngine(fraudContext, decisionFlowInterface.isRiskServiceOutput(), decisionFlowInterface, interfaceDefinition, resultMap);

            } catch (Exception e) {
                logger.error("dubbo泛化调用异常 call generic dubbo interface(generate result) error", e);
            }
        }
        return true;
    }

    /**
     * 封装dubbo调用所需的参数信息
     *
     * @param fraudContext
     * @param decisionFlowInterface
     * @return
     */
    private Map.Entry<APIResult, List<InterfaceDefinitionParamInfo>> encapsulateParams(AbstractFraudContext fraudContext, DecisionFlowInterface decisionFlowInterface) {
        List<InterfaceDefinitionParamInfo> inputParams = decisionFlowInterface.getInputParams();
        if (null == inputParams) {
            return new AbstractMap.SimpleEntry(APIResult.DUBBO_API_RESULT_MISSING_PARAMETER, Collections.EMPTY_MAP);
        }
        String indexValueStr, fieldValueStr;
        String indexUuids = decisionFlowInterface.getIndexUuids();
        String fields = decisionFlowInterface.getFields();
        if (null == indexUuids) {
            indexUuids = "";
        }
        if (null == fields) {
            fields = "";
        }
        //如果以逗号结尾进行split时会少一个，所以加上一个空格
        if (indexUuids.endsWith(",")) {
            indexUuids += " ";
        }
        if (fields.endsWith(",")) {
            fields += " ";
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
        indexValueStr = indexTempList.toString();

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
        List<JsonElement> jsonElLists = Lists.newArrayList();
        Map<String, String> interfaceToType = Maps.newHashMap();
        try {
            for (InterfaceDefinitionParamInfo paramInfo : inputParams) {
                Object value = null;
                if (paramInfo.getType().equalsIgnoreCase("indexJoint")) {
                    value = indexValueStr;
                } else if (paramInfo.getType().equalsIgnoreCase("fieldsJoint")) {
                    value = fieldValueStr;
                } else if (paramInfo.getType().equalsIgnoreCase("input")) {
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

                if (paramInfo.getInterfaceField().indexOf(".") == -1) {
                    paramInfo.setValue(value);
                } else {
                    JsonElement jsonElement = new JsonElement();
                    String iPN = paramInfo.getInterfaceField();
                    jsonElement.setPath(iPN);
                    jsonElement.setValue(value);
                    interfaceToType.put(iPN.substring(0, iPN.indexOf(".")), paramInfo.getInterfaceType());
                    jsonElLists.add(jsonElement);
                    paramInfo.setValue(value);
                }

                if (isNecessary && value == null) {
                    logger.warn("generic dubbo call missing necessary params : {}, rulefield : {} ", paramInfo.getInterfaceField(), paramInfo.getRuleField());
                    return new AbstractMap.SimpleEntry(APIResult.DUBBO_API_RESULT_MISSING_PARAMETER, Collections.EMPTY_MAP);
                }
            }
        } catch (Exception e) {
            logger.error("call generic dubbo interface error", e);
        }
        decisionFlowInterface.setInputParams(inputParams);
        return new AbstractMap.SimpleEntry(APIResult.DUBBO_API_RESULT_SUCCESS, inputParams);
    }

    /***
     * 获取接口的参数值
     *
     * @param decisionFlowInterface
     * @param valueList
     * @return
     */
    private void generateTypeAndValue(DecisionFlowInterface decisionFlowInterface, List<String> typeList, List<Object> valueList) {
        List<InterfaceDefinitionParamInfo> inputParams = decisionFlowInterface.getInputParams();

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
            logger.info("dubbo泛化调用返回501错误 errorMessage incorrect generic dubbo result");
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
        JSONObject jsonObject = new JSONObject(inputParamsMap.size());
        for (Map.Entry<String, Object> entry : inputParamsMap.entrySet()) {
            jsonObject.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entry.getKey()), entry.getValue());
        }
        interfaceParams.setInputParams(jsonObject);

        //输出参数
        interfaceParams.setOutputParams(new JSONObject(resultMap));
        interfaceResult.addInterfaceParams(interfaceDefinition.getMethodName(), interfaceParams);


        logger.info("封装泛化dubbo调用结果 resultMap:" + JSON.toJSONString(resultMap));

        //记录调用业务日志
        writeBusinessLog(fraudContext.getSequenceId(), interfaceDefinition.getServiceName(), interfaceDefinition.getMethodName(), interfaceParams.toString(), beginTime);
    }

    public void serializeOutParamsToRuleEngine(AbstractFraudContext fraudContext, boolean isRiskServiceOutput,
                                               DecisionFlowInterface decisionFlowInterface, InterfaceDefinition interfaceDefinition, Object result) {
        if (!(result instanceof Map)) {
            return;
        }

        Map<String, Object> resultMap = (HashMap<String, Object>) result;
        Map<String, Object> resultMapOutput = Maps.newHashMapWithExpectedSize(resultMap.size());
        List<InterfaceDefinitionParamInfo> paramInfoList = getOutParams(decisionFlowInterface);

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
                        logger.error("dubbo泛化调用异常 序列化结果异常", e);
                    }

                    JSONObject resultObjectOutput = resultMapOutput.get(interfaceParams[0]) == null ? new JSONObject((JSONObject) interfaceResult) : (JSONObject) resultMapOutput.get(interfaceParams[0]);
                    if (paramInfo.isArray()) {
                        logger.info("三方调用有数组返回 " + paramInfo.getInterfaceField() + " partnerCode:" + fraudContext.getPartnerCode());
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
                    resultMapOutput.put("policy_set_output_service", decisionFlowInterface.getUuid());
                } else {
                    resultMapOutput.put("policy_set_output_service", interfaceDefinition.getInterfaceId());
                }
                fraudContext.appendInterfaceOutputFields(resultMapOutput);
            }
            Map<String, Object> reflectOutputMap = reflect(outputMap);
            String outputJson = JSON.toJSONString(reflectOutputMap);
            Map<String, Object> flattened3rdRslt = JsonUtil.getFlatteneInfoNoLowCase(outputJson);
            for (Map.Entry<String, Object> entry : flattened3rdRslt.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value != null && !(value instanceof Map)) {
                    fraudContext.setField(key, value);
                }
                String[] interfaceParams = key.split("\\.");

                if (interfaceParams.length > 1) {
                    fraudContext.setObject(true);

                    Object bomObj = reflectOutputMap.get(interfaceParams[0]);
                    fraudContext.setField(interfaceParams[0], bomObj);
                }
            }

            // for logging purpose only
            if (logger.isDebugEnabled()) {
                JSONObject resultObject = new JSONObject();
                for (Map.Entry<String, Object> entry : fraudContext.getSystemFiels().entrySet()) {
                    resultObject.put(entry.getKey(), entry.getValue());
                }
            }

        } catch (Exception e) {
            logger.error("dubbo泛化调用异常 接口调用的结果赋值给规则引擎字段异常", e);
        }
    }


    /***
     * 获取接口输入输出参数
     *
     * @param interfaceInfo
     * @return
     */
    private List<InterfaceDefinitionParamInfo> getOutParams(DecisionFlowInterface interfaceInfo) {
        List<InterfaceDefinitionParamInfo> paramInfoList = new CopyOnWriteArrayList<>();
        try {
            for (InterfaceDefinitionParamInfo paramInfo : interfaceInfo.getOutputParams()) {
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
            logger.error("dubbo泛化调用异常 获取接口输入输出参数异常", e);
        }
        return paramInfoList;
    }

    public static Map<String, Object> reflect(Map<String, Object> vals) {

        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : vals.keySet()) {
            //Object value = vals.get(key);
            for (int i = 1; i <= getPathLength(key); i++) {
                String mkey = getPaths(key, i);
                Object item = get(result, mkey);

                Object val = vals.get(mkey);
                if (item == null && val == null) {
                    set(result, mkey, new HashMap<String, Object>());
                } else if (item != null && val == null) {
//                    set(result, mkey, item);
//                    System.out.println("已经存在不用重新赋值:" + mkey);
                    logger.warn("已经存在不用重新赋值:" + mkey);
                } else if (item != null && val != null) {
//                    System.out.println("数据有问题");
                    logger.warn("数据有问题");
                } else if (item == null && val != null) {
                    set(result, mkey, val);
                }
            }
        }

        return result;

    }

    public static int getPathLength(String path) {
        String[] as = path.split("\\.");
        return as.length;
    }

    public static String getPaths(String path, int index) {
        String[] as = path.split("\\.");
        StringBuilder sbf = new StringBuilder();
        int i = 0;
        while (i < index) {
            sbf.append(as[i]);
            sbf.append(".");
            i++;
        }
        sbf.deleteCharAt(sbf.length() - 1);
        return sbf.toString();
    }

    public static Object get(Map<String, Object> item, String path) {
        if (item == null) {
            return null;
        }
        String[] paths = path.split("\\.");
        int i = 0;
        while (i < paths.length) {
            Object v = item.get(paths[i]);
            if (v == null) {
                return null;
            } else if (v instanceof Map) {
                item = (Map<String, Object>) v;
            }
            i++;
        }
        return item;
    }

    /**
     * 组装成JsonObject
     *
     * @return
     */

    public static void set(Map<String, Object> obj, String path, Object value) {
        String[] paths = path.split("\\.");

        int i = 0;
        boolean end = false;
        while (i < paths.length) {
            Object item = obj.get(paths[i]);
            if (item instanceof Map) {
                obj = (Map<String, Object>) item;
            } else {
                end = true;
            }
            if (end) {
                break;
            }
            i++;
        }
        if (i != paths.length - 1) {
            //path有问题
//            System.out.println(path + " path有问题");
            logger.warn(path + " path有问题");
        } else {
            obj.put(paths[i], value);
        }
    }

    private void writeBusinessLog(String sequenceId, String service, String method, String params, long beginTime) {
        JSONObject jsonObject = new JSONObject(4);
        jsonObject.put("service_name", service);
        jsonObject.put("method_name", method);
        jsonObject.put("params", params);
        jsonObject.put("cost", System.currentTimeMillis() - beginTime);
        logger.info("泛化dubbo调用业务日志" + jsonObject);
    }

}
