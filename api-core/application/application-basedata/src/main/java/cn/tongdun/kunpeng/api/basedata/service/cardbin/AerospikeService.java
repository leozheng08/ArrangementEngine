package cn.tongdun.kunpeng.api.basedata.service.cardbin;

import java.util.List;

public interface AerospikeService {
    boolean set(String asSet, String key, String value);

    String get(String asSet, String key);

    void delete(String asSet, String key);

    void clearSet(String asSet);

    List<String> getAll(String asSet);

    String getAsData(String namespace, String setName, String keyName);
}
