package hws.chat.com.hwschat.Model;

public class ChatMessage {
    private String body;
    private boolean isMine;
    private String date;
    private int extra_key;
    private int room_id, sender, receiver;
    Users users;
    File_model fileModel;

    public ChatMessage(String body, int extra_key,boolean isMine, String date, int room_id, int sender, int receiver, Users users, File_model fileModel) {
        this.body = body;
        this.extra_key=extra_key;
        this.isMine = isMine;
        this.date = date;
        this.room_id = room_id;
        this.sender = sender;
        this.receiver = receiver;
        this.users = users;
        this.fileModel = fileModel;
    }

    public int getExtra_key() {
        return extra_key;
    }

    public void setExtra_key(int extra_key) {
        this.extra_key = extra_key;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
    public File_model getFileModel() {
        return fileModel;
    }

    public void setFileModel(File_model fileModel) {
        this.fileModel = fileModel;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
