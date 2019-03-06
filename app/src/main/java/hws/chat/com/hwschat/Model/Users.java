package hws.chat.com.hwschat.Model;

public class Users {
    int extra_key, roomid, recever, sender;
    String name, profile;
    String username, userprofile;

    public int getExtra_key() {
        return extra_key;
    }

    public void setExtra_key(int extra_key) {
        this.extra_key = extra_key;
    }

    public int getRoomid() {
        return roomid;
    }

    public int getRecever() {
        return recever;
    }

    public void setRecever(int recever) {
        this.recever = recever;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserprofile() {
        return userprofile;
    }

    public void setUserprofile(String userprofile) {
        this.userprofile = userprofile;
    }

}
