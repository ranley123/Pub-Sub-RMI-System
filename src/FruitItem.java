import java.io.Serializable;

public class FruitItem implements Serializable {
    String fruitName;
    double price;

    public FruitItem(String fruitName, double price){
        this.fruitName = fruitName;
        this.price = price;
    }
}
