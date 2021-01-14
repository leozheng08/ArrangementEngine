package cn.tongdun.kunpeng.api.application.context;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IOutputField;
import cn.tongdun.kunpeng.client.data.impl.underline.OutputField;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:24
 */
@Data
public class FraudContext extends AbstractFraudContext {

    private List<IOutputField> outputFields = Lists.newArrayList();

    private List<Map<String, Object>> outputIndicatrixes = Lists.newArrayList();


    public void appendOutputFields(OutputField outputField) {
        //如果包含则覆盖,否则就追加
        int i = outputFields.indexOf(outputField);
        if (i > -1) {
            IOutputField exists = outputFields.get(i);
            exists.setValue(outputField.getValue());
            exists.setType(outputField.getType());
            exists.setDesc(outputField.getDesc());
        } else {
            outputFields.add(outputField);
        }
    }
}
