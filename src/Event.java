import java.io.Serializable;
import java.sql.Timestamp;

public class Event implements Serializable {
    String type;
    Timestamp timestamp;
    int publisherId;
    FruitItem fruitItem;

    public Event(String type, Timestamp timestamp, int publisherId, FruitItem fruitItem){
        this.type = type;
        this.timestamp = timestamp;
        this.publisherId = publisherId;
        this.fruitItem = fruitItem;
    }

    public String toString(){
        return "Event: " + type + " Fruit Name: " + this.fruitItem.fruitName + " Fruit Price: " + this.fruitItem.price;
    }
}
