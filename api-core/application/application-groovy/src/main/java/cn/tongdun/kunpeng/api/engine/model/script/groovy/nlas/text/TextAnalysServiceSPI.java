package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.text;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;

/**
 * 论坛垃圾远程接口
 *
 * @author zhanjs
 */
public interface TextAnalysServiceSPI {

    Double textModelScore(String value);

    Double anchorModelScore(String value);

    Double textModelScore(AbstractFraudContext context, String value);

    Double anchorModelScore(AbstractFraudContext context, String value);

}
