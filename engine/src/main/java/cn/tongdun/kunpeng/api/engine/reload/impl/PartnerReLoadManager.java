package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.InterfaceDefinitionDO;
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
public class PartnerReLoadManager implements IReload<Partner> {

    private Logger logger = LoggerFactory.getLogger(PartnerReLoadManager.class);

    @Autowired
    private IPartnerRepository partnerRepository;

    @Autowired
    private PartnerCache partnerCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(Partner.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(Partner partnerDO){
        String partnerCode = partnerDO.getPartnerCode();
        logger.debug("PartnerReLoadManager start, partnerCode:{}",partnerCode);
        try {
            Long timestamp = partnerDO.getGmtModify().getTime();
            Partner oldPartner = partnerCache.get(partnerCode);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldPartner != null && oldPartner.getModifiedVersion() >= timestamp) {
                return true;
            }

            Partner newPartner = partnerRepository.queryByPartnerCode(partnerCode);
            //如果失效则删除缓存
            if(newPartner == null || CommonStatusEnum.CLOSE.getCode() == newPartner.getStatus()){
                return remove(partnerDO);
            }

            partnerCache.put(partnerCode, newPartner);
        } catch (Exception e){
            logger.error("PartnerReLoadManager failed, partnerCode:{}",partnerCode,e);
            return false;
        }
        logger.debug("PartnerReLoadManager success, partnerCode:{}",partnerCode);
        return true;
    }


    /**
     * 删除事件类型
     * @param partner
     * @return
     */
    @Override
    public boolean remove(Partner partner){
        try {
            partnerCache.remove(partner.getPartnerCode());
        } catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 关闭状态
     * @param partner
     * @return
     */
    @Override
    public boolean deactivate(Partner partner){
        return remove(partner);
    }

}
