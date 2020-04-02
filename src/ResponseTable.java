import java.util.ArrayList;

public class ResponseTable {
    private ArrayList<String> responserNames;

    public ResponseTable(){
        responserNames = new ArrayList<>();
    }

    public void updateResponseTable(String name){
        responserNames.remove(name);
    }

    public void setResponserTable(ArrayList<String> responserNames){
        this.responserNames = responserNames;
    }

    public ArrayList<String> getResponseTable(){
        return responserNames;
    }
}
