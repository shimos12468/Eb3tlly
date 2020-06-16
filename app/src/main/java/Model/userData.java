package Model;

import android.widget.EditText;

public class userData {

    private String name;
    private String phone;
    private String email;
    private String date;
    private String id;
    private String accountType;
    private String ppURL,ssnURL;
    private String mpass;
    private String canceled;
    private String ssnNum;

    public userData(){ }

    public userData(String name, String phone, String email,
                    String date, String id, String accountType, String ppURL, String ssnURL,
                    String mpass, String ssnNum, String canceled) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.date = date;
        this.id = id;
        this.accountType = accountType;
        this.ppURL = ppURL;
        this.canceled = canceled;
        this.mpass = mpass;
        this.ssnURL = ssnURL;
        this.ssnNum = ssnNum;
    }

    public String getCanceled() {
        return canceled;
    }
    public void setCanceled(String canceled) {
        this.canceled = canceled;
    }
    public String getSsnNum() {
        return ssnNum;
    }
    public void setSsnNum(String ssnNum) {
        this.ssnNum = ssnNum;
    }
    public String getMpass() { return mpass; }
    public void setMpass(String mpass) { this.mpass = mpass; }
    public String getPpURL() { return ppURL; }
    public void setPpURL(String ppURL) { this.ppURL = ppURL; }
    public String getname() {
        return name;
    }
    public void setname(String name) {
        this.name = name;
    }
    public String getphone() {
        return phone;
    }
    public void setphone(String phone) {
        this.phone = phone;
    }
    public String getemail() {
        return email;
    }
    public void setemail(String id) {
        this.email = email;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAccountType() {
        return accountType;
    }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    public String getSsnURL() {
        return ssnURL;
    }
    public void setSsnURL(String ssnURL) {
        this.ssnURL = ssnURL;
    }
}
