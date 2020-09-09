package cn.tongdun.kunpeng.api.engine.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * @author yangchangkai
 * @date 2020/9/8
 */
public class LevenshteinDistanceUtils {

    /**
     * 字符串编辑距离算法相似度
     * @param left
     * @param right
     * @return 0.0 ~ 1.0
     */
    public static double editDistanceSimilary(String left, String right){
        //先判断相等
        if(StringUtils.equals(left, right)){
            return 1.0;
        }
        //有一个null
        if(left == null || right == null){
            return 0.0;
        }
        int maxLen = Math.max(left.length(), right.length());
        LevenshteinDistance ld = new LevenshteinDistance();
        int count = ld.apply(left, right);
        return 1 - (double)count / maxLen;
    }
}
