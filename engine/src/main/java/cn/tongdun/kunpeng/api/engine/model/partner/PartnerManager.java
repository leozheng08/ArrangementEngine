package cn.tongdun.kunpeng.api.engine.model.partner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @Author: liang.chen
 * @Date: 2019/12/16 下午7:58
 */
@Component
public class PartnerManager {

    @Autowired
    PartnerCache partnerCache;
}
