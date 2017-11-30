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
}
