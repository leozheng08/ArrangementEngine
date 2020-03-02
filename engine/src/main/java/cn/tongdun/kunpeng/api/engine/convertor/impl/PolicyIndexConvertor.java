package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.fraudmetrix.module.tdrule.constant.FieldTypeEnum;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.tongdun.kunpeng.api.engine.constant.PolicyIndexConstant;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.dto.IndexDefinitionDTO;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndex;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/2/18 10:58 AM
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class PolicyIndexConvertor implements IConvertor<List<IndexDefinitionDTO>, List<PolicyIndex>> {

    private static BufferedWriter bw;

    static {
        try {
            bw = new BufferedWriter(new FileWriter("/Users/liuqiang/work/kunpeng-api/test.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    DefaultConvertorFactory convertorFactory;

    @PostConstruct
    public void init() {
        convertorFactory.register(IndexDefinitionDTO.class, this);
    }

    @Override
    public List<PolicyIndex> convert(List<IndexDefinitionDTO> indexDefinitionDTOS) {

        if (indexDefinitionDTOS == null || indexDefinitionDTOS.isEmpty()) {
            return Collections.emptyList();
        }
        //1.把所有的PolicyIndex转为FunctionDesc
        Map<String, FunctionDesc> functionDescMap = Maps.newHashMap();
        int num = 0;
        for (IndexDefinitionDTO definitionDTO : indexDefinitionDTOS) {
            List<FunctionParam> paramList = JSONArray.parseArray(definitionDTO.getParams(), FunctionParam.class);
            FunctionDesc functionDesc = new FunctionDesc();
            functionDesc.setId(num++);
            functionDesc.setType(definitionDTO.getTemplate());
            functionDesc.setParamList(paramList);
            functionDescMap.put(definitionDTO.getUuid(), functionDesc);
        }

        //2.找出所有有嵌套递归计算的FunctionDesc，并把嵌套的逻辑放好
        for (Map.Entry<String, FunctionDesc> entry : functionDescMap.entrySet()) {
            for (FunctionParam functionParam : entry.getValue().getParamList()) {
                if (FieldTypeEnum.POLICY_INDEX.equalsForType(functionParam.getType())) {
                    FunctionDesc functionDesc = functionDescMap.get(functionParam.getValue());
                    if (null == functionDesc) {
                        try {
                            bw.write(entry.getKey());
                            bw.newLine();
                            bw.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        throw new ParseException("PolicyIndexConvertor convert error,the Index param is function,but not found functionDesc,policyIndexUuid:" + entry.getKey());
                    }

                    functionParam.putExtProperty(PolicyIndexConstant.POLICY_INDEX_FUNC_DESC, functionDesc);
                    functionParam.putExtProperty(PolicyIndexConstant.POLICY_INDEX_STAGE_TAG, "true");
                }
            }
        }
        //3.构造PolicyIndex列表返回
        List<PolicyIndex> policyIndexList = Lists.newArrayList();
        for (IndexDefinitionDTO indexDefinitionDTO : indexDefinitionDTOS) {
            PolicyIndex policyIndex = new PolicyIndex();
            policyIndex.setGmtModify(indexDefinitionDTO.getGmtModify());
            policyIndex.setUuid(indexDefinitionDTO.getUuid());
            policyIndex.setPolicyUuid(indexDefinitionDTO.getPolicyUuid());
            policyIndex.setCalculateFunction(FunctionLoader.getFunction(functionDescMap.get(indexDefinitionDTO.getUuid())));
            policyIndexList.add(policyIndex);
        }
        return policyIndexList;
    }
}
