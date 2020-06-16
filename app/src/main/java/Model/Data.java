package Model;

public class Data {

    // Pick up Data
    private String mPAddress;
    private String mPShop;
    private String txtPState;
    private String mPRegion;

    // Main
    private String date;
    private String id;
    private String uId;

    // Drop Data
    private String txtDState;
    private String DAddress;
    private String DDate;
    private String DPhone;
    private String DName;
    private String mDRegion;

    // Money Data
    private String GMoney;
    private String GGet;

    // Transportation
    private String isTrans;
    private String isMetro;
    private String isMotor;
    private String isCar;

    private String statue;
    private String uAccepted;

    private String srated;
    private String srateid;

    private String drated;
    private String drateid;
    private String dilverTime;
    private String acceptedTime;
    private String lastedit;
    private String notes;

    public Data(){ }

    public Data(String txtPState, String mPRegion, String mPAddress, String mPShop, String txtDState, String mDRegion, String DAddress, String DDate, String DPhone, String DName,
                String GMoney, String GGet, String date, String id, String uId, String isTrans, String isMetro, String isMotor, String isCar, String statue, String uAccepted, String srated,
                String srateid, String drated, String drateid, String acceptedTime, String dilverTime, String notes) {

        //PICK
        this.txtPState = txtPState;
        this.mPRegion = mPRegion;
        this.mPAddress = mPAddress;
        this.mPShop = mPShop;
        this.notes = notes;

        this.acceptedTime = acceptedTime;
        this.dilverTime = dilverTime;

        //DROP
        this.txtDState = txtDState;
        this.mDRegion = mDRegion;
        this.DAddress = DAddress;
        this.DDate = DDate;
        this.DPhone = DPhone;
        this.DName = DName;
        this.GMoney = GMoney;
        this.GGet = GGet;
        this.date = date;

        // ids
        this.id = id;
        this.uId = uId;

        //Transportation
        this.isTrans  = isTrans;
        this.isMetro = isMetro;
        this.isMotor = isMotor;
        this.isCar = isCar;

        //order statue
        this.statue = statue;
        this.uAccepted = uAccepted;

        this.srated = srated;
        this.srateid = srateid;

        this.drated = drated;
        this.drateid = drateid;
    }

    public String getTxtPState() {
        return txtPState;
    }

    public void setTxtPState(String txtPState) {
        this.txtPState = txtPState;
    }

    public String getmPAddress() {
        return mPAddress;
    }

    public void setmPAddress(String mPAddress) {
        this.mPAddress = mPAddress;
    }

    public String getmPShop() {
        return mPShop;
    }

    public void setmPShop(String mPShop) {
        this.mPShop = mPShop;
    }

    public String getDate() {
        return date;
    }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setDate(String date) {
        this.date = date;
    }
    public String getStatue() {
        return statue;
    }
    public void setStatue(String statue) {
        this.statue = statue;
    }
    public String trans(){ String trans = "( " + isCar + " " + isMetro + " " +isTrans + " " + isMotor + " )";return trans; }
    public String reStateP(){ String reStateP = txtPState + " - " + mPRegion;return reStateP; }
    public String reStateD(){ String reStateD = txtDState + " - " + mDRegion;return reStateD; }
    public String getDilverTime() {
        return dilverTime;
    }
    public void setDilverTime(String dilverTime) {
        this.dilverTime = dilverTime;
    }
    public String getAcceptedTime() {
        return acceptedTime;
    }
    public void setAcceptedTime(String acceptedTime) {
        this.acceptedTime = acceptedTime;
    }
    public String getLastedit() { return lastedit; }
    public void setLastedit(String lastedit) { this.lastedit = lastedit; }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDAddress() {
        return DAddress;
    }
    public String getTxtDState() {
        return txtDState;
    }
    public void setTxtDState(String txtDState) {
        this.txtDState = txtDState;
    }
    public void setDAddress(String DAddress) {
        this.DAddress = DAddress;
    }
    public String getDDate() {
        return DDate;
    }
    public void setDDate(String DDate) {
        this.DDate = DDate;
    }
    public String getDPhone() {
        return DPhone;
    }
    public void setDPhone(String DPhone) {
        this.DPhone = DPhone;
    }
    public String getDName() {
        return DName;
    }
    public void setDName(String DName) {
        this.DName = DName;
    }
    public String getGMoney() {
        return GMoney;
    }
    public void setGMoney(String GMoney) {
        this.GMoney = GMoney;
    }
    public String getGGet() {
        return GGet;
    }
    public void setGGet(String GGet) {
        this.GGet = GGet;
    }
    public String getuId() {
        return uId;
    }
    public void setuId(String uId) {
        this.uId = uId;
    }
    public String getIsTrans() {
        return isTrans;
    }
    public void setIsTrans(String isTrans) {
        this.isTrans = isTrans;
    }
    public String getIsMetro() { return isMetro; }
    public void setIsMetro(String isMetro) { this.isMetro = isMetro;
    }

    public String getIsMotor() {
        return isMotor;
    }

    public void setIsMotor(String isMotor) {
        this.isMotor = isMotor;
    }

    public String getIsCar() {
        return isCar;
    }

    public void setIsCar(String isCar) {
        this.isCar = isCar;
    }

    public String getmPRegion() {
        return mPRegion;
    }

    public void setmPRegion(String mPRegion) {
        this.mPRegion = mPRegion;
    }

    public String getmDRegion() {
        return mDRegion;
    }

    public void setmDRegion(String mDRegion) {
        this.mDRegion = mDRegion;
    }
    public String getuAccepted() { return uAccepted;
    }

    public String getSrated() {
        return srated;
    }

    public void setSrated(String srated) {
        this.srated = srated;
    }

    public String getSrateid() {
        return srateid;
    }

    public void setSrateid(String srateid) {
        this.srateid = srateid;
    }

    public String getDrated() {
        return drated;
    }

    public void setDrated(String drated) {
        this.drated = drated;
    }

    public String getDrateid() {
        return drateid;
    }

    public void setDrateid(String drateid) {
        this.drateid = drateid;
    }

    public void setuAccepted(String uAccepted) {
        this.uAccepted = uAccepted;
    }

}
