package cn.tongdun.kunpeng.api.engine.model.dictionary;

import java.util.List;

/**
 * @Author: liuq
 * @Date: 2020/5/25 8:37 下午
 */
public interface IDictionaryRepository {

    List<Dictionary> getDictionary(String key);
}
