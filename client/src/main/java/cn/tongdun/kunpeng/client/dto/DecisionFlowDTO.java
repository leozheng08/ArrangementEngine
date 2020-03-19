package cn.tongdun.kunpeng.client.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/1/17 下午10:08
 */
@Data
public class DecisionFlowDTO extends CommonDTO {

    /**
     * partner code partner_code
     */
    private String partnerCode;


    /**
     * 事件id event_id
     */
    private String eventId;

    /**
     * 决策流描述 description
     */
    private String description;

    /**
     * 决策流状态 0:未启用 1:已启用 status
     */
    private Integer status;

    /**
     * 暂存决策流关联决策流uuid origin_uuid
     */
    private String originUuid;

    /**
     * 决策流内容 process_content
     */
    private String processContent;

    /**
     * 决策流图形定义内容 diagram_content
     */
    private String diagramContent;

    /**
     * 是否已删除
     */
    private boolean deleted;

    /**
     * 删除时间
     */
    private Date gmtDelete;


//    private List<DecisionFlowInterfaceDTO> decisionFlowInterfaceList;
//    private List<DecisionFlowModelDTO> DecisionFlowModelList;
}
