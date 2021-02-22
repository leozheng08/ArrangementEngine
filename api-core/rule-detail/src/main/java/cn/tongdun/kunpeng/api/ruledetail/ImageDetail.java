package cn.tongdun.kunpeng.api.ruledetail;


import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;
import lombok.Data;

import java.util.List;

/**
 * @description: 图片识别详情
 * @author: zhongxiang.wang
 * @date: 2021-02-22 11:20
 */
@Data
public class ImageDetail extends ConditionDetail {
    private List<List<FilterConditionDO>> hitConditions;
    public ImageDetail(){super("image");}

}
