package Model;

public class ConfirmationData {
    String ssnURL,isConfirmed,id,date;

    public ConfirmationData () {};

    public String getSsnURL() { return ssnURL; }

    public void setSsnURL(String ssnURL) { this.ssnURL = ssnURL; }

    public String getIsConfirmed() { return isConfirmed; }

    public void setIsConfirmed(String isConfirmed) { this.isConfirmed = isConfirmed; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public ConfirmationData(String ssnURL, String isConfirmed, String id, String date) {
        this.ssnURL = ssnURL;
        this.isConfirmed = isConfirmed;
        this.id = id;
        this.date = date;
    }
}
