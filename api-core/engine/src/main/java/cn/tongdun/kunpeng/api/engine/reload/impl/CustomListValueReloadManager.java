package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValue;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValueCache;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ICustomListValueRepository;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.CustomListValueEventDO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by liupei on 2021/3/17.
 */
@Component
public class CustomListValueReloadManager implements IReload<CustomListValueEventDO> {

    private Logger logger = LoggerFactory.getLogger(CustomListValueReloadManager.class);


    @Autowired
    private ICustomListValueRepository customListValueRepository;

    @Autowired
    private CustomListValueCache customListValueCache;


    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init() {
        reloadFactory.register(CustomListValueEventDO.class, this);
    }

    @Override
    public boolean create(CustomListValueEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean update(CustomListValueEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean activate(CustomListValueEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean imported(CustomListValueEventDO eventDO) {
        return addOrUpdate(eventDO);
    }


    /**
     * 更新事件类型
     *
     * @return
     */
    public boolean addOrUpdate(CustomListValueEventDO eventDO) {
        String uuid = eventDO.getUuid();
        logger.debug(TraceUtils.getFormatTrace() + "CustomListValueEventDO reload start, uuid:{}", uuid);
        try {

            Long timestamp = eventDO.getModifiedVersion();
            double oldScore = customListValueCache.getZsetScore(eventDO.getCustomListUuid(), eventDO.getDataValue());
            //缓存中的数据是相同版本或更新的，则不刷新
            if (timestamp != null && timestampCompare((long) oldScore, timestamp) >= 0) {
                logger.debug(TraceUtils.getFormatTrace() + "CustomListValueEventDO reload localCache is newest, ignore uuid:{}", uuid);
                return true;
            }

            //设置要查询的时间戳，如果redis缓存的时间戳比这新，则直接按redis缓存的数据返回
            ThreadContext.getContext().setAttr(ReloadConstant.THREAD_CONTEXT_ATTR_MODIFIED_VERSION, timestamp);
            CustomListValue customListValue = customListValueRepository.selectByUuid(uuid);
            //如果失效则删除缓存
            if (customListValue == null || !customListValue.isValid()) {
                return remove(eventDO);
            }
            customListValueCache.zadd(customListValue.getCustomListUuid(), customListValue.getExpireTime(), customListValue.getValue());
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "CustomListValueEventDO reload failed, uuid:{}", uuid, e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "CustomListValueEventDO reload success, uuid:{}", uuid);

        return true;
    }


    /**
     * 删除事件类型
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(CustomListValueEventDO eventDO) {
        if (StringUtils.isAnyBlank(eventDO.getDataValue(), eventDO.getCustomListUuid())) {
            return true;
        }

        try {
            customListValueCache.zrem(eventDO.getCustomListUuid(), eventDO.getDataValue());
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "CustomListValue remove failed, uuid:{}", eventDO.getUuid(), e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "CustomListValue remove failed, uuid:{}", eventDO.getUuid());
        return true;
    }

    /**
     * 关闭状态
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(CustomListValueEventDO eventDO) {
        return true;
    }


    /**
     * 更改策略执行模式
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean switchDecisionMode(CustomListValueEventDO eventDO) {
        return true;
    }


}
