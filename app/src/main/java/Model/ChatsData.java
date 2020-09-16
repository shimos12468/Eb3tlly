package Model;

public class ChatsData {

    public ChatsData () {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    String userId;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    String roomid;
    String timestamp;

    public ChatsData(String userId, String timestamp, String roomid) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.roomid = roomid;
    }


}
