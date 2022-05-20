package cn.fraudmetrix.module.riskbase.dao;

import cn.fraudmetrix.module.riskbase.common.CommonDao;
import cn.fraudmetrix.module.riskbase.object.MobileInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//TODO 兼容过度阶段脚本，线上稳定后删除即可
public interface MobileInfoDao extends CommonDao<MobileInfoDO> {

    Integer batchInsertFor17mon(List<MobileInfoDO> mobileInfo);

    public Integer dropTable(@Param("tableName") String tableName);

    /**
     * 创建17mon临时表
     */
    public Integer createTable();

    public Integer createMobileIndex();

    /**
     * 重命名17mon表
     */
    public Integer renameTable();
}
