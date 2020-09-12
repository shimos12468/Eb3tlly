package Model;

public class ChatsData {

    public ChatsData () {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    String userId;
    String orderid;
    String roomid;

    public ChatsData(String userId, String orderid, String roomid) {
        this.userId = userId;
        this.orderid = orderid;
        this.roomid = roomid;
    }


}
