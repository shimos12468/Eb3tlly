package Model;

public class replyAdmin {
    public replyAdmin () {}

    String email, message, name, phone,statue,timestamp,id,currentVersion,uID;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public replyAdmin(String email, String message, String name, String phone, String statue, String timestamp, String id, String currentVersion, String uID) {
        this.email = email;
        this.message = message;
        this.name = name;
        this.phone = phone;
        this.statue = statue;
        this.timestamp = timestamp;
        this.id = id;
        this.uID = uID;
        this.currentVersion = currentVersion;
    }
}
