package cn.tongdun.kunpeng.client.dto;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2020/1/17 下午10:13
 */
@Data
public class DecisionFlowInterfaceDTO extends CommonDTO {

    /**
     * 合作方 partner_code
     */
    private String partnerCode;

    /**
     * 接口uuid interface_uuid
     */
    private String interfaceUuid;

    /**
     * 决策流uuid decision_flow_uuid
     */
    private String decisionFlowUuid;

    /**
     * 三方接口输出变量是否直接决策接口输出：1 是 0 否 is_risk_service_output
     */
    private boolean riskServiceOutput;

    /**
     * 输入 input_field [
     * {"interface_field":"MobileInfo.partnerCode","interface_type":"cn.fraudmetrix.creditcloud.object.request.MobileInfo","necessary":false,"isArray":false,"type":"default","rule_field":"partnerCode"},
     * {"interface_field":"MobileInfo.mobile","interface_type":"cn.fraudmetrix.creditcloud.object.request.MobileInfo","necessary":false,"isArray":false,"type":"default","rule_field":"accountMobile"},
     * {"interface_field":"MobileInfo.seqId","interface_type":"cn.fraudmetrix.creditcloud.object.request.MobileInfo","necessary":false,"isArray":false,"type":"default","rule_field":"sequenceId"}
     * ]
     */
    private String inputs;

    /**
     * 输出 output_field
     *
     * [{"interface_field":"result","interface_type":"java.lang.Integer","necessary":false,"isArray":false,"selectType":"java.lang.Integer","rule_field":"mobileThreeElementConsistence"},
     * {"interface_field":"success","interface_type":"java.lang.Boolean","necessary":false,"isArray":false,"selectType":"java.lang.Boolean","rule_field":"mobileThreeElementSuccess"}
     * ]
     */
    private String outputs;

    /**
     * 字段 fields
     */
    private String fields;
}
