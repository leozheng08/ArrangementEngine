package cn.tongdun.kunpeng.api.acl.impl.engine.model.cluster;

import cn.fraudmetrix.chassis.api.common.ApiResult;
import cn.fraudmetrix.chassis.api.partner.dto.PartnerDTO;
import cn.fraudmetrix.chassis.api.partner.dto.PartnerResultDTO;
import cn.fraudmetrix.chassis.api.partner.intf.PartnerQueryService;
import cn.tongdun.kunpeng.api.acl.engine.model.cluster.IPartnerClusterRepository;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author yuanhang
 * 当前chassis只支持single模式
 */
@Slf4j
@Repository
public class ChassisPartnerClusterRepository implements IPartnerClusterRepository {

    @Autowired
    private PartnerQueryService partnerQueryService;

    /**
     * 没有分集群部署，则查询出所有合作方
     * @param cluster
     * @return
     */
    @Override
    public Set<String> queryPartnerByCluster(String cluster){
        Set<String> partners = Sets.newHashSet();
        long beginTime = System.currentTimeMillis();
        int start = 0, end = 1000, length = 1000;
        ApiResult<PartnerResultDTO> responseVo = partnerQueryService.queryAllPartner(start, end);
        log.info("partnerQueryService.queryAllPartner start:{},end:{}",start,end);
        if (responseVo == null || responseVo.getCode() != 200) {
            log.error("get partner responseVo error! responseVo :{}", responseVo);
            return partners;
        }
        List<PartnerDTO> partnerDTOs = responseVo.getData().getPartnerDTOs();
        partnerDTOs.forEach(r -> partners.add(r.getPartnerCode()));
        int total = responseVo.getData().getTotal();
        log.info("partnerQueryService.queryAllPartner total:{}",total);
        if (total > length) {
            do {
                start += length;
                end = start + length;
                ApiResult<PartnerResultDTO> result = partnerQueryService.queryAllPartner(start, end);
                log.info("partnerQueryService.queryAllPartner start:{},end:{}",start,end);
                if (responseVo == null || responseVo.getCode() != 200) {
                    log.error("get partner responseVo error! result :{}", result);
                    return partners;
                } else {
                    List<PartnerDTO> partnerDTOList =result.getData().getPartnerDTOs();
                    partnerDTOList.forEach(r -> partners.add(r.getPartnerCode()));
                }
            } while(end < total);
        }
        log.info(TraceUtils.getFormatTrace() + "EventTypeLoadManager success, cost:{}, size:{}", System.currentTimeMillis() - beginTime, partners.size());
        return partners;
    }

}
