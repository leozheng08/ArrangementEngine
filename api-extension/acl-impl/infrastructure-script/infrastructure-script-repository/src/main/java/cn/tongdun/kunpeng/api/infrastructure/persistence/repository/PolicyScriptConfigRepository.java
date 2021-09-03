package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.script.IPolicyScriptConfigRepository;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liupei
 */
@Repository
public class PolicyScriptConfigRepository implements IPolicyScriptConfigRepository {

    @Override
    public List<String> queryByPolicyUuid(String policyUuid) {
        return Lists.newArrayList();
    }
}
