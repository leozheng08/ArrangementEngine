package cn.tongdun.kunpeng.api.dataobject;

import java.io.Serializable;
import java.sql.Timestamp;

public class CommonDO implements Serializable {

    private static final long serialVersionUID = 1911389989055349715L;
    protected Long            id;
    protected String          uuid;
    protected Timestamp       gmtCreate;
    protected Timestamp       gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

}
