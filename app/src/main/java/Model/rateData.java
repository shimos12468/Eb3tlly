package Model;

public class rateData {

    private String rId, datee, oId, dId, sId;
    private int rate;
    private String comment;

    public rateData (){};

    public rateData(String rId, String oId, String sId, String dId, int rate, String comment, String datee){
        this.rId = rId;
        this.oId = oId;
        this.sId = sId;
        this.dId = dId;
        this.rate = rate;
        this.comment = comment;
        this.datee = datee;

    }

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getDatee() {
        return datee;
    }

    public void setDatee(String datee) {
        this.datee = datee;
    }

    public String getoId() {
        return oId;
    }

    public void setoId(String oId) {
        this.oId = oId;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
