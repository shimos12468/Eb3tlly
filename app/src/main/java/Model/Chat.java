package Model;

public class Chat {
    private String senderid;

    public Chat(String senderid, String reciverid, String msg, String timestamp) {
        this.senderid = senderid;
        this.reciverid = reciverid;
        this.msg = msg;
        this.timestamp = timestamp;
    }

    public Chat () { }
    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getReciverid() {
        return reciverid;
    }

    public void setReciverid(String reciverid) {
        this.reciverid = reciverid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String reciverid;
    private String msg;
    private String timestamp;
}
