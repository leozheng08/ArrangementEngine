package cn.tongdun.kunpeng.api.basedata.service.cardbin;

import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import com.aerospike.client.*;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AerospikeServiceImpl implements AerospikeService {
    private static final Logger logger = LoggerFactory.getLogger(AerospikeServiceImpl.class);

    @Autowired
    AerospikeClient aerospikeClient;

    @Value("${cardbin.aerospike.namespace:us-as-np}")
    private String asNameSpace;

    @Autowired
    private IMetrics metrics;

    // 相当于表的列
    private static final String asBinName = "DATA";
    private static final String asSetControl = "sync_client_control";
    private static final String asSetRoute = "sync_client_route";

    private static Map<String, Map<Key, Bin>> filedData = new HashMap<>();

    /**
     * 写数据到缓存
     *
     * @param key   key
     * @param value value
     * @return 成功或者失败
     */
    @Override
    public boolean set(String asSet, String key, String value) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return false;
        }
        Key asKey = new Key(asNameSpace, asSet, key);
        Bin asValue = new Bin(asBinName, value);
        try {
            aerospikeClient.put(null, asKey, asValue);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("try again");
            try {
                aerospikeClient.put(null, asKey, asValue);
            } catch (Exception e1) {
                logger.error("err again: {}", e.getMessage());
                Map<Key, Bin> setMap = filedData.get(asSet);
                if (null == setMap) {
                    setMap = new HashMap<>();
                }
                setMap.put(asKey, asValue);
                return false;
            }
        }
        return true;
    }

    public String getLoadFile(String key) {
        return get(asSetControl, key);
    }

    public void setLoadFile(String key, String fileName) {
        set(asSetControl, key, fileName);
    }

    // control md5Salt
    public String getMd5Salt(String key) {
        return get(asSetControl, key);
    }

    public void setMd5Salt(String key, String md5Salt) {
        set(asSetControl, key, md5Salt);
    }


    public String getRoute(String key) {
        return get(asSetRoute, key);
    }

    public void setRoute(String key, String route) {
        set(asSetRoute, key, route);
    }


    public boolean tryLock(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        Key asKey = new Key(asNameSpace, asSetControl, key);
        Bin asValue = new Bin(asBinName, "true");
        WritePolicy wp = new WritePolicy();
        wp.generation = 0;
        wp.recordExistsAction = RecordExistsAction.CREATE_ONLY;
        wp.expiration = 1200;   //20分钟
        try {
            aerospikeClient.put(wp, asKey, asValue);
        } catch (Exception e) {
            logger.info("获取锁失败");
            return false;
        }
        return true;
    }

    public void delLock(String key) {
        Key asKey = new Key(asNameSpace, asSetControl, key);
        aerospikeClient.delete(null, asKey);
    }

    void failedDataClean(String asSet) {
        Map<Key, Bin> setMap = filedData.get(asSet);
        if (null != setMap) {
            setMap.clear();
        }
    }

    void failedDataReDo(String asSet) {
        Map<Key, Bin> setMap = filedData.get(asSet);
        if (null != setMap) {
            setMap.forEach(this::set);
        }
    }

    private boolean set(Key key, Bin bin) {
        try {
            aerospikeClient.put(null, key, bin);
        } catch (Exception e) {
            logger.error("failed redo err: {}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 获取缓存数据
     *
     * @param key key   黑名单:加盐hash值
     * @return value
     */
    @Override
    public String get(String asSet, String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object value = null;
        try {
            Key asKey = new Key(asNameSpace, asSet, key);
            Record record = aerospikeClient.get(null, asKey);
            if (null != record) {
                value = aerospikeClient.get(null, asKey).getString(asBinName);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (ReasonCodeUtil.isTimeout(e)) {
                String[] tags = {
                        "sub_reason_code", ReasonCode.USBIN_ERROR_TIMEOUT.getCode()};
                metrics.counter("kunpeng.api.subReasonCode",tags);
            } else {
                String[] tags = {
                        "sub_reason_code", ReasonCode.USBIN_ERROR_OTHER.getCode()};
                metrics.counter("kunpeng.api.subReasonCode",tags);
            }
        }
        return null != value ? value.toString() : null;
    }

    @Override
    public void delete(String asSet, String key) {
        Key asKey = new Key(asNameSpace, asSet, key);
        aerospikeClient.delete(null, asKey);
    }

    @Override
    public void clearSet(String asSet) {
        aerospikeClient.truncate(null, asNameSpace, asSet, null);
    }

    @Override
    public List<String> getAll(String asSet) {

        ScanPolicy policy = new ScanPolicy();
        policy.concurrentNodes = true;
        policy.priority = Priority.LOW;
        policy.includeBinData = true;

        Scan scan = new Scan();
        aerospikeClient.scanAll(policy, asNameSpace, asSet, scan);
        return scan.getList();
    }

    @Override
    public String getAsData(String namespace, String setName, String keyName) {


        if (!StringUtils.isBlank(keyName)) {
            Map<String, Object> result = new HashMap<>();
            WritePolicy writePolicy = new WritePolicy();
            writePolicy.setTimeout(60000);
            Key key = new Key(namespace, setName, keyName);
            Record r = aerospikeClient.get(writePolicy, key);
            Map<String, Object> map = r.bins;
            for (String binName : map.keySet()) {
                result.put(binName, map.get(binName));
            }
            return JSON.toJSONString(result);
        }

        ScanPolicy policy = new ScanPolicy();
        policy.priority = Priority.LOW;
        policy.includeBinData = true;
        policy.concurrentNodes = true;

        Scan scan = new Scan();
        aerospikeClient.scanAll(policy, namespace, setName, scan);
        return JSON.toJSONString(scan.getList());
    }

    class Scan implements ScanCallback {

        private List<String> list = new ArrayList<>();

        @Override
        public void scanCallback(Key key, Record record) throws AerospikeException {
            list.add((String) record.bins.get(asBinName));
        }

        public List<String> getList() {
            return list;
        }
    }
}
