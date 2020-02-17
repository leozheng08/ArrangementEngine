package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 本地调用所有dubbo的调用信息
 */
public class CallResult {

    @JSONField(name="external_interface_call_result")
    private List<ApplicationResult> applicationResultList = new CopyOnWriteArrayList<>();   //dubbo应用调用列表

    private Map<String,Integer> applicationNameMap = new HashMap<>();

    public List<ApplicationResult> getApplicationResultList() {
        return applicationResultList;
    }

    public void setApplicationResultList(List<ApplicationResult> applicationResultList) {
        this.applicationResultList = applicationResultList;
    }

    public JSONObject getOutputParams(String applicationName, String interfaceName, String methodName){
        Integer index = applicationNameMap.get(applicationName);
        if(index != null ){
            ApplicationResult applicationResult =  this.applicationResultList.get(index);
            InterfaceResult interfaceResult = applicationResult.getInterfaceResult(interfaceName);
            if(interfaceResult != null){
                InterfaceParams interfaceParams = interfaceResult.getInterfaceParams(methodName);
                if(interfaceParams != null ){
                    return interfaceParams.getOutputParams();
                }
            }
        }
        return null;
    }
    public ApplicationResult getDubboApplicationResult(String applicationName){
        Integer index = applicationNameMap.get(applicationName);
        ApplicationResult applicationResult = null;
        if(index == null){
            applicationNameMap.put(applicationName,applicationResultList.size());
            applicationResult = new ApplicationResult(applicationName);
            this.applicationResultList.add(applicationResult);
        }else{
            applicationResult = this.applicationResultList.get(index);
        }
        return applicationResult;
    }
}

/**
 * 某个应用的dubbo的调用信息
 */
class ApplicationResult {

    @JSONField(name="application")
    private String application;                 //dubbo服务所属应用

    @JSONField(name="interface_call_list")
    private List<InterfaceResult> interfaceResultList = new CopyOnWriteArrayList<>(); //dubbo接口调用列表


    private Map<String,Integer> interfaceResultMap = new HashMap<>();


    public ApplicationResult(){

    }

    public ApplicationResult(String application){
        this.application = application;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public List<InterfaceResult> getInterfaceResultList() {
        return interfaceResultList;
    }

    public void setInterfaceResultList(List<InterfaceResult> interfaceResultList) {
        this.interfaceResultList = interfaceResultList;
    }

    public InterfaceResult getInterfaceResult(String type){
        Integer index = interfaceResultMap.get(type);
        if(index != null){
            return this.interfaceResultList.get(index);
        }
        return  null;
    }
    public InterfaceResult getDubboInterfaceResult(String type){
        Integer index = interfaceResultMap.get(type);
        InterfaceResult interfaceResult = null;
        if(index == null){
            interfaceResultMap.put(type,interfaceResultList.size());
            interfaceResult = new InterfaceResult(type);
            this.interfaceResultList.add(interfaceResult);
        }else{
            interfaceResult = this.interfaceResultList.get(index);
        }
        return interfaceResult;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        return ((ApplicationResult)obj).getApplication().equalsIgnoreCase(this.application);
    }

    @Override
    public int hashCode() {
        return this.application.hashCode();
    }

}

/**
 * 某个应用下某个dubbo接口的调用情况
 */
class InterfaceResult {

    @JSONField(name="type")
    private String type;                    //dubbo服务类型

    @JSONField(name="details")              //dubbo服务调用具体情况
    private List<InterfaceParams> interfaceParamsList = new CopyOnWriteArrayList<>();

    private Map<String,Integer>  interfaceParamsMap = new HashMap<>();

    public InterfaceResult(){

    }

    public InterfaceResult(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InterfaceParams getInterfaceParams(String methodName){
        Integer index = interfaceParamsMap.get(methodName);
        if(index != null){
            return this.interfaceParamsList.get(index);
        }
        return  null;
    }
    public List<InterfaceParams> getInterfaceParamsList() {
        return interfaceParamsList;
    }

    public void addInterfaceParams(String methodName, InterfaceParams interfaceParams){
        Integer index = interfaceParamsMap.get(methodName);
        if(index == null){
            interfaceParamsMap.put(methodName,interfaceParamsList.size());
            this.interfaceParamsList.add(interfaceParams);
        }else{
            this.interfaceParamsList.get(index);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        return ((InterfaceResult)obj).getType().equalsIgnoreCase(this.type);
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }
}

/**
 * 某个应用下面某个dubbo接口的输入输出结果
 */
class InterfaceParams {

    @JSONField(name="input_parameters")
    private JSONObject    inputParams;       //用户输入参数, 规则引擎字段
    @JSONField(name="result")
    private JSONObject    outputParams;      //三方输出结果

    public JSONObject getInputParams() {
        return inputParams;
    }

    public void setInputParams(JSONObject inputParams) {
        this.inputParams = inputParams;
    }

    public JSONObject getOutputParams() {
        return outputParams;
    }

    public void setOutputParams(JSONObject outputParams) {
        this.outputParams = outputParams;
    }

    @Override
    public String toString() {
        return String.format("input:%s,result:%s", inputParams.toJSONString(),outputParams.toJSONString());
    }
}
