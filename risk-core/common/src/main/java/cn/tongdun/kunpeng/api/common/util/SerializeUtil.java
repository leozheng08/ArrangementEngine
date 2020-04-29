package cn.tongdun.kunpeng.api.common.util;

import cn.tongdun.kunpeng.share.json.JSON;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by coco on 16/12/9.
 */
public class SerializeUtil {
    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private static <T> Schema<T> getSchema(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(clazz);
            if (schema != null) {
                cachedSchema.put(clazz, schema);
            }
        }
        return schema;
    }

    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    public static <T> byte[] serializerByProstuff(T obj) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> byte[] serializerListByProstuff(List<T> objList){
        if (null==objList||objList.isEmpty()){
            return null;
        }
        Class<T> clazz= (Class<T>) objList.get(0).getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(clazz);
            OutputStream byteStream=new ByteArrayOutputStream();
            ProtostuffIOUtil.writeListTo(byteStream,objList,schema,buffer);
            return ((ByteArrayOutputStream) byteStream).toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> List<T> deserializerListByProstuff(byte[] data, Class<T> clazz){
        try {
            Schema<T> schema = getSchema(clazz);
            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(data);
            return ProtostuffIOUtil.parseListFrom(byteArrayInputStream,schema);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
    /**
     * 反序列化
     *
     * @param data
     * @param clazz
     * @return
     */
    public static <T> T deserializerByProstuff(byte[] data, Class<T> clazz) {
        try {
            T obj = clazz.newInstance();
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(data, obj, schema);
            return obj;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


    static class Person implements Serializable{
        private static final long serialVersionUID = 6178621029226958795L;
        String name;
        int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static void main(String[] args) {
        long st = System.currentTimeMillis();
        int size = 1000000;
        for (int i=0;i<size;i++){
            Person person = new Person();
            person.setName("sdfsfdafasdfa");
            person.setAge(23232);
            serializerByProstuff(person);

        }
        System.out.println("serializeByProtostuff: " + String.valueOf(System.currentTimeMillis() - st));


        Person person = new Person();
        person.setName("sdfsfdafasdfa");
        person.setAge(23232);
        Person newPerson = deserializerByProstuff(serializerByProstuff(person),Person.class);
        System.out.println(JSON.toJSONString(newPerson));


        byte[] bbb = serializerByProstuff("true");
        System.out.println(deserializerByProstuff(bbb,String.class));
    }
}
