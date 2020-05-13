package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.fraudmetrix.module.tdflow.definition.EdgeDesc;
import cn.fraudmetrix.module.tdflow.definition.GraphDesc;
import cn.fraudmetrix.module.tdflow.model.graph.Graph;
import cn.fraudmetrix.module.tdflow.util.GraphParser;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.client.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionFlow;
import cn.tongdun.kunpeng.api.engine.util.CompressUtil;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

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
        return convert(decisionFlowDTO,false);
    }

    /**
     *
     * @param decisionFlowDTO
     * @param isPreCheck 是否为预校验，提供给管理域使用
     * @return
     */
    public DecisionFlow convert(DecisionFlowDTO decisionFlowDTO,boolean isPreCheck){
        if (StringUtils.isBlank(decisionFlowDTO.getProcessContent())) {
            throw new ParseException("DecisionFlowConvertor convert error,decisionFlowUuid:" + decisionFlowDTO.getUuid());
        }

        DecisionFlow decisionFlow = new DecisionFlow();
        decisionFlow.setUuid(decisionFlowDTO.getUuid());
        decisionFlow.setPolicyUuid(decisionFlowDTO.getUuid());
        decisionFlow.setGmtModify(decisionFlowDTO.getGmtModify());
        decisionFlow.setDecisionFlowUuid(decisionFlowDTO.getUuid());

        String processXml = decisionFlowDTO.getProcessContent();
        if(!isPreCheck) {//如果是校验接口中使用，内容不压缩
            try {
                processXml = CompressUtil.ungzip(decisionFlowDTO.getProcessContent());
            } catch (Exception e) {
                logger.error(TraceUtils.getFormatTrace()+"DecisionFlowConvertor convert processContent error!decisionFlowUuid:" + decisionFlowDTO.getUuid());
                throw new ParseException("DecisionFlowConvertor convert error!Uuid:" + decisionFlowDTO.getUuid(), e);
            }
        }

        Graph graph = new Graph();
        try {

            GraphDesc graphDesc = GraphParser.parseFromXml(processXml);
            if(isPreCheck){//添加一些预校验标记，预校验情况下，部分检查将不做。
                graphDesc = addCheckFlag(graphDesc);
            }
            graph.parse(graphDesc);

        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"DecisionFlowConvertor createGraph error!decisionFlowUuid:" + decisionFlowDTO.getUuid());
            throw e;
        }

        decisionFlow.setGraph(graph);

        return decisionFlow;
    }


    /**
     * 对于边，添加一下预校验标记
     * @param graphDesc
     * @return
     */
    private GraphDesc addCheckFlag(GraphDesc graphDesc){
        List<EdgeDesc> edgeDescList = graphDesc.getEdgeList();
        if(edgeDescList == null || edgeDescList.isEmpty()){
            return graphDesc;
        }

        for(EdgeDesc edgeDesc:edgeDescList) {
            edgeDesc.putExtProperty(Constant.Flow.FLOW_PRE_CHECK,true);
        }

        return graphDesc;
    }
}
