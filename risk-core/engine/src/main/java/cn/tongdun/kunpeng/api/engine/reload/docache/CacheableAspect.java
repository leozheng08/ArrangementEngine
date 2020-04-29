package cn.tongdun.kunpeng.api.engine.reload.docache;

import cn.tongdun.ddd.common.domain.CommonEntity;
import cn.tongdun.kunpeng.api.common.util.TimestampUtil;
import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.share.config.IConfigRepository;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class CacheableAspect {

    @Autowired
    private DataObjectCacheFactory doCacheFactory;

    private final Logger  logger = LoggerFactory.getLogger(CacheableAspect.class);

    @Autowired
    private IConfigRepository configRepository;

    public Object around(ProceedingJoinPoint point) throws Throwable{
        //强制从数据库查询
        if(forceFromDb()){
            return point.proceed();
        }

        //此处realMethod是目标对象（原始的）的方法
        Method realMethod = getMethod(point);
        if(realMethod == null){
            return point.proceed();
        }

        //取得注解类
        Cacheable cacheable = realMethod.getAnnotation(Cacheable.class);

        //取得缓存处理的实现类
        IDataObjectCache dataObjectCache = getDataObjectCache(point, realMethod, cacheable);

        //如果有缓存对象则直接返回
        Object cacheObj = getCacheDataObject(point,dataObjectCache,cacheable);
        if(cacheObj != null){
            return cacheObj;
        }


        //如果缓存没有，则再查数据库
        Object result = point.proceed();

        //放到缓存中
        cacheDataObject(result,dataObjectCache);
        return result;

    }

    private Method getMethod(ProceedingJoinPoint point){
        try {
            Class<?> clazz = point.getTarget().getClass().getInterfaces()[0];
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method realMethod = clazz.getDeclaredMethod(signature.getName(),
                    signature.getParameterTypes());

            return realMethod;
        } catch (Exception exc){
            logger.error("CacheableAspect getAnnotation error",exc);
        }
        return null;
    }


    /**
     * 判断是否加强不走redis缓存直接从数据库查询
     * @return
     */
    private boolean forceFromDb(){
        try {
            //全局要求强制从数据库查询，则直接返回
            if (configRepository.getBooleanProperty("force.from.db")) {
                return true;
            }

            //当前线程要求强制从数据库查询，则直接返回
            Boolean forceFromDb = (Boolean) ThreadContext.getContext().getAttr(ReloadConstant.THREAD_CONTEXT_ATTR_FORCE_FROM_DB);
            if (forceFromDb != null && forceFromDb) {
                return true;
            }
        }catch (Exception e){
            logger.error("CacheableAspect forceFromDb error",e);
        }
        return false;
    }

    /**
     * 取得缓存处理的实现类
     * @param point
     * @return
     */
    private IDataObjectCache getDataObjectCache(ProceedingJoinPoint point, Method realMethod, Cacheable cacheable){
        try {
            Class returnClass = realMethod.getReturnType();
            Type returnType = realMethod.getGenericReturnType();
            if (Collection.class.isAssignableFrom(returnClass)) {
                if (returnType instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) returnType;
                    Type[] argTypes = paramType.getActualTypeArguments();
                    if (argTypes.length > 0) {
                        IDataObjectCache dataObjectCache = doCacheFactory.getDOCache((Class)argTypes[0]);
                        return dataObjectCache;
                    }
                }
            } else {
                IDataObjectCache dataObjectCache = doCacheFactory.getDOCache(returnClass);
                return dataObjectCache;
            }

        } catch (Exception e){
            logger.error("CacheableAspect getDataObjectCache error",e);
        }
        return null;
    }

    /**
     * 从redis取得缓存数据对象
     * @param point
     * @return
     */
    private Object getCacheDataObject(ProceedingJoinPoint point, IDataObjectCache dataObjectCache, Cacheable cacheable ){
        try{
            if(dataObjectCache == null){
                return null;
            }

            Object[] args = point.getArgs();
            //按索引查询
            if(StringUtils.isNotBlank(cacheable.idxName())) {
                return dataObjectCache.getByIdx(cacheable.idxName(), args);
            } else {
                //按uuid查询
                if(args== null || args.length<1 || args[0] == null) {
                    return null;
                }

                //从缓存中获取
                CommonEntity result = dataObjectCache.get(args[0].toString());

                if(result == null){
                    return null;
                }

                Long queryModifiedVersion = (Long) ThreadContext.getContext().getAttr(ReloadConstant.THREAD_CONTEXT_ATTR_MODIFIED_VERSION);
                if(queryModifiedVersion == null){
                    return result;
                }
                //如果按时间戳查询，则判断redis缓存的时间戳大于等于queryModifiedVersion 时才按redis缓存的数据返回
                if(result.getGmtModify() != null && TimestampUtil.compare(result.getGmtModify(),queryModifiedVersion)>=0) {
                    return result;
                }
                return null;
            }
        } catch (Exception exc){
            logger.error("CacheableAspect getCacheDataObject error",exc);
        }
        return null;
    }


    /**
     * 将数据缓存到redis
     * @param result
     * @param dataObjectCache
     */
    private void cacheDataObject(Object result, IDataObjectCache dataObjectCache){
        if( dataObjectCache == null){
            return;
        }
        try {
            if(result instanceof Collection){
                for(Object element:(Collection)result) {
                    if(!(element instanceof CommonEntity)){
                        break;
                    }
                    dataObjectCache.set((CommonEntity)element);
                }
            } else {
                if((result instanceof CommonEntity)) {
                    dataObjectCache.set((CommonEntity) result);
                }
            }
        } catch (Exception e){
            logger.error("CacheableAspect cacheDataObject error",e);
        }
    }



