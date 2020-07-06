package Model;

public class reportData {
    String userID,reportedID,orderID,date,type,id;
    public reportData() {}

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getReportedID() {
        return reportedID;
    }

    public void setReportedID(String reportedID) {
        this.reportedID = reportedID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public reportData(String userID, String reportedID, String orderID, String date, String type, String id) {
        this.userID = userID;
        this.reportedID = reportedID;
        this.orderID= orderID;
        this.date = date;
        this.type = type;
        this.id = id;
    }
}
