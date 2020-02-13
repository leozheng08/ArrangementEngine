package cn.tongdun.kunpeng.api.engine.model.eventtype;

import cn.tongdun.tdframework.core.domain.EntityObject;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/11 下午1:42
 */
@Data
public class EventType extends EntityObject {

    private long id;                    /* 全局唯一ID*/

    private String            code;

    private String            name;

}
