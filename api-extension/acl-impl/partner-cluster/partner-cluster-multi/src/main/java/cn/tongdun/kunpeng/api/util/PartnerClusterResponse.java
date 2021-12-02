package cn.tongdun.kunpeng.api.util;

import cn.tongdun.kunpeng.api.common.data.Response;
import lombok.Data;

/**
 * @author: mengtao
 * @create: 2021-12-01 16:00
 */

@Data
public class PartnerClusterResponse extends Response {

    public PartnerClusterResponse(boolean success){
        this.setSuccess(success);
    }
}
