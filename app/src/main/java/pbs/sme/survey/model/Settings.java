package pbs.sme.survey.model;

import androidx.annotation.Keep;

import pk.gov.pbs.database.annotations.PrimaryKey;

@Keep
public class Settings {
    protected int id;
    @PrimaryKey
    protected String app;
    protected float version;
    protected int updateOption;
    protected int readTimeSec;
    protected int writeTimeSec;
    protected int connTimeSec;
    protected int loginAttempts;
    protected int loginDelayMints;
    protected boolean sameDaySync;
    protected String startTime;
    protected String endTime;
    protected int remarksLimit;
    protected int accuracyMeter;
    protected int updateMinutes;
    protected int updateDistance;
    protected String otherUrl;
    protected int maxWeeks;
    protected int loginExpireHours;
    protected String updatedTime;
    protected String env;
    protected String def;
    protected String apk_path;

    @Override
    public String toString() {
        return "Settings{" +
                "id=" + id +
                ", app='" + app + '\'' +
                ", version=" + version +
                ", updateOption=" + updateOption +
                ", readTimeSec=" + readTimeSec +
                ", writeTimeSec=" + writeTimeSec +
                ", connTimeSec=" + connTimeSec +
                ", loginAttempts=" + loginAttempts +
                ", loginDelayMints=" + loginDelayMints +
                ", sameDaySync=" + sameDaySync +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", remarksLimit=" + remarksLimit +
                ", accuracyMeter=" + accuracyMeter +
                ", updateMinutes=" + updateMinutes +
                ", updateDistance=" + updateDistance +
                ", otherUrl='" + otherUrl + '\'' +
                ", maxWeeks=" + maxWeeks +
                ", loginExpireHours=" + loginExpireHours +
                ", updatedTime='" + updatedTime + '\'' +
                ", env='" + env + '\'' +
                ", def='" + def + '\'' +
                ", apk_path='" + apk_path + '\'' +
                '}';
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getApk_path() {
        return apk_path;
    }

    public void setApk_path(String apk_path) {
        this.apk_path = apk_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public int getReadTimeSec() {
        return readTimeSec;
    }

    public void setReadTimeSec(int readTimeSec) {
        this.readTimeSec = readTimeSec;
    }

    public int getWriteTimeSec() {
        return writeTimeSec;
    }

    public void setWriteTimeSec(int writeTimeSec) {
        this.writeTimeSec = writeTimeSec;
    }

    public int getConnTimeSec() {
        return connTimeSec;
    }

    public void setConnTimeSec(int connTimeSec) {
        this.connTimeSec = connTimeSec;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public int getLoginDelayMints() {
        return loginDelayMints;
    }

    public void setLoginDelayMints(int loginDelayMints) {
        this.loginDelayMints = loginDelayMints;
    }

    public boolean isSameDaySync() {
        return sameDaySync;
    }

    public void setSameDaySync(boolean sameDaySync) {
        this.sameDaySync = sameDaySync;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getRemarksLimit() {
        return remarksLimit;
    }

    public void setRemarksLimit(int remarksLimit) {
        this.remarksLimit = remarksLimit;
    }

    public int getAccuracyMeter() {
        return accuracyMeter;
    }

    public void setAccuracyMeter(int accuracyMeter) {
        this.accuracyMeter = accuracyMeter;
    }

    public int getUpdateMinutes() {
        return updateMinutes;
    }

    public void setUpdateMinutes(int updateMinutes) {
        this.updateMinutes = updateMinutes;
    }

    public int getUpdateDistance() {
        return updateDistance;
    }

    public void setUpdateDistance(int updateDistance) {
        this.updateDistance = updateDistance;
    }

    public String getOtherUrl() {
        return otherUrl;
    }

    public void setOtherUrl(String otherUrl) {
        this.otherUrl = otherUrl;
    }

    public int getMaxWeeks() {
        return maxWeeks;
    }

    public void setMaxWeeks(int maxWeeks) {
        this.maxWeeks = maxWeeks;
    }

    public int getLoginExpireHours() {
        return loginExpireHours;
    }

    public void setLoginExpireHours(int loginExpireHours) {
        this.loginExpireHours = loginExpireHours;
    }

    public int getUpdateOption() {
        return updateOption;
    }

    public void setUpdateOption(int updateOption) {
        this.updateOption = updateOption;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }
}
