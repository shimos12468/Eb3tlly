package Model;

public class requestsData {
    String id, date, statue;

    public requestsData(){ }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public requestsData(String id, String date, String statue) {
        this.id = id;
        this.date = date;
        this.statue = statue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
