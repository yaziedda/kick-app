package id.co.icg.rnd.kickmode;

/**
 * Created by Dizzay on 11/30/2017.
 */

public class LoginModel {

    private String id;
    private String appVersion;
    private String androidType;
    private String androidOs;
    private String latestIP;
    private String location;
    private boolean active;
    private double lat;
    private double lng;
    private String time;
    private String simpleLocation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAndroidType() {
        return androidType;
    }

    public void setAndroidType(String androidType) {
        this.androidType = androidType;
    }

    public String getAndroidOs() {
        return androidOs;
    }

    public void setAndroidOs(String androidOs) {
        this.androidOs = androidOs;
    }

    public String getLatestIP() {
        return latestIP;
    }

    public void setLatestIP(String latestIP) {
        this.latestIP = latestIP;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSimpleLocation() {
        return simpleLocation;
    }

    public void setSimpleLocation(String simpleLocation) {
        this.simpleLocation = simpleLocation;
    }
}
