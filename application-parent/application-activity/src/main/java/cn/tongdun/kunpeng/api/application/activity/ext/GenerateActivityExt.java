package cn.tongdun.kunpeng.api.application.activity.ext;

import cn.tongdun.kunpeng.api.application.activity.ActitivyMsg;
import cn.tongdun.kunpeng.api.application.activity.IActitivyMsg;
import cn.tongdun.kunpeng.api.application.activity.IGenerateActivityExtPt;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.BizScenario;
import cn.tongdun.kunpeng.common.data.QueueItem;
import cn.tongdun.tdframework.core.extension.Extension;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/3/4 下午4:00
 */
@Extension(business = BizScenario.DEFAULT,tenant = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class GenerateActivityExt implements IGenerateActivityExtPt{

    private Map<String, Method> systemFieldGetter;


    /**
     * 根据出入参、上下文生成Activity消息
     * @param queueItem
     * @return
     */
    @Override
    public IActitivyMsg generateActivity(QueueItem queueItem){
        AbstractFraudContext context = queueItem.getContext();

        ActitivyMsg actitivy = new ActitivyMsg();
        actitivy.setProduceTime(System.currentTimeMillis());
        actitivy.setSeqId(context.getSeqId());

//        actitivy.setRequest();
        actitivy.setResponse(queueItem.getResponse());
        actitivy.setSubReasonCodes(context.getSubReasonCodes());
        return null;
    }


//    /**
//     * 提取FraudContext中所有的系统字段和扩展字段
//     */
//    private JSONObject encodeRequest(AbstractFraudContext context) {
//        // 通过反射提取FraudContext中代码写死的字段
//        JSONObject result = new JSONObject();
//        for (Map.Entry<String, Method> entry : systemFieldGetter.entrySet()) {
//            String fieldName = entry.getKey();
//            Method method = entry.getValue();
//            Object value;
//            try {
//                value = method.invoke(context);
//            } catch (Exception ex) {
//                LogUtil.logWarn(logger, "FraudContext反射调用", method.getName(), "方法调用失败");
//                continue;
//            }
//            if (value == null) continue;
//            result.put(fieldName, value);
//        }
//
//        // 获取系统字段
//        Map<String, Object> systemFields = context.getSystemFiels();
//        putAllIfNotExists(result, systemFields);
//
//        // 获取扩展字段
//        Map<String, Object> customFields = context.getCustomFields();
//        putAllIfNotExists(result, customFields);
//
//        // 指标2.0，velocity数据存储改造需要从activity数据里获取全量数据
//        //去掉运行过程中产生的新地址标准化字段
////        try {
////            if (context.getStandardAddressFieldSet() != null) {
////                for(String fieldName : context.getStandardAddressFieldSet()){
////                    result.remove(fieldName);
////                }
////            }
////        }catch(Exception e){
////        }
//
//        // 返回结果
//        return result;
//    }
}
