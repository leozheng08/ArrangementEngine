package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

import java.util.List;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;
/**
 * 自定义列表详情
 *
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class CustomListDetail extends ConditionDetail {
    /**
     * 匹配上的列表值
     */
    private List<String> list;
    /**
     * 匹配字段
     */
    private String dimType;
    /**
     * 匹配字段显示名称
     */
    private String dimTypeDisplayName;
    /**
     * 匹配字段值
     */
    private String dimValue;

    public CustomListDetail() {
        super("custom_list");
    }
}
