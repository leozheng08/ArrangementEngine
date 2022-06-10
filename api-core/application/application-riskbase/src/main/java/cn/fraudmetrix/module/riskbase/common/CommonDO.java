package cn.fraudmetrix.module.riskbase.common;

import java.io.Serializable;
import java.sql.Timestamp;

public class CommonDO implements Serializable {

    private static final long serialVersionUID = 3860268582917734008L;

    protected Long id;
    private String uuid;
    protected Timestamp gmtCreate;
    protected Timestamp gmtModified;

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
