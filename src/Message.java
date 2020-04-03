import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    String type;
    String content;
    int status;
    String senderName;
    UUID messageChannelId;

    public Message(String type, String content, int status, String senderName){
        this.type = type;
        this.content = content;
        this.status = status;
        this.senderName = senderName;
    }

    public void setMessageChannelId(UUID messageChannelId){
        this.messageChannelId = messageChannelId;
    }
}
