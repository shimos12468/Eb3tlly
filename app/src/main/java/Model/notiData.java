package Model;

public class notiData {

    String from, to, orderid, statue,datee;
    public notiData () {};

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getDatee() {
        return datee;
    }

    public void setDatee(String datee) {
        this.datee = datee;
    }

    public notiData(String from, String to, String orderid, String statue, String datee) {
        this.from = from;
        this.orderid = orderid;
        this.statue = statue;
        this.to = to;
        this.datee = datee;
    }
}