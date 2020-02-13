package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DynamicScriptDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DynamicScriptDOMapper {



    /**
     * 根据条件查询
     * 合作方指定事件类型
     *     context.getPartnerCode() + context.getAppName() + context.getEventType();
     * 合作方全部事件类型
     *     ontext.getPartnerCode() + context.getAppName() + "All";
     * 全局指定事件类型
     *     "All" + "All" + context.getEventType();
     * 全局全部事件类型
     *     "All" + "All" + "All";
     * @param partnerCode
     * @param appName
     * @param eventType
     * @return
     */
    List<DynamicScriptDO> selectByQuery(String partnerCode,String appName, String eventType);

    /**
     * 根据uuid查询
     * @param uuid
     * @return
     */
    DynamicScriptDO selectByUuid(@Param("uuid") String uuid);
}