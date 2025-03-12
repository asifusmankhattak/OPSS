package pbs.sme.survey.model;

import androidx.annotation.Keep;

@Keep
public class Forgot {

    protected int code;
    protected String msg;
    protected String sms;
    protected String mobile;
    protected String email;
    protected int sms_status;
    protected int email_status;
    protected int expiry;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSms_status() {
        return sms_status;
    }

    public void setSms_status(int sms_status) {
        this.sms_status = sms_status;
    }

    public int getEmail_status() {
        return email_status;
    }

    public void setEmail_status(int email_status) {
        this.email_status = email_status;
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(int expiry) {
        this.expiry = expiry;
    }
}
