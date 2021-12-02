package cn.tongdun.kunpeng.api.intf.adapter.rest;

import cn.tongdun.kunpeng.api.util.ReloadData;
import cn.tongdun.kunpeng.api.engine.reload.IPartnerClusterReloadManager;
import cn.tongdun.kunpeng.api.util.PartnerClusterResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author: mengtao
 * @create: 2021-12-01 15:25
 */

@Controller
@RequestMapping(value = "/partner_cluster")
public class PartnerClusterController {

    @Autowired
    private IPartnerClusterReloadManager reloadManager;

    @RequestMapping(value = "/reload")
    @ResponseBody
    public PartnerClusterResponse reload(@RequestBody ReloadData reloadData){
        //校验参数
        if(StringUtils.isEmpty(reloadData.getPartnerCode()) || null == reloadData.getIsCreate()){
            return new PartnerClusterResponse(false);
        }
        return reloadManager.reload(reloadData);
    }
}
