package cn.tongdun.kunpeng.api.intf.adapter.rest;

import cn.tongdun.kunpeng.api.engine.reload.IPartnerClusterReloadManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: mengtao
 * @create: 2021-12-01 15:25
 */

@RestController
@RequestMapping( "/partner_cluster")
public class PartnerClusterController {

    @Autowired
    private IPartnerClusterReloadManager reloadManager;

    @RequestMapping(value = "reload", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Boolean reload(String partnerCode,Integer isCreate){
        //校验参数
        if(StringUtils.isEmpty(partnerCode) || null == isCreate){
            return false;
        }
        return reloadManager.reload(partnerCode,isCreate);
    }
}
