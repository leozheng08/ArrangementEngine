package cn.tongdun.kunpeng.api.intf.elfin;


import java.util.List;
import java.util.Map;

/**
 * 项目: elfin
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2017/4/10 下午3:37
 * 描述:
 */
public interface BaseDataQueryService {

    /**
     * 批量查询IPDB数据中关联的地理位置信息, 支持IPv4和IPv6混编
     * @param listIp IP地址列表
     * @return
     * @throws Exception
     */
    Map<String,GeoipEntity> getIpInfos(List<String> listIp) throws Exception;

    /**
     * 查询IPDB数据中IPv4 或 IPv6 关联的地理位置信息
     * @param ip IP地址
     * @return
     */
    GeoipEntity getIpInfo(String ip) throws Exception;

    /**
     * 查询基站信息
     * @param ip IP地址
     * @return
     * @throws Exception
     */
    StationEntity getStationInfo(String ip) throws Exception;

    /**
     * 判断某个IP是否是基站
     * @param ip IP地址
     * @return
     * @throws Exception
     */
    boolean isStation(String ip) throws Exception;

    /**
     * 查询IDC信息
     * @param ip ip地址
     * @return
     * @throws Exception
     */
    IdcEntity getIdcInfo(String ip) throws Exception;

    /**
     * 判断一个IP是否是IDC
     * @param ip IP地址
     * @return
     * @throws Exception
     */
    boolean isIdc(String ip) throws Exception;

    /**
     * 查询手机归属地信息
     * @param phone 手机号
     * @return
     * @throws Exception
     */
    PhoneAttrEntity getPhoneInfo(String phone) throws Exception;

    /**
     * 身份证归属地查询
     * @param idNum 身份证号码
     * @return
     * @throws Exception
     */
    IdCardEntity getIdCardInfo(String idNum) throws Exception;

    /**
     * 银行卡归属地查询
     * @param bankNum 银行卡号
     * @return
     * @throws Exception
     */
    BankCardEntity getBankCardInfo(String bankNum) throws Exception;
}
