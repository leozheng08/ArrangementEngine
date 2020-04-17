package cn.tongdun.kunpeng.api.engine.model;

import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.constant.DeleteStatusEnum;
import lombok.Data;

/**
 * 有状态标识的实体
 * @Author: liang.chen
 * @Date: 2020/3/27 下午1:55
 */
@Data
public class StatusEntity extends VersionedEntity {

    /**
     * 是否被审核通过，1为通过，-1为未通过，0为未审核 status
     */
    private Integer status;
    /**
     * 是否已删除 is_deleted
     */
    private boolean deleted;

    public boolean isValid(){
        if(status != null && status.equals(CommonStatusEnum.CLOSE.getCode())) {
            return false;
        }
        if(DeleteStatusEnum.INVALID.getCode() == deleted) {
            return false;
        }

        return true;
    }
}
