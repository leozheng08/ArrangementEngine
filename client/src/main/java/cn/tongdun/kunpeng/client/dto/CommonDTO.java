package cn.tongdun.kunpeng.client.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
/**
 * 通用信息
 */
public class CommonDTO implements Serializable {

    private static final long serialVersionUID = 1911389989055349715L;
    protected Long id;
    protected String uuid;
    private Date gmtCreate;
    private Date gmtModify;


    /**
     * 是否被审核通过，1为通过，-1为未通过，0为未审核 status
     */
    private Integer status;
    /**
     * 是否已删除 is_deleted
     */
    private boolean deleted;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 操作人
     */
    private String operator;

    public boolean isValid() {
        if (status == null || !status.equals(1)) {
            return false;
        }
        if (deleted) {
            return false;
        }

        return true;
    }
}
