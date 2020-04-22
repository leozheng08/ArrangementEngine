package cn.tongdun.kunpeng.api.common.util;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestUtil {

    public static void assignPrivateField(Class cls,Object object, String fieldName, Object value) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void assignPrivateField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getPrivateField(Object obj, String fieldName){
        try{
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public static Object invokePrivateMethod(Object obj, String methodName, Class[] parameters, Object... paramValues){
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, parameters);
            method.setAccessible(true);
            Object value = method.invoke(obj, paramValues);
            return value;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
