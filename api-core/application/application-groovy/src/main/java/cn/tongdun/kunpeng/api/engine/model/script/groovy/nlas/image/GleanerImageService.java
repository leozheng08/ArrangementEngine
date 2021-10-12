package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.image;

import java.util.List;

/**
 * @author liupei
 */
public interface GleanerImageService {

    @Deprecated
    boolean isContainsPorn(List<String> imageIds);

    /**
     * 获取鉴黄模型label
     */
    int getLabel(List<String> imageIds);

    /**
     * 获取鉴黄模型prob
     */
    double getProb(List<String> imageIds);

    /**
     * 获取文字识别模型分数
     *
     * @param imageIds
     * @return
     */
    double getWordsRecognitionScore(List<String> imageIds);

    String getAll(List<String> imageIds);

    /**
     * 获取文字识别模型文本
     *
     * @param imageIds
     * @return
     */
    String getWordsRecognitionFulltext(List<String> imageIds);

    /**
     * 获取涉政模型label
     *
     * @param imageIds
     * @return
     */
    int getPoliticsLabel(List<String> imageIds);

    /**
     * 获取涉政模型prob
     *
     * @param imageIds
     * @return
     */
    double getPoliticsProb(List<String> imageIds);
}
