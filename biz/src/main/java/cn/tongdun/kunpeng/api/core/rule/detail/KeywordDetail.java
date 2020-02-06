package cn.tongdun.kunpeng.api.core.rule.detail;

import lombok.Data;

import java.util.List;
import java.util.Map;

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
