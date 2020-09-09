package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

import java.util.List;

/**
 * 个人模糊自定义列表详情
 *
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class PersonalFuzzyListDetail extends ConditionDetail {
    /**
     * 匹配上的列表值
     */
    private List<String> list;
    /**
     * 性别
     */
    private String gender;
    /**
     * 姓名
     */
    private String name;
    /**
     * 姓名权重
     */
    private String nameWeight;
    /**
     * 生日
     */
    private String birthday;
    /**
     * 生日权重
     */
    private String birthdayWeight;
    /**
     * 相似度
     */
    private String similarity;

    private Boolean isMatch;

    public PersonalFuzzyListDetail() {
        super("personal_fuzzy_list");
    }
}
