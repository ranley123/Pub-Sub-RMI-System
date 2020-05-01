import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * The permanent message for operation
 */
public class Event implements Serializable {
    String type; // topic
    Timestamp timestamp;
    FruitItem fruitItem; // payload
    UUID uuid; // unique id for identification

    public Event(UUID uuid, String type, Timestamp timestamp, FruitItem fruitItem){
        this.uuid = uuid;
        this.type = type;
        this.timestamp = timestamp;
        this.fruitItem = fruitItem;
    }

    public String toString(){
        return "Event: " + type + " Fruit Name: " + this.fruitItem.fruitName + " Fruit Price: " + this.fruitItem.price;
    }
}
