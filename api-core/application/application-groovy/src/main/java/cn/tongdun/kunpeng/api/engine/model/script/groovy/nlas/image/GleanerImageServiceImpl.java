package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.image;

import cn.fraudmetrix.gleaner.service.GleanerForApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liupei
 */
@Service("gleanerImageService")
public class GleanerImageServiceImpl implements GleanerImageService {

    private Logger logger = LoggerFactory.getLogger(GleanerImageServiceImpl.class);

    @Autowired
    private GleanerForApiService gleanerForApiService;

    @Override
    public boolean isContainsPorn(List<String> imageIds) {
        //废弃接口
        return false;
    }

    @Override
    public int getLabel(List<String> imageIds) {

        try {
            return gleanerForApiService.getLabel(imageIds);
        } catch (Exception e) {
            logger.warn("gleaner::getLabel", e);
            return 0;
        }
    }

    @Override
    public double getProb(List<String> imageIds) {

        try {
            return gleanerForApiService.getProb(imageIds);
        } catch (Exception e) {
            logger.warn("gleaner::getProb", e);
            return 0d;
        }
    }

    @Override
    public String getAll(List<String> imageIds) {
        try {
            return gleanerForApiService.getAllLabel(imageIds);
        } catch (Exception e) {
            logger.warn("gleaner::getAll", e);
            return "";
        }
    }


    @Override
    public double getWordsRecognitionScore(List<String> imageIds) {
        try {
            return gleanerForApiService.getWordsRecognitionScore(imageIds);
        } catch (Exception e) {
            logger.warn("gleaner::getWordsRecognitionScore", e);
            return 0d;
        }
    }

    @Override
    public String getWordsRecognitionFulltext(List<String> imageIds) {
        try {
            return gleanerForApiService.getWordsRecognitionFulltext(imageIds);
        } catch (Exception e) {
            logger.warn("gleaner::getWordsRecognitionFulltext", e);
            return "";
        }
    }

    @Override
    public int getPoliticsLabel(List<String> imageIds) {
        try {
            return gleanerForApiService.getPoliticsLabel(imageIds);
        } catch (Exception e) {
            logger.warn("gleaner::getPoliticsLabel", e);
            return 0;
        }
    }

    @Override
    public double getPoliticsProb(List<String> imageIds) {
        try {
            return gleanerForApiService.getPoliticsProb(imageIds);
        } catch (Exception e) {
            logger.warn("gleaner::getPoliticsProb", e);
            return 0d;
        }
    }
}
