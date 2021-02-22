package cn.tongdun.kunpeng.api.application.content.rule.image;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.tongdun.kunpeng.api.application.content.function.image.ImageFunction;

/**
 * @description: 内容安全-图像识别
 * @author: zhongxiang.wang
 * @date: 2021-02-22 15:04
 */
public class ImageRule extends AbstractRule {
    private ImageFunction function;

    @Override
    public EvalResult run(ExecuteContext executeContext) {
        Object ret = this.function.eval(executeContext);
        return EvalResult.valueOf(ret);
    }

    @Override
    public void parse(RawRule rawRule) {
        if (null != rawRule && rawRule.getFunctionDescList() != null && !rawRule.getFunctionDescList().isEmpty()) {
            if (rawRule.getFunctionDescList().size() > 1) {
                throw new ParseException("ImageRule parse error!expect 1 FunctionDesc,but input :" + rawRule.getFunctionDescList().size());
            } else {
                this.function = (ImageFunction) FunctionLoader.getFunction((FunctionDesc) rawRule.getFunctionDescList().get(0));
            }
        } else {
            throw new ParseException("ImageRule parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }

    }
}
