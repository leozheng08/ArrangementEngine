package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.script.DynamicScript;
import cn.tongdun.kunpeng.api.engine.model.script.IDynamicScriptRepository;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyCompileManager;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyObjectCache;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.WrappedGroovyObject;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;
import cn.tongdun.kunpeng.share.dataobject.DynamicScriptDO;
import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
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

    @Override
    public boolean create(DynamicScriptDO dynamicScriptDO){
        return addOrUpdate(dynamicScriptDO);
    }
    @Override
    public boolean update(DynamicScriptDO dynamicScriptDO){
        return addOrUpdate(dynamicScriptDO);
    }
    @Override
    public boolean activate(DynamicScriptDO dynamicScriptDO){
        return addOrUpdate(dynamicScriptDO);
    }

    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(DynamicScriptDO dynamicScriptDO){
        String uuid = dynamicScriptDO.getUuid();
        logger.debug("DynamicScript reload start, uuid:{}",uuid);
        try {
            Long timestamp = dynamicScriptDO.getGmtModify().getTime();
            WrappedGroovyObject oldWrappedGroovyObject = groovyObjectCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldWrappedGroovyObject != null && oldWrappedGroovyObject.getModifiedVersion() >= timestamp) {
                logger.debug("DynamicScript reload localCache is newest, ignore uuid:{}",uuid);
                return true;
            }

            DynamicScript dynamicScript = dynamicScriptRepository.queryByUuid(uuid);
            //如果失效则删除缓存
            if(dynamicScript == null || CommonStatusEnum.CLOSE.getCode() == dynamicScript.getStatus()){
                return remove(dynamicScriptDO);
            }

            groovyCompileManager.addOrUpdate(dynamicScript);
        } catch (Exception e){
            logger.error("DynamicScript reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("DynamicScript reload success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param dynamicScriptDO
     * @return
     */
    @Override
    public boolean remove(DynamicScriptDO dynamicScriptDO){
        try {
            groovyCompileManager.remove(dynamicScriptDO.getUuid());
        } catch (Exception e){
            logger.error("DynamicScript remove failed, uuid:{}",dynamicScriptDO.getUuid(),e);
            return false;
        }
        logger.debug("DynamicScript remove success, uuid:{}",dynamicScriptDO.getUuid());
        return true;
    }


    /**
     * 关闭状态
     * @param dynamicScriptDO
     * @return
     */
    @Override
    public boolean deactivate(DynamicScriptDO dynamicScriptDO){
        return remove(dynamicScriptDO);
    }
}
