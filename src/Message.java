import java.io.Serializable;
import java.util.UUID;

/**
 * Transient messages for response and notification
 */
public class Message implements Serializable {
    String type; // "response" from subscribers, "subscribe" from servers to subscribers
    String content; // message content
    String senderName;
    UUID messageChannelId; // unique id of current message channel, it is the related event id
    UUID messageId; // unique id

    public Message(String type, String content, String senderName){
        this.type = type;
        this.content = content;
        this.senderName = senderName;
        messageId = UUID.randomUUID();
    }

    public void setMessageChannelId(UUID messageChannelId){
        this.messageChannelId = messageChannelId;
    }
}
