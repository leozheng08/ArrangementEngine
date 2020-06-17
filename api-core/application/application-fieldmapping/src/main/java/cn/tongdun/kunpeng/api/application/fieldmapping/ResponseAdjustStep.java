package cn.tongdun.kunpeng.api.application.fieldmapping;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.model.access.AccessBusiness;
import cn.tongdun.kunpeng.api.engine.model.access.AccessBusinessCache;
import cn.tongdun.kunpeng.api.engine.model.access.AccessParam;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: yuanhang
 * @date: 2020-06-16 11:26
 **/
@Component
//@Step(pipeline = Risk.NAME, phase = Risk.OUTPUT, order = 3500)
public class ResponseAdjustStep implements IRiskStep {

    private Logger logger = LoggerFactory.getLogger(ResponseAdjustStep.class);

    @Autowired
    AccessBusinessCache accessBusinessCache;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        String appName = context.getAppName();
        Map<String, AccessBusiness> uuidAccessMap = accessBusinessCache.getAccessBusinessMap();
        AccessBusiness access = uuidAccessMap.get(appName);
        if (null == access) {
            logger.info(TraceUtils.getFormatTrace() + "access :{} not exits, use default response", appName);
            return true;
        }
        List<AccessParam> accessParams = access.getAccessParams().stream().filter(r -> r.getInputOutput().equals("output")).collect(Collectors.toList());
        // 确定不输出的数据量，少的话，可以用枚举，反射耗时太长
        accessParams.stream().forEach(accessParam -> {
            if ("1".equals(accessParam.getIsMust())) {
                switch (accessParam.getFieldName()) {
                    case "subPolicies":
                        response.setSubPolicys(null);
                        break;
                    case "hitRules":
                        List<ISubPolicyResult> subPolicies = response.getSubPolicys();
                        subPolicies.stream().forEach(r -> r.setHitRules(null));
                        response.setSubPolicys(subPolicies);
                        break;
                    default:
                        break;
                }
            } else {
                try {
                    // TODO check if cost time under 1ms
                    long start = System.currentTimeMillis();
                    Field field = response.getClass().getDeclaredField(accessParam.getFieldName());
                    JsonProperty annotation = field.getAnnotation(JsonProperty.class);
                    InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
                    Field declaredField = invocationHandler.getClass().getDeclaredField(accessParam.getFieldName());
                    declaredField.setAccessible(true);
                    Map members = (Map) declaredField.get(invocationHandler);
                    members.put(accessParam.getFieldName(), accessParam.getAccessParam());
                    logger.info("modify annotation parameters cost time :{}", System.currentTimeMillis() - start);
                } catch (Exception e) {
                    logger.info("modify annotation parameters raise exception :{}", e);
                }
            }

        });

        // 用反射实现可以增加其扩展性
        return true;
    }

//    /**
//     * TODO thread-safe
//     */
//    @Override
//    public String translate(String s) {
//        Map<String, AccessBusiness> uuidAccessMap = accessBusinessCache.getAccessBusinessMap();
//        AccessBusiness access = uuidAccessMap.get(appName);
//        if (null == access) {
//            return s;
//        }
//        List<AccessParam> accessParams = access.getAccessParams().stream().filter(r -> r.getInputOutput().equals("output")).collect(Collectors.toList());
//        for (int var0 = 0;var0 < accessParams.size();var0 ++) {
//            if ("0".equals(accessParams.get(var0).getIsMust()) && s.equals(accessParams.get(var0).getFieldName())) {
//                return accessParams.get(var0).getAccessParam();
//            }
//        }
//        return s;
//    }
}
