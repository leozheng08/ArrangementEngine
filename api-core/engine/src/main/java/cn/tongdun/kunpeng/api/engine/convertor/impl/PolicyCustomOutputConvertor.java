package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.dto.PolicyCustomOutputDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyCustomOutputElementDTO;
import cn.tongdun.kunpeng.api.engine.model.customoutput.PolicyCustomOutput;
import cn.tongdun.kunpeng.api.engine.model.customoutput.PolicyCustomOutputElement;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/9/16 23:25
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class PolicyCustomOutputConvertor implements IConvertor<PolicyCustomOutputDTO, PolicyCustomOutput> {
    @Override
    public PolicyCustomOutput convert(PolicyCustomOutputDTO policyCustomOutputDTO) {

        PolicyCustomOutput policyCustomOutput = new PolicyCustomOutput();
        BeanUtils.copyProperties(policyCustomOutputDTO,policyCustomOutput);
        policyCustomOutput.setPolicyCustomOutputElements(buildCustomOutputElement(policyCustomOutputDTO.getPolicyCustomOutputElementDTOS()));
        return policyCustomOutput;
    }

    private List<PolicyCustomOutputElement> buildCustomOutputElement(List<PolicyCustomOutputElementDTO> policyCustomOutputElementDTOS) {
        List<PolicyCustomOutputElement> list = new ArrayList<>();
        for (PolicyCustomOutputElementDTO dto :
                policyCustomOutputElementDTOS) {
            PolicyCustomOutputElement element = new PolicyCustomOutputElement();
            element.setLeftProperty(dto.getLeftProperty());
            element.setLeftPropertyDataType(dto.getLeftPropertyDataType());
            element.setLeftPropertyType(dto.getLeftPropertyType());
            element.setRightConfig(dto.isRightConfig());
            element.setPolicyCustomOutputUuid(dto.getPolicyCustomOutputUuid());
            element.setRightDataType(dto.getRightDataType());
            element.setRightType(dto.getRightType());
            element.setRightValue(dto.getRightValue());
            list.add(element);
        }
        return list;
    }
}
