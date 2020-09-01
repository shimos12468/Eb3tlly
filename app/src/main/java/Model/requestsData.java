package Model;

public class requestsData {
    String id, offer, date;

    public requestsData(){ }

    public requestsData(String id, String offer, String date) {
        this.id = id;
        this.offer = offer;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
