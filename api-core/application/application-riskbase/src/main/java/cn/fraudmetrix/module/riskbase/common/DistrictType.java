package cn.fraudmetrix.module.riskbase.common;

public enum DistrictType {
    // 国家，地区，省份，城市，区县，乡村，市辖区
    COUNTRY, AREA, PROVINCE, CITY, COUNTY, VILLAGE, DISTRICT;

    public static DistrictType getDistrictType(int code) {
        switch (code) {
            case 0:
                return COUNTRY;
            case 1:
                return AREA;
            case 2:
                return PROVINCE;
            case 3:
            case 31:
            case 32:
            case 33:
                return CITY;
            case 4:
            case 41:
            case 42:
            case 43:
                return COUNTY;
            case 5:
                return VILLAGE;
            case 11:
                return DISTRICT;
            default:
                return null;
        }
    }
}
