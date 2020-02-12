package cn.tongdun.kunpeng.api.engine.service;

import cn.fraudmetrix.elfin.biz.entity.IdcEntity;
import cn.fraudmetrix.elfin.biz.entity.PhoneAttrEntity;
import cn.fraudmetrix.elfin.biz.entity.StationEntity;
import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.fraudmetrix.module.riskbase.object.MobileInfoDO;

import java.util.List;
import java.util.Map;

/**
 * Created by coco on 17/12/26.
 */
public interface ElfinBaseDataService {

    /**
     * 查询GeoIP数据
     *
     * @param ip IP地址
     * @return
     */
    GeoipEntity getIpInfo(String ip);

    /**
     * 批量调用，查询GeoIP数据
     *
     * @param ips ip地址列表
     * @return
     */
    Map<String, GeoipEntity> getIpInfos(List<String> ips);

    /**
     * 查询基站信息
     *
     * @param ip IP地址
     * @return
     * @throws Exception
     */
    StationEntity getStationInfo(String ip);

    /**
     * 判断某个IP是否是基站
     *
     * @param ip IP地址
     * @return
     * @throws Exception
     */
    boolean isStation(String ip);

    /**
     * 查询IDC信息
     *
     * @param ip ip地址
     * @return
     * @throws Exception
     */
    IdcEntity getIdcInfo(String ip);

    /**
     * 判断一个IP是否是IDC
     *
     * @param ip IP地址
     * @return
     * @throws Exception
     */
    boolean isIdc(String ip);

    /**
     * 查询手机归属地信息
     *
     * @param phone 手机号
     * @return
     * @throws Exception
     */
    PhoneAttrEntity getPhoneInfo(String phone);

    MobileInfoDO getMobileInfo(String phone);

}

