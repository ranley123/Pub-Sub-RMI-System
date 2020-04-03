import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

public class Event implements Serializable {
    String type;
    Timestamp timestamp; // used as an id
    FruitItem fruitItem;
    UUID uuid;

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
