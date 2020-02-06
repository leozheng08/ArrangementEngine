package cn.tongdun.kunpeng.api.core.rule.detail;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 设备获取异常
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class FpExceptionDetail extends ConditionDetail {

    //异常码
    private String code;

    //异常码显示名
    private String codeDisplayName;

    public FpExceptionDetail(){
        super("fp_exception");
    }


}
