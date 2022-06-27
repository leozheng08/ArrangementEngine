package cn.tongdun.kunpeng.api.common.data;

import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:44
 */
@Data
public class RuleResponse extends Response {

    private boolean hit;
    private String id;
    private String uuid;
    private String name;
    private String parentUuid;
    private boolean terminate;
    private boolean pilotRun = false;
}
