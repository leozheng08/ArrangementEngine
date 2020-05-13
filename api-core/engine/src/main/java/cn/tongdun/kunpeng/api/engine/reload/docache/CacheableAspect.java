package cn.tongdun.kunpeng.api.engine.reload.docache;

import cn.tongdun.ddd.common.domain.CommonEntity;
import cn.tongdun.kunpeng.api.common.util.TimestampUtil;
import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.share.config.IConfigRepository;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;


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
            logger.debug(TraceUtils.getFormatTrace()+"dataobject cache, class:{} method:{}, size:{}, uuid:{}",
                    realMethod.getDeclaringClass().getSimpleName(),realMethod.getName(),
                    cacheObj instanceof List ? ((List)cacheObj).size(): null,
                    cacheObj instanceof CommonEntity ? ((CommonEntity)cacheObj).getUuid(): null);
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
            logger.error(TraceUtils.getFormatTrace()+"CacheableAspect getAnnotation error",exc);
        }
        return null;
    }


    /**
     * 判断是否加强不走redis缓存直接从数据库查询
     * @return
     */
    private boolean forceFromDb(){
        try {
            //dataobject cache 全局开关
            if (!configRepository.getBooleanProperty("dataobject.cache.enable")) {
                return true;
            }

            //当前线程要求强制从数据库查询，则直接返回
            Boolean forceFromDb = (Boolean) ThreadContext.getContext().getAttr(ReloadConstant.THREAD_CONTEXT_ATTR_FORCE_FROM_DB);
            if (forceFromDb != null && forceFromDb) {
                return true;
            }
        }catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"CacheableAspect forceFromDb error",e);
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
            logger.error(TraceUtils.getFormatTrace()+"CacheableAspect getDataObjectCache error",e);
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
                return dataObjectCache.getByIdx(cacheable.idxName(), args,cacheable.onlyAvailable());
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
            logger.error(TraceUtils.getFormatTrace()+"CacheableAspect getCacheDataObject error",exc);
        }
        return null;
    }


    /**
     * 将数据缓存到redis
     * @param result
     * @param dataObjectCache
     */
    private void cacheDataObject(Object result, IDataObjectCache dataObjectCache){
        //dataobject cache 全局开关
        if (!configRepository.getBooleanProperty("dataobject.cache.enable")) {
            return;
        }
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
            logger.error(TraceUtils.getFormatTrace()+"CacheableAspect cacheDataObject error",e);
        }
    }
}
