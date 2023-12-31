package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.acl.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.PartnerDTO;
import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PartnerEventDO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class PartnerReLoadManager implements IReload<PartnerEventDO> {

    private Logger logger = LoggerFactory.getLogger(PartnerReLoadManager.class);

    @Autowired
    private IPartnerRepository partnerRepository;

    @Autowired
    private PartnerCache partnerCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(PartnerEventDO.class,this);
    }

    @Override
    public boolean create(PartnerEventDO partnerDO){
        return addOrUpdate(partnerDO);
    }
    @Override
    public boolean update(PartnerEventDO partnerDO){
        return addOrUpdate(partnerDO);
    }
    @Override
    public boolean activate(PartnerEventDO partnerDO){
        return addOrUpdate(partnerDO);
    }

    /**
     * 更新
     * @return
     */
    public boolean addOrUpdate(PartnerEventDO partnerDO){
        String partnerCode = partnerDO.getPartnerCode();
        logger.debug(TraceUtils.getFormatTrace()+"PartnerEventDO reload start, partnerCode:{}",partnerCode);
        try {
            Long timestamp = partnerDO.getModifiedVersion();
            Partner oldPartner = partnerCache.get(partnerCode);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldPartner != null && timestampCompare(oldPartner.getModifiedVersion(), timestamp) >= 0) {
                logger.debug(TraceUtils.getFormatTrace()+"PartnerEventDO reload localCache is newest, ignore partnerCode:{}",partnerCode);
                return true;
            }

            //设置要查询的时间戳，如果redis缓存的时间戳比这新，则直接按redis缓存的数据返回
            ThreadContext.getContext().setAttr(ReloadConstant.THREAD_CONTEXT_ATTR_MODIFIED_VERSION,timestamp);
            PartnerDTO partnerDTO = partnerRepository.queryByPartnerCode(partnerCode);
            Partner newPartner = null;
            if(partnerDTO != null) {
                newPartner = new Partner();
                BeanUtils.copyProperties(partnerDTO,newPartner);
            }

            //如果失效则删除缓存
            if(newPartner == null || !newPartner.isValid()){
                return remove(partnerDO);
            }

            partnerCache.put(partnerCode, newPartner);
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"PartnerEventDO reload failed, partnerCode:{}",partnerCode,e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"PartnerEventDO reload success, partnerCode:{}",partnerCode);
        return true;
    }


    /**
     * 删除事件类型
     * @param partner
     * @return
     */
    @Override
    public boolean remove(PartnerEventDO partner){
        try {
            partnerCache.remove(partner.getPartnerCode());
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"PartnerEventDO remove failed, uuid:{}",partner.getPartnerCode(),e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"PartnerEventDO remove success, partnerCode:{}",partner.getPartnerCode());
        return true;
    }

    /**
     * 关闭状态
     * @param partner
     * @return
     */
    @Override
    public boolean deactivate(PartnerEventDO partner){
        return remove(partner);
    }

}
