package cn.tongdun.kunpeng.api.engine.load;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午4:30
 */
public interface ILoadByPartner extends ILoad {
    boolean loadByPartner(String partnerCode);
}
