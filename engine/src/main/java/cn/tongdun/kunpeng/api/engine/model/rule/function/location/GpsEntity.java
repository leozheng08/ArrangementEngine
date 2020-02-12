package cn.tongdun.kunpeng.api.engine.model.rule.function.location;

import java.io.Serializable;

public class GpsEntity implements Serializable {

    private double longitude;
    private double latitude;

    public GpsEntity(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "GpsEntity [lon=" + longitude + ", lat=" + latitude + "]";
    }
}
