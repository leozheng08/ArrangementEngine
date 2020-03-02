package cn.tongdun.kunpeng.client.data;

import java.io.Serializable;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 下午12:53
 */
public interface IHitRule extends Serializable {

    String getId();

    void setId(String id) ;

    String getUuid() ;

    void setUuid(String uuid) ;

    String getName() ;

    void setName(String name) ;

    String getDecision() ;

    void setDecision(String decision) ;

    Integer getScore() ;

    void setScore(Integer score) ;

    String getParentUuid() ;

    void setParentUuid(String parentUuid) ;
}
