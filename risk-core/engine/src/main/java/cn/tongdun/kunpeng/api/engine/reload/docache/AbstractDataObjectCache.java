package cn.tongdun.kunpeng.api.engine.reload.docache;

import cn.tongdun.ddd.common.domain.CommonEntity;
import cn.tongdun.kunpeng.api.common.util.SerializeUtil;
import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.share.kv.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:34
 */
public abstract class AbstractDataObjectCache<T extends CommonEntity> implements IDataObjectCache<T> {
    @Autowired
    protected IHashBinaryKVRepository hashBinaryKVRepository;

    @Autowired
    protected IScoreKVRepository scoreKVRepository;

    protected Class<T> clazz;
    protected String cacheKey;

    @Autowired
    protected DataObjectCacheFactory doCacheFactory;

    public void register(Class clazz){
        doCacheFactory.register(clazz,this);
    }

    @PostConstruct
    public void init(){
        clazz = getTClass();
        register(clazz);
        cacheKey = "dataobj_cache_" + clazz.getSimpleName();
    }

    //取得DO类
    private Class<T> getTClass()
    {
        Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }

    /**
     * 根据uuid查询DO对象
     * @param uuid
     * @return
     */
    @Override
    public T get(String uuid){
        byte[] bytes = hashBinaryKVRepository.hget(getBytes(cacheKey),getBytes(uuid));
        if(bytes == null || bytes.length==0){
            return null;
        }
        return deserializerObj(bytes);
    }

    /**
     * 将DO对象放到集中缓存中
     * @param dataObject
     */
    @Override
    public void set(T dataObject){
        if(dataObject == null || StringUtils.isBlank(dataObject.getUuid())){
            return;
        }
        //hash表按uuid->dataobject存放
        hashBinaryKVRepository.hset(getBytes(cacheKey),getBytes(dataObject.getUuid()),serializerObj(dataObject));
        //索引按zadd(idex_name,分数固定为0,索引值:uuid)方式添加，通过zrangebylex来按索引查询
        setByIdx(dataObject);
    }

    //添加索引
    public abstract void setByIdx(T dataObject);

    /**
     * 从缓存中删除
     */
    @Override
    public void remove(String uuid){
        T dataObj = get(uuid);
        if(dataObj == null){
            return;
        }
        hashBinaryKVRepository.hdel(getBytes(cacheKey),getBytes(uuid));
        removeIdx(dataObj);
    }

    //删除索引
    public abstract void removeIdx(T dataObject);


    /**
     * 根据索引查询
     * @param idxName
     * @param args
     * @return
     */
    @Override
    public List<T> getByIdx(String idxName, Object[] args,boolean onlyAvailable){
        if(StringUtils.isBlank(idxName)){
            return null;
        }

        //如果查询所有，通过hsan遍历所有元素
        if(ReloadConstant.IDX_NAME_ALL.equals(idxName)){
            List<T> result = getAll();
            if(onlyAvailable) {
                return getAvailable(result);
            } else {
                return result;
            }
        }

        if(args == null || args.length==0){
            return null;
        }

        //如果入参为集合
        if(args.length == 1 && args[0] instanceof Collection){
            List<T> results = new ArrayList();
            ((Collection)args[0]).forEach(element ->{
                if(ReloadConstant.IDX_NAME_UUID.equals(idxName)){
                    T tmp = get(element.toString());
                    if (tmp != null) {
                        results.add(tmp);
                    }
                } else {
                    List<T> tmp = getByIdx(idxName, element.toString(),onlyAvailable);
                    if (tmp != null) {
                        results.addAll(tmp);
                    }
                }
            });
            if(results.isEmpty()){
                return null;
            }
            return results;
        } else {
            String idxValue = buildQueryValue(args);
            List<T> results = getByIdx(idxName,idxValue,onlyAvailable);
            return results;
        }
    }


    //todo 为了性能zrangeByLex改成按分页查询
    private List<T> getByIdx(String idxName,String idxValue,boolean onlyAvailable){
        //通过zrangebylex来按索引查询,
        Set<String> set = scoreKVRepository.zrangeByLex(cacheKey+"_"+idxName,"["+idxValue+":","["+idxValue+":");
        if(set == null || set.isEmpty()){
            return null;
        }
        List<T> results = new ArrayList(set.size());
        set.forEach(value ->{
            if(StringUtils.isBlank(value)){
                return;
            }
            int index = value.lastIndexOf(":");
            if(index<0){
                return;
            }
            String uuid = value.substring(index+1);
            T dataObj = get(uuid);
            if(dataObj != null){
                results.add(dataObj);
            }
        });

        if(results.isEmpty()){
            return null;
        }
        if(onlyAvailable){
            return getAvailable(results);
        } else {
            return results;
        }
    }

    /**
     * 返回所有对象，通过hsan遍历所有元素
     * @return
     */
    protected List<T> getAll() {
        Cursor cursor = Cursor.first();
        int count = 10_000;
        boolean first = true;
        List<T> results = null;
        while (first || cursor.getCursor() != 0) {
            first = false;
            List<IValue<byte[], byte[]>> tmp = hashBinaryKVRepository.hscan(getBytes(cacheKey), cursor, count);
            if (CollectionUtils.isEmpty(tmp)) {
                continue;
            }

            for (IValue<byte[], byte[]> value : tmp) {
                if (value != null && value.getValue() != null) {
                    T dataObj = deserializerObj(value.getValue());
                    if(results == null){
                        results = new ArrayList<T>();
                    }
                    results.add(dataObj);
                }
            }
            if(results!= null && results.size() >10_000) {
                throw new RuntimeException("数据量过大，从redis获取的数据超过10000条记录");
            }
        }

        return results;
    }

    protected List<T> getAvailable(List<T> list) {
        if(list == null || list.isEmpty()){
            return list;
        }
        return list.stream().filter(dataObj -> {
            return isValid(dataObj);
        }).collect(Collectors.toList());
    }

    public abstract boolean isValid(T dataObj);

    private String buildQueryValue(Object[] args){
        if(args == null || args.length==0){
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for(Object obj:args){
            if(builder.length()>0){
                builder.append(".");
            }
            builder.append(obj==null?obj:obj.toString());
        }
        return builder.toString();
    }


    /**
     * 字符串转换为byte数组
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     */
    protected byte[] getBytes(String key) {
        try {
            return key.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e){
            throw new RuntimeException("UnsupportedEncodingException UTF-8");
        }
    }


    /**
     * 序列化缓存对象, 变为byte数组
     * @param obj
     * @return
     */
    protected byte[] serializerObj(T obj) {
        return SerializeUtil.serializerByProstuff(obj);
    }

    /**
     * 反序列化缓存对象
     * @param data
     * @return
     */
    protected T deserializerObj(byte[] data) {
        return SerializeUtil.deserializerByProstuff(data,clazz);
    }
}
