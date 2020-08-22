package Model;

public class ChatsData {

    public ChatsData () {}
    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    String user1;
    String user2;
    String orderid;
    String groupid;

    public ChatsData(String user1, String user2, String orderid, String groupid) {
        this.user1 = user1;
        this.user2 = user2;
        this.orderid = orderid;
        this.groupid = groupid;
    }


}
