package cn.tongdun.kunpeng.api.engine.reload.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.SubPolicyConvertor;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.script.IDynamicScriptRepository;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyCompileManager;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyObjectCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.reload.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.DynamicScriptDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class DynamicScriptReLoadManager implements IReload<DynamicScriptDO> {

    private Logger logger = LoggerFactory.getLogger(SubPolicyReLoadManager.class);

    @Autowired
    private IDynamicScriptRepository dynamicScriptRepository;

    @Autowired
    private SubPolicyCache subPolicyCache;

    @Autowired
    GroovyCompileManager groovyCompileManager;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private GroovyObjectCache groovyObjectCache;

    @PostConstruct
    public void init(){
        reloadFactory.register(DynamicScriptDO.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(DynamicScriptDO dynamicScriptDO){
        String uuid = dynamicScriptDO.getUuid();
        logger.info("SubPolicyReLoadManager start, uuid:{}",uuid);
        try {
            Long timestamp = dynamicScriptDO.getGmtModify().getTime();
            SubPolicy oldSubPolicy = subPolicyCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(oldSubPolicy != null && oldSubPolicy.getModifiedVersion() >= timestamp) {
                return true;
            }
//
//            SubPolicyDTO subPolicyDTO = subPolicyRepository.queryByUuid(uuid);
//            SubPolicy subPolicy = subPolicyConvertor.convert(subPolicyDTO);
//            subPolicyCache.put(uuid,subPolicy);
        } catch (Exception e){
            logger.error("SubPolicyReLoadManager failed, uuid:{}",uuid,e);
            return false;
        }
        logger.info("SubPolicyReLoadManager success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param subPolicyDO
     * @return
     */
    @Override
    public boolean remove(DynamicScriptDO dynamicScriptDO){
//        try {
//            return removeSubPolicy(dynamicScriptDO.getUuid());
//        } catch (Exception e){
//            return false;
//        }
        return false;
    }
}
