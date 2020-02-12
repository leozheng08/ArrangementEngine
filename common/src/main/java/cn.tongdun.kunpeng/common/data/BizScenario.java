package cn.tongdun.kunpeng.common.data;


import cn.tongdun.tdframework.common.extension.IBizScenario;
import lombok.Data;

/**
 * 业务场景
 */
@Data
public class BizScenario implements IBizScenario{
    private final static String DOT_SEPARATOR = ".";

    /**
     * tenant租户编码
     */
    private String tenant = IBizScenario.DEFAULT;

    /**
     * 业务编码，包含ANTI_FRAUD：反欺诈,CREDIT：信贷"
     */
    private String business = IBizScenario.DEFAULT;

    /**
     * 合作方编码
     */
    private String partner = IBizScenario.DEFAULT;


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
        return BizScenario.valueOf(tenant, IBizScenario.DEFAULT, IBizScenario.DEFAULT);
    }

    public static BizScenario valueOf(String tenant, String business){
        return BizScenario.valueOf(tenant, business, IBizScenario.DEFAULT);
    }

    public static BizScenario newDefault(){
        return BizScenario.valueOf(IBizScenario.DEFAULT, IBizScenario.DEFAULT, IBizScenario.DEFAULT);
    }


}
