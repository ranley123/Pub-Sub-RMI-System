import java.io.Serializable;

public class Message implements Serializable {
    String type;
    String content;
    int status;
    String senderName;

    public Message(String type, String content, int status, String senderName){
        this.type = type;
        this.content = content;
        this.status = status;
        this.senderName = senderName;
    }
}
