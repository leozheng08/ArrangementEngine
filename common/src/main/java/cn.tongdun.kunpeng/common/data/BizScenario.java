package cn.tongdun.kunpeng.common.data;


import cn.tongdun.tdframework.common.extension.IBizScenario;

/**
 * 业务场景
 */
public class BizScenario implements IBizScenario{
    public final static String DEFAULT_TENANT = "defaultTenant";
    public final static String DEFAULT_BUSINESS = "defaultBusiness";
    public final static String DEFAULT_PARTNER = "defaultPartner";
    private final static String DOT_SEPARATOR = ".";

    /**
     * tenant租户编码
     */
    private String tenant = DEFAULT_TENANT;

    /**
     * 业务编码，包含ANTI_FRAUD：反欺诈,CREDIT：信贷"
     */
    private String business = DEFAULT_BUSINESS;

    /**
     * 合作方编码
     */
    private String partner = DEFAULT_PARTNER;


    @Override
    public String getUniqueIdentity(){
        return new StringBuilder().append(tenant).append(DOT_SEPARATOR)
                .append(business).append(DOT_SEPARATOR)
                .append(partner).toString();
    }

    public static BizScenario valueOf(String tenant, String business, String partner){
        BizScenario bizScenario = new BizScenario();
        bizScenario.tenant = tenant;
        bizScenario.business = business;
        bizScenario.partner = partner;
        return bizScenario;
    }

    public static BizScenario valueOf(String tenant){
        return BizScenario.valueOf(tenant, DEFAULT_BUSINESS, DEFAULT_PARTNER);
    }

    public static BizScenario valueOf(String tenant, String business){
        return BizScenario.valueOf(tenant, business, DEFAULT_PARTNER);
    }

    public static BizScenario newDefault(){
        return BizScenario.valueOf(DEFAULT_TENANT, DEFAULT_BUSINESS, DEFAULT_PARTNER);
    }
}
