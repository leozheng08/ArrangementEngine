package cn.tongdun.kunpeng.api.application.phone.fuction;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.application.phone.constant.PhoneModelResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

public class PhoneModelFunction extends AbstractFunction {


    /**
     * 类型结果
     */
    private String operate;


    @Override
    protected void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    protected FunctionResult run(ExecuteContext executeContext) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }



//    public static void main(String[] args) {
//        // 缓存不存在，调用接口
//        PhoneModelResult info = new PhoneModelResult();
//        Map<String, String> params = new HashMap<>();
//        Map<String, String> headers = new HashMap<>();
//        params.put("account_lifecycle_event", "create");
//        String url="https://rest-ww.telesign.com/v1/score/";
//        String baseValue = BaseEncoding.base64().encode(("5A0C9ECE-D3C4-49A6-9010-5FAD59FDDEBB" + ":" + "55b2vF0ASzUy3dOtsVvo/J1SBxpbdvdw/wUr+6sUYc/LRpAEP8JcFPAyTJnnRYRT8FzR/uhPCDBgHJv2WH6nXQ==").getBytes());
//        //log.info("TeleSignRequestFilter-queryTeleSign,mobile:{},baseValue:{}", "+8615587688256", baseValue);
//        headers.put(HttpHeaders.AUTHORIZATION, "Basic " + baseValue);
//        try {
//            String result = HttpUtils.post(url + "15587688256", params, headers);
//            String s;
//        }catch (Exception e){
//
//        }



//    }

    public static void main(String[] args) throws Exception {
        Map params = Maps.newHashMap();
        Map result = Maps.newHashMap();
        params.put("account_lifecycle_event", "create");
        params.put("business", "test");
        params.put("time_inteval", "7");
        params.put("sim_num", "10");
        params.put("event_time", "2020-05-01 12:12:12");
        String url ="https://rest-ww.telesign.com/v1/score/";
        String baseValue = BaseEncoding.base64().encode(("5A0C9ECE-D3C4-49A6-9010-5FAD59FDDEBB" + ":" + "55b2vF0ASzUy3dOtsVvo/J1SBxpbdvdw/wUr+6sUYc/LRpAEP8JcFPAyTJnnRYRT8FzR/uhPCDBgHJv2WH6nXQ==").getBytes());
        System.out.println(url);

        RequestBody body = new FormBody.Builder()
                .add("account_lifecycle_event", "create")
                .build();
        Request request = new Request
                .Builder()
                .addHeader(HttpHeaders.AUTHORIZATION, baseValue)
                .url(url+"15587688256")
                .post(body).build();

        cn.tongdun.kunpeng.api.application.util.HttpUtils.postJson(Lists.newArrayList(request), result);
        System.out.println(result);
    }




}
