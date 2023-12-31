package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

import java.util.List;
import java.util.Map;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;
/**
 * 关键词表规则详细
 * @Author: liang.chen
 * @Date: 2020/2/6 下午9:53
 */
@Data
public class KeywordDetail extends ConditionDetail {
	private String replacedText;
	private List<Map<String,Object>> data;

	public KeywordDetail(){
		super("keyword");
	}

}
