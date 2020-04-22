package cn.tongdun.kunpeng.api.consumer.customlist;

import cn.tongdun.kunpeng.api.consumer.util.JsonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Data
public class AddCustomListAction {

    private List matchField;
    private String customListUuid;
    private Integer extension; //有效时长，小时为单位
    private Integer period; //时间周期
    private String unit; //单位

    public String getCustomListUuid() {
        return customListUuid;
    }

    public void setCustomListUuid(String customListUuid) {
        this.customListUuid = customListUuid;
    }

    public static AddCustomListAction parse(Map json) {
        String customListUuid = JsonUtil.getString(json,"customListUuid");
        String periodStr = JsonUtil.getString(json,"period");
        String unit = JsonUtil.getString(json,"unit");
        List matchField = (List)json.get("matchField");
        Integer extension = null;
        int timeslice = 0;
        if (!StringUtils.isAnyBlank(periodStr, unit)) {
            try {
                timeslice = Integer.parseInt(periodStr);
            } catch (Exception e) {

            }
            switch (unit) {
                case "h" :
                    extension = timeslice;
                    break;
                case "d" :
                    extension = timeslice*24;
                    break;
                case "w" :
                    extension = timeslice*7*24;
                    break;
                case "m" :
                    extension = timeslice*30*24;
                    break;
                default:
                    break;
            }
        }
        AddCustomListAction result = new AddCustomListAction();
        if (null == matchField || matchField.isEmpty()){
            result.setMatchField(null);
        } else {
            result.setMatchField(matchField);
        }
        result.setCustomListUuid(customListUuid);
        result.setPeriod(timeslice);
        result.setUnit(unit);
        if (extension!=null){
            result.setExtension(extension);
        }
        return result;
    }


}
