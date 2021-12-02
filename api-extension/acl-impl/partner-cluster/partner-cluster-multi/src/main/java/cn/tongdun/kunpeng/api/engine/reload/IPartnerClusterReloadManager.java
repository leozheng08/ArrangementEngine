package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.api.util.ReloadData;
import cn.tongdun.kunpeng.api.util.PartnerClusterResponse;

/**
 * @author: mengtao
 * @create: 2021-12-01 15:27
 */

public interface IPartnerClusterReloadManager {

    /**
     * 刷新各机器缓存，创建或者删除
     * @param reloadData
     * @return
     */
    PartnerClusterResponse reload(ReloadData reloadData);
}
