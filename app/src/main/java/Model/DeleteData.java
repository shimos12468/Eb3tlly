package Model;

public class DeleteData {
    String userID, orderID, reason, date, userType, reasonID;
    public DeleteData () { }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public DeleteData (String userID, String orderID, String reason, String date, String userType, String reasonID) {
        this.date = date;
        this.orderID = orderID;
        this.reason = reason;
        this.userID = userID;
        this.userType = userType;
        this.reasonID = reasonID;
    }

}


