package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyFieldEncryptionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hls
 * @version 1.0
 * @date 2021/10/29 4:35 下午
 */
@Mapper
public interface PolicyFieldEncryptionDAO {
    PolicyFieldEncryptionDO selectByPolicyDefinitionUuid(String uuid);
}
