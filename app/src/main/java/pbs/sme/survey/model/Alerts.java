package pbs.sme.survey.model;

import androidx.annotation.Keep;

import pk.gov.pbs.database.annotations.PrimaryKey;

@Keep
public class Alerts {
    @PrimaryKey
    protected int id;
    protected String title;
    protected String msg;
    protected int app;
    protected int userid;
    protected String created;
    protected String expiry;

    @Override
    public String toString() {
        return "Alerts{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", msg='" + msg + '\'' +
                ", app=" + app +
                ", userid=" + userid +
                ", created='" + created + '\'' +
                ", expiry='" + expiry + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getApp() {
        return app;
    }

    public void setApp(int app) {
        this.app = app;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
