package cn.tongdun.kunpeng.api.engine.reload;

/**
 * @author: mengtao
 * @create: 2021-12-01 15:27
 */

public interface IPartnerClusterReloadManager {

    /**
     * 刷新各机器缓存，创建或者删除
     * @param partnerCode
     * @param isCreate
     * @return
     */
    Boolean reload(String partnerCode,String cluster,Integer isCreate);
}
