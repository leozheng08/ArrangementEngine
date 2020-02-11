package cn.tongdun.kunpeng.api.engine;

import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyManager;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.SubPolicyResponse;

import java.util.concurrent.Callable;

/**
 * 子策略异常执行任务
 * @Author: liang.chen
 * @Date: 2019/12/17 下午3:53
 */
public class SubPolicyExecuteAsyncTask implements Callable<SubPolicyResponse> {


    private String       subPolicyUuid;
    private AbstractFraudContext context;
    private SubPolicyManager subPolicyManager;

    public SubPolicyExecuteAsyncTask(SubPolicyManager subPolicyManager,String subPolicyUuid, AbstractFraudContext context){
        this.subPolicyManager = subPolicyManager;
        this.subPolicyUuid = subPolicyUuid;
        this.context = context;
    }

    @Override
    public SubPolicyResponse call() throws Exception {
        SubPolicyResponse subPolicyResponse = subPolicyManager.execute(subPolicyUuid, context);
        return subPolicyResponse;
    }
}
