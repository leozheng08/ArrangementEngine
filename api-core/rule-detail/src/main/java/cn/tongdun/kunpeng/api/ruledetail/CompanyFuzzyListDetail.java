package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

import java.util.List;

/**
 * 企业模糊自定义列表详情
 *
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class CompanyFuzzyListDetail extends ConditionDetail {
    /**
     * 匹配上的列表值
     */
    private List<String> list;
    /**
     * 企业注册证件号
     */
    private String no;
    /**
     * 企业名称
     */
    private String name;
    /**
     * 企业名称相似度
     */
    private String nameimilarity;

    public CompanyFuzzyListDetail() {
        super("company_fuzzy_list");
    }
}
