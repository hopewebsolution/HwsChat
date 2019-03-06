package hws.chat.com.hwschat.Model;

public class File_model {
    String file;
    String formate;


    public File_model(String file, String formate) {
        this.file = file;
        this.formate = formate;

    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFormate() {
        return formate;
    }

    public void setFormate(String formate) {
        this.formate = formate;
    }

}
