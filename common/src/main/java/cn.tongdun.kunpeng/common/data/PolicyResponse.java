package cn.tongdun.kunpeng.common.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:44
 */
@Data
public class PolicyResponse extends Response{

    private String policyUuid;
    private String policyName;
    private String riskType;


    private List<SubPolicyResponse> subPolicyList = new ArrayList<>();

    public void addSubPolicy(SubPolicyResponse subPolicyResponse){
        subPolicyList.add(subPolicyResponse);
    }
}
