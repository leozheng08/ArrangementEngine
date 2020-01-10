package cn.tongdun.kunpeng.api.intf.ip.entity;

import java.io.Serializable;
import java.util.List;

/**
 * PassiveDnsObjs
 *
 * @author pandy(潘清剑)
 * @date 16/7/29
 */
public class PassiveDnsObjs extends UpdateTime implements Serializable {
    private List<PassiveDnsObj> passiveDnsObjs;

    public List<PassiveDnsObj> getPassiveDnsObjs() {
        return passiveDnsObjs;
    }

    public void setPassiveDnsObjs(List<PassiveDnsObj> passiveDnsObjs) {
        this.passiveDnsObjs = passiveDnsObjs;
    }

}