//    public Map<String, Object> getFieldsName(ProceedingJoinPoint point) throws Throwable {
//        Class<?> clazz = point.getTarget().getClass().getInterfaces()[0];
//
//        MethodSignature signature = (MethodSignature) point.getSignature();
//        Method realMethod = clazz.getDeclaredMethod(signature.getName(),
//                signature.getParameterTypes());
//
//        String[] parameterNames = ParameterNameUtils.getMethodParameterNamesByAsm4(clazz, realMethod);
//
//        return null;
//    }
//
//
//    public Map<String, Object> getFieldsName2(ProceedingJoinPoint joinPoint) throws Throwable {
//
//
//        Class<?> clazz = joinPoint.getTarget().getClass().getInterfaces()[0];
//        String clazzName = clazz.getName();
//        String methodName = joinPoint.getSignature().getName(); //获取方法名称
//        Object[] args = joinPoint.getArgs();//参数
//        //获取参数名称和值
//        Map<String, Object> nameAndArgs = getFieldsName(this.getClass(), clazzName, methodName, args);
//        return nameAndArgs;
//    }
//
//
//
//    private Map<String,Object> getFieldsName(Class cls, String clazzName, String methodName, Object[] args) throws NotFoundException {
//        Map<String,Object > map=new HashMap<String,Object>();
//
//        ClassPool pool = ClassPool.getDefault();
//        //ClassClassPath classPath = new ClassClassPath(this.getClass());
//        ClassClassPath classPath = new ClassClassPath(cls);
//        pool.insertClassPath(classPath);
//
//        CtClass cc = pool.get(clazzName);
//        CtMethod cm = cc.getDeclaredMethod(methodName);
//        MethodInfo methodInfo = cm.getMethodInfo();
//        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
//        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
//        if (attr == null) {
//            // exception
//        }
//        // String[] paramNames = new String[cm.getParameterTypes().length];
//        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
//        for (int i = 0; i < cm.getParameterTypes().length; i++){
//            map.put( attr.variableName(i + pos),args[i]);//paramNames即参数名
//        }
//
//        //Map<>
//        return map;
//    }



}
