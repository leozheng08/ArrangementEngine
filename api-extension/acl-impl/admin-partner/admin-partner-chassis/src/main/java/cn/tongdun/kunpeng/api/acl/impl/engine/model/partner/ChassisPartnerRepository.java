package cn.tongdun.kunpeng.api.acl.impl.engine.model.partner;

import cn.fraudmetrix.chassis.api.common.ApiResult;
import cn.fraudmetrix.chassis.api.partner.dto.PartnerResultDTO;
import cn.fraudmetrix.chassis.api.partner.intf.PartnerQueryService;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.PartnerDTO;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 只有一个default默认合作方
 *
 * @Author: liang.chen
 * @Date: 2020/4/13 上午11:27
 */
@Repository
public class ChassisPartnerRepository implements IPartnerRepository {

    private Logger logger = LoggerFactory.getLogger(ChassisPartnerRepository.class);

    @Autowired
    PartnerQueryService partnerQueryService;

    private static final PartnerDTO DEFAULT_PARTNER = createDefaultPartner();

    @Autowired
    ILocalEnvironment localEnvironment;

    //查询所有合作方编码
    @Override
    public Set<String> queryAllPartnerCode() {

        return queryPartnerByCluster(localEnvironment.getCluster());
    }

    //查询合作信息列表
    @Override
    public List<PartnerDTO> queryEnabledByPartners(Set<String> partners) {
        if (CollectionUtils.isEmpty(partners)) {
            return Lists.newArrayList();
        }
        ApiResult<List<cn.fraudmetrix.chassis.api.partner.dto.PartnerDTO>> apiResult = partnerQueryService.queryByPartnerCodeList(new ArrayList<>(partners));
        if (apiResult == null || apiResult.getCode() != 200 || CollectionUtils.isEmpty(apiResult.getData())) {
            logger.error("get partner responseVo error! responseVo :{}", apiResult);
            return Lists.newArrayList();
        }
        List<PartnerDTO> partnerDTOList = Lists.newArrayList();
        apiResult.getData().forEach(p -> {
            PartnerDTO partnerDTO = new PartnerDTO();
            BeanUtils.copyProperties(p, partnerDTO);
            partnerDTO.setIndustryType(p.getIndustryTypeCode());
            partnerDTO.setSecondIndustryType(p.getSecondIndustryTypeCode());
            partnerDTOList.add(partnerDTO);
        });
        return partnerDTOList;

    }

    //查询单个合作方信息
    @Override
    public PartnerDTO queryByPartnerCode(String partnerCode) {

        ApiResult<List<cn.fraudmetrix.chassis.api.partner.dto.PartnerDTO>> apiResult = partnerQueryService.queryByPartnerCode(partnerCode);
        if (apiResult == null || apiResult.getCode() != 200 || CollectionUtils.isEmpty(apiResult.getData())) {
            logger.error("get partner responseVo error! responseVo :{}", apiResult);
            return null;
        }
        cn.fraudmetrix.chassis.api.partner.dto.PartnerDTO partnerDTO = apiResult.getData().get(0);
        PartnerDTO partner = new PartnerDTO();
        BeanUtils.copyProperties(partnerDTO, partner);
        return partner;

    }


    private static PartnerDTO createDefaultPartner() {
        PartnerDTO partner = new PartnerDTO();
        partner.setPartnerCode(Constant.DEFAULT_PARTNER);
        partner.setUuid(Constant.DEFAULT_PARTNER);
        partner.setDisplayName(Constant.DEFAULT_PARTNER);
        partner.setStatus(1);
        return partner;
    }


    public Set<String> queryPartnerByCluster(String cluster) {
        Set<String> partners = Sets.newHashSet();
        long beginTime = System.currentTimeMillis();
        int start = 0, end = 1000, length = 1000;
        ApiResult<PartnerResultDTO> responseVo = partnerQueryService.queryAllPartner(start, end);
        logger.info("partnerQueryService.queryAllPartner start:{},end:{}", start, end);
        if (responseVo == null || responseVo.getCode() != 200) {
            logger.error("get partner responseVo error! responseVo :{}", responseVo);
            return partners;
        }
        List<cn.fraudmetrix.chassis.api.partner.dto.PartnerDTO> partnerDTOs = responseVo.getData().getPartnerDTOs();
        partnerDTOs.forEach(r -> partners.add(r.getPartnerCode()));
        int total = responseVo.getData().getTotal();
        logger.info("partnerQueryService.queryAllPartner total:{}", total);
        if (total > length) {
            do {
                start += length;
                end = start + length;
                ApiResult<PartnerResultDTO> result = partnerQueryService.queryAllPartner(start, end);
                logger.info("partnerQueryService.queryAllPartner start:{},end:{}", start, end);
                if (responseVo == null || responseVo.getCode() != 200) {
                    logger.error("get partner responseVo error! result :{}", result);
                    return partners;
                } else {
                    List<cn.fraudmetrix.chassis.api.partner.dto.PartnerDTO> partnerDTOList = result.getData().getPartnerDTOs();
                    partnerDTOList.forEach(r -> partners.add(r.getPartnerCode()));
                }
            } while (end < total);
        }
        logger.info(TraceUtils.getFormatTrace() + "ChassisPartnerRepository success, cost:{}, size:{}", System.currentTimeMillis() - beginTime, partners.size());
        return partners;
    }
}
