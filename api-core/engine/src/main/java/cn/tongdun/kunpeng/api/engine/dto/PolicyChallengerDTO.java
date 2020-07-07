package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

import java.util.Date;

/**
 * @Author: liang.chen
 * @Date: 2020/1/17 下午10:17
 */
@Data
public class PolicyChallengerDTO extends CommonDTO {

    /**
     * 策略uuid policy_uuid
     */
    private String policyDefinitionUuid;

    /**
     * 开始 start_time
     */
    private Date startTime;

    /**
     * 结束 end_time
     */
    private Date endTime;

    /**
     * 流量配置 [ {"policyUuid":"3333","percent":90}, {"policyUuid":"4444","percent":10} ] config
     */
    private String config;
}
