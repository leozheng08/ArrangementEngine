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
import java.io.IOException;
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

    public static void main(String[] args) throws IOException {
        String processContent = "H4sIAAAAAAAAAM1YXW/cRBR9z69YGalPnV17/L3ppqIkRUhJWjUtQkIoGs9cb916bdcz3qSgSkhA\\naalAKmp5QkV9QO0DqEiA+lKJP0Oz4al/gfHHbuMlu9ndJBIva2t875lzz71j37vnzu/2wkYfUh7E\\nUUfRmqrSgIjGLIi6HeXa1YvIUc6vLC2d85JehNsM/CAKhLTlDekY8Xax3lGuC5G0W62dnZ1m3Os2\\n47Tb4gnQ1oXLG5strGqqamKjtXFpdW1dKT1rPje8mPPCi6VxHHLlADoLZoVf/WDox+hkn9XVAx7v\\njTym7FLzGO1xg/RJPQi50KRxr5XfXL2dwCgMMWO4u7zOYkcvjLCqaq2PNta36HXoERREXJCIgszU\\nbhKnAtKOIs2aQMMg4dAsUtLsxQxCSCWh9IDlh28zjZt682IQkRD1ZXSWirGFNBvr6IJrFw4p8Nx2\\nnUTdjHShxqzXh7DklldMwDrK6qg0lIYgaRfEJukBTwiFI2IXUqpDNzlUUB60eaHDekyJKGKZo/ga\\n+RpWm7ucNaaQapSXcTMv4UGvxC8o8JY8MI1yVZoqK0uNRnVSkjSmUr9CGtuklkYMYlgmMXTieBhr\\ntqkaGrVMW1NdKSBf2wWaCeKFUgKRZjJlkZSvo6RZCBfDeEdpVIi5Dh3lchr0iZBWsrTaUuObUrvN\\nwkEeUZKF4nK5VlAakZJ1k4q1PkRieAbL3G2N1rc9UC1HJYAs19SRAbaKXNc2kWsSjB1ieQCsAh3B\\nxpnoxvKFsbIFtzL59igIbztUAxdrPtId30GGQzByKVORqXsybsv0DYuca40hlGxb43RrUXgZDyKp\\nxBUpzVXCb9ZjuTD2dFtnpga+5iFsqCAjUj3k+QYgSoAQSwWfuVqp4xA4dyvF3Hv+9PWTB2dCsdzy\\n0jNdsZzfypqOGlzclqk6cyuLxbIfRwLx4FNoazjZXaZxGKftd1zXLR/nfntfPdt/8u3g2cO9bz4f\\nfPfbP18/3P/j5eDR7/svXv7919PBo+f7L758fe9uuV9fVtX24P69vR9/wcXe+Y45SkmzONk5x47y\\nWblDlgWsvGuXFzA0ZhPL1VyCLVPXytWz5SWIkkzwyv7jCsIPIKxjUBKG7/aEXfNNg+51cTG3zeuw\\nZn8A4T/G8wDnytfsy987Z49iqhmnRXUi8vxcuxFLT5zmFNAF1Ax7/HTyPh14fqbAshMnORlzAX40\\nEjJgXYtPnuZR0POzjSlNxIkTnYY6P8cgOYVDPgV04dNzGu+iI5AXr081OLX6nAg9P1uSJJtZbx2i\\nSQQWZ3sk9CS2n1SWsn+Z7Zuasm1OJ72rx023hpajfSLY2ci//pVl3ihWT4qmoOruazi4tlnArwT8\\n5hak/YDCpYJ1Ze2TkMOdssMYNp3vp3GWdBTPUnUVdGL6gA2XYtfSPMfTbFAZJQRgvB8M5ADZO1Y/\\nOEKYpdE0bdtjYDDk2SpBhsoI8rAEdk3fBB+Db9EjGs3xjvI47aalYRmQYyKqmR4ysG4hB1NAvqNi\\nzzAcm6pkUrs5+PWHweOfF+0x37y69/ru94Mvftr788H+i8dvXt0/pHkcS63hYEfVfJ+A6RjUlOn0\\nbId5xNIx0wzqz5baeTIwV2oxkbXGbIzANS1kWC5GxDcxMuT4JMuJgqzLY6RWHvhDpqG1anXb84D5\\nRNOR6jE5CzlgygCBIR0Y1kzXs3XmzSbQPHHUBRquDqnW57kDe4xNdPOePKXB4yylcAX8+efBauAv\\nfI8xf4nRQYiyMKxm2zSI00Dc7iiVEY0jVs7Abwv6WpbHrKyMBscD0S8i2Cz1XBNs0aCnyDb7e+R/\\nI9ssVT5dthmDfivbfGf1dOUarld/1KwsDRcO/Gu6svQv1niuWnIVAAA=";
        String diagramContent = "H4sIAAAAAAAAALWWS4/aMBDH73yKKHcTx3ZsBxUOFCr10Ap1e0d+jAPaPCgJZfvtax4rIFoko929WZP8xzO/eSSDL3pT1XY9mi5+/JytVbFVVfRSlXU7Oj0Yx6uu24ySZL/fD5uqGDbbImk3YJKDIiE4xTgjLJl9j6O1HcdXfpZpPBlE0fUNi1LVEB0M8xIqqLtxbMGpXdktlHlWBQwtVM3F0/H95WLbGGjbs79bj08rtel5fOrUtpv/9eelMynklAtEGcOICZUiTXiOUksl11wQSMnltqOvMM0pEB+KNaNps6tte6ZmzX1is9kVr69xtF/bbjWOGY6jFayLVXc6v4zjNOdx9G8c05THyTnppJ91EIzprl3XHt6vXQm/Vfu8ZNopIzVH4DD16XGGtMksAuy4yYjkgGkfSZDmI5F4y4UJPzGhVJ6YYPnRTExuhHYgEYVcIaYzgnJBHeJWEmGNyYQRfSZBms9mkqWvfYLfyWRe29PIEC1M7kSKMmoZYtYKlGObIw0aMqEkkZr1WQRpPnlkeE7OKB5qj7kt+usD/uygNvCtbPbLVAtuGTEImC8wo8YhCZnPTGonASTwXF9oHJwFStpmtzVwacnH1k/kV1wB3RvysFG9IXOI+mEw0mnHvE9ECYBvf+aQtplAUmqsncbEWtIDEya5CyZscd0FEzav7wfjiADiY0WHOh/KT5GSXPnyG6f8/YSTfseESe6CCdted8GEDe+bYG6Mx2/1ZHBjO/8JTP4Dx2wQWmcIAAA=";
        processContent = processContent.replaceAll("\\\\n","");
        System.out.println(CompressUtil.ungzip(processContent));
    }
}
