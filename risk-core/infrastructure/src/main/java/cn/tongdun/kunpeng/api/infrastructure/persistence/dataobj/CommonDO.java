package cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj;


import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 应用DO公共的字段
 */
@Data
public class CommonDO implements Serializable {


    private static final long serialVersionUID = 1911389989055349715L;
    protected Long            id;
    protected String          uuid;
    protected Timestamp       gmtCreate;
    protected Timestamp       gmtModified;
}
