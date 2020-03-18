package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.fraudmetrix.module.tdflow.model.graph.Graph;
import cn.fraudmetrix.module.tdflow.util.GraphUtil;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.client.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionFlow;
import cn.tongdun.kunpeng.api.engine.util.CompressUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 上午10:16
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class DecisionFlowConvertor implements IConvertor<DecisionFlowDTO, DecisionFlow> {

    private static Logger logger = LoggerFactory.getLogger(DecisionFlowConvertor.class);

    @Autowired
    DefaultConvertorFactory convertorFactory;

    @PostConstruct
    public void init() {
        convertorFactory.register(DecisionFlowDTO.class, this);
    }

    @Override
    public DecisionFlow convert(DecisionFlowDTO decisionFlowDTO) {
        return convert(decisionFlowDTO,true);
    }

    public DecisionFlow convert(DecisionFlowDTO decisionFlowDTO,boolean isZip){
        if (StringUtils.isBlank(decisionFlowDTO.getProcessContent())) {
            throw new ParseException("DecisionFlowConvertor convert error,decisionFlowUuid:" + decisionFlowDTO.getUuid());
        }

        DecisionFlow decisionFlow = new DecisionFlow();
        decisionFlow.setUuid(decisionFlowDTO.getUuid());
        decisionFlow.setPolicyUuid(decisionFlowDTO.getUuid());
        decisionFlow.setGmtModify(decisionFlowDTO.getGmtModify());
        decisionFlow.setDecisionFlowUuid(decisionFlowDTO.getUuid());

        String processXml = decisionFlowDTO.getProcessContent();
        if(isZip) {//是否内容为压缩格式
            try {
                processXml = CompressUtil.ungzip(decisionFlowDTO.getProcessContent());
            } catch (Exception e) {
                logger.error("DecisionFlowConvertor convert processContent error!decisionFlowUuid:" + decisionFlowDTO.getUuid());
                throw new ParseException("DecisionFlowConvertor convert error!Uuid:" + decisionFlowDTO.getUuid(), e);
            }
        }

        Graph graph = null;
        try {
            graph = GraphUtil.createGraph(processXml);
        } catch (Exception e) {
            logger.error("DecisionFlowConvertor createGraph error!decisionFlowUuid:" + decisionFlowDTO.getUuid());
            throw e;
        }

        decisionFlow.setGraph(graph);

        return decisionFlow;
    }
}
