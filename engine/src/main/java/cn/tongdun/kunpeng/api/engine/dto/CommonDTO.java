package cn.tongdun.kunpeng.api.engine.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommonDTO implements Serializable {

    private static final long serialVersionUID = 1911389989055349715L;
    protected Long            id;
    protected String          uuid;
    private Date gmtCreate;
    private Date gmtModify;
}
