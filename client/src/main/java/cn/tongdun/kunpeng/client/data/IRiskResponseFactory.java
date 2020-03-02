package cn.tongdun.kunpeng.client.data;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 上午10:05
 */
public interface IRiskResponseFactory {
    IRiskResponse newRiskResponse();
    IHitRule newHitRule();
    IOutputField newOutputField();
    ISubPolicyResult newSubPolicyResult();
}
