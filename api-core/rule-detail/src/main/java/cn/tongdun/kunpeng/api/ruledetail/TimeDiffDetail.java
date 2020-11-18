package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

import java.util.Date;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;

/**
 * 时间差运算
 * @Author: liang.chen
 * @Date: 2020/2/5 下午8:05
 */
@Data
public class TimeDiffDetail extends ConditionDetail {

    /**
     * 时间A
     */
    private Date dateA;
    /**
     * 时间B
     */
    private Date dateB;
    /**
     * 时间差，毫秒数
     */
    private Double result;
    /**
     * 时间差，格式化后的显示值，例xx小时xx分钟xx秒
     */
    private String diffDisplay;

    public TimeDiffDetail(){
        super("time_diff");
    }
}
