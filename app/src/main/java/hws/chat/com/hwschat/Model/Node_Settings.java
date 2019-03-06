package hws.chat.com.hwschat.Model;

public class Node_Settings {
    String node_url;
    int user_id;
    String api_for_file;
    String post_file_key;
    String current_user_name;
    String current_user_profile;

    public Node_Settings(String node_url, int user_id, String api_for_file, String post_file_key, String current_user_name, String current_user_profile) {
        this.node_url = node_url;
        this.user_id = user_id;
        this.api_for_file = api_for_file;
        this.post_file_key = post_file_key;
        this.current_user_name = current_user_name;
        this.current_user_profile = current_user_profile;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getNode_url() {
        return node_url;
    }

    public void setNode_url(String node_url) {
        this.node_url = node_url;
    }

    public String getApi_for_file() {
        return api_for_file;
    }

    public void setApi_for_file(String api_for_file) {
        this.api_for_file = api_for_file;
    }

    public String getPost_file_key() {
        return post_file_key;
    }

    public void setPost_file_key(String post_file_key) {
        this.post_file_key = post_file_key;
    }

    public String getCurrent_user_name() {
        return current_user_name;
    }

    public void setCurrent_user_name(String current_user_name) {
        this.current_user_name = current_user_name;
    }

    public String getCurrent_user_profile() {
        return current_user_profile;
    }

    public void setCurrent_user_profile(String current_user_profile) {
        this.current_user_profile = current_user_profile;
    }
}
