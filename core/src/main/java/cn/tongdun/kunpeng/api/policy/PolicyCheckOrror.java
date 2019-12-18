package cn.tongdun.kunpeng.api.policy;

/**
 * 导致策略加载失败的各类异常
 */
public enum PolicyCheckOrror {
    //子策略为空
    SUB_POLICY_EMPTY,
    //规则为空
    RULE_EMPTY;
}
