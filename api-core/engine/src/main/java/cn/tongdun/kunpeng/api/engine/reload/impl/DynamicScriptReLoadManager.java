package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.api.engine.model.script.DynamicScript;
import cn.tongdun.kunpeng.api.engine.model.script.IDynamicScriptRepository;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyCompileManager;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyObjectCache;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.WrappedGroovyObject;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.DynamicScriptEventDO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
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
public class DynamicScriptReLoadManager implements IReload<DynamicScriptEventDO> {

    private Logger logger = LoggerFactory.getLogger(DynamicScriptReLoadManager.class);

    @Autowired
    private IDynamicScriptRepository dynamicScriptRepository;

    @Autowired
    GroovyCompileManager groovyCompileManager;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private GroovyObjectCache groovyObjectCache;

    @PostConstruct
    public void init() {
        reloadFactory.register(DynamicScriptEventDO.class, this);
    }

    @Override
    public boolean create(DynamicScriptEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean update(DynamicScriptEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean activate(DynamicScriptEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    /**
     * 更新事件类型
     *
     * @return
     */
    public boolean addOrUpdate(DynamicScriptEventDO eventDO) {
        String uuid = eventDO.getUuid();
        logger.debug(TraceUtils.getFormatTrace() + "DynamicScript reload start, uuid:{}", uuid);
        try {
            Long timestamp = eventDO.getModifiedVersion();
            WrappedGroovyObject oldWrappedGroovyObject = groovyObjectCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if (timestamp != null && oldWrappedGroovyObject != null &&
                    timestampCompare(oldWrappedGroovyObject.getModifiedVersion(), timestamp) >= 0) {
                logger.debug(TraceUtils.getFormatTrace() + "DynamicScript reload localCache is newest, ignore uuid:{}", uuid);
                return true;
            }

            //设置要查询的时间戳，如果redis缓存的时间戳比这新，则直接按redis缓存的数据返回
            ThreadContext.getContext().setAttr(ReloadConstant.THREAD_CONTEXT_ATTR_MODIFIED_VERSION, timestamp);
            DynamicScript dynamicScript = dynamicScriptRepository.queryByUuid(uuid);
            //如果失效则删除缓存
            if (dynamicScript == null || !dynamicScript.isValid()) {
                return remove(eventDO);
            }
            groovyCompileManager.addOrUpdate(dynamicScript);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "DynamicScript reload failed, uuid:{}", uuid, e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "DynamicScript reload success, uuid:{}", uuid);
        return true;
    }


    /**
     * 删除事件类型
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(DynamicScriptEventDO eventDO) {
        try {
            groovyCompileManager.remove(eventDO.getUuid());
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "DynamicScript remove failed, uuid:{}", eventDO.getUuid(), e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "DynamicScript remove success, uuid:{}", eventDO.getUuid());
        return true;
    }


    /**
     * 关闭状态
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(DynamicScriptEventDO eventDO) {
        return remove(eventDO);
    }
}
