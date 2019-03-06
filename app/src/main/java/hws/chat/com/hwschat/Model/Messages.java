package hws.chat.com.hwschat.Model;

import java.util.ArrayList;

public class Messages {
    int room_id;
    ArrayList<ChatMessage> msg;

    public Messages(int room_id, ArrayList<ChatMessage> msg) {
        this.room_id = room_id;
        this.msg = msg;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public ArrayList<ChatMessage> getMsg() {
        return msg;
    }

    public void setMsg(ArrayList<ChatMessage> msg) {
        this.msg = msg;
    }
}
