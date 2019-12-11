package cn.tongdun.kunpeng.api.dataobj;


import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 应用DO公共的字段
 */
public class CommonDO implements Serializable {

    private long id;                    /* 全局唯一ID*/
    private String createdBy;           /* 创建者 */
    private Timestamp gmtCreate;        /* 创建时间 */
    private String modifiedBy;          /* 修改者 */
    private Timestamp gmtModified;        /* 最后次修改时间 */
    private String isDelete;            /* 逻辑删除, 1代表删除 */


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }
}
