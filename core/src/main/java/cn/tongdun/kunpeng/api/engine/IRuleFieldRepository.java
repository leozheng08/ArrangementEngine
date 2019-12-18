package cn.tongdun.kunpeng.api.engine;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IRuleFieldRepository {


    List<RuleField> queryByParams(RuleField field);
}
