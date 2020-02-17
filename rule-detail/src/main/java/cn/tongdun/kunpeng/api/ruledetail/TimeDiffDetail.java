package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.model.IDetail;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import lombok.Data;

import java.util.Date;

/**
 * 时间差运算
 *
 * @Author: liang.chen
 * @Date: 2020/2/5 下午8:05
 */
@Data
public class TimeDiffDetail extends ConditionDetail implements DetailCallable {

    //时间A
    private Date dateA;

    //时间B
    private Date dateB;

    //时间差，毫秒数
    private Double result;

    //时间差，格式化后的显示值，例xx小时xx分钟xx秒
    private String diffDisplay;

    public TimeDiffDetail() {
        super("time_diff");
    }

    @Override
    public IDetail call() {
        return this;
    }
}
