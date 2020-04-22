package cn.tongdun.kunpeng.api.consumer.mongo;


public interface IMongoDBStorage {

    /**
     * 查询
     *
     * @param encodedKey
     * @return
     * @throws Exception
     */
    public byte[] get(String encodedKey) throws Exception;

    /**
     * 存储
     *
     * @param encodedKey
     * @param data
     */
    public void set(String encodedKey, byte[] data) throws Exception;

    /**
     * 删除
     *
     * @param encodeKey
     * @throws Exception
     */
    public void delete(String encodeKey) throws Exception;
}
