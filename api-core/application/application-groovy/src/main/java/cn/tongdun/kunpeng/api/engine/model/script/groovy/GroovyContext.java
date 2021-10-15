package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import cn.fraudmetrix.module.riskbase.service.intf.CardBinNewService;
import cn.fraudmetrix.module.riskbase.service.intf.IdInfoQueryService;
import cn.fraudmetrix.module.riskbase.service.intf.MobileInfoQueryService;
import cn.tongdun.kunpeng.api.basedata.service.elfin.ElfinBaseDataService;

public class GroovyContext {


    /**
     * 不迁移，详情：http://wiki.tongdun.me/pages/viewpage.action?pageId=41340496
     */
//    private DistrictQueryService districtQueryService;
//    private BinInfoQueryService binInfoQueryService;
//    private GaeaCallerService       gaeaCallerService;
    //    private IModelService           modelService;            //模型计算服务

    private MobileInfoQueryService mobileInfoQueryService;

    private IdInfoQueryService idInfoQueryService;

    private CardBinNewService cardBinNewService;  //银行卡归属地查询服务

    private ElfinBaseDataService elfinBaseDataService;

    public CardBinNewService getCardBinNewService() {
        return cardBinNewService;
    }

    public void setCardBinNewService(CardBinNewService cardBinNewService) {
        this.cardBinNewService = cardBinNewService;
    }

    public ElfinBaseDataService getElfinBaseDataService() {
        return elfinBaseDataService;
    }

    public void setElfinBaseDataService(ElfinBaseDataService elfinBaseDataService) {
        this.elfinBaseDataService = elfinBaseDataService;
    }

    public MobileInfoQueryService getMobileInfoQueryService() {
        return mobileInfoQueryService;
    }

    public void setMobileInfoQueryService(MobileInfoQueryService mobileInfoQueryService) {
        this.mobileInfoQueryService = mobileInfoQueryService;
    }

    public IdInfoQueryService getIdInfoQueryService() {
        return idInfoQueryService;
    }

    public void setIdInfoQueryService(IdInfoQueryService idInfoQueryService) {
        this.idInfoQueryService = idInfoQueryService;
    }
}
