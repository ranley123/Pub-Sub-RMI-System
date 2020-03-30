import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Channel{
    private ArrayList<String> subscriberList;
    private Queue<Event> eventQueue;
    private String name;


    public Channel(String name){
        subscriberList = new ArrayList<>();
        eventQueue = new LinkedList<Event>();
        this.name = name;
    }

    public synchronized void produce(Event event){
        eventQueue.add(event);
    }

    public synchronized Event consume(){
        if(eventQueue.peek() == null)
            return null;
        else
            return eventQueue.poll();
    }

    public void addSubscriber(String subscriberName){
        subscriberList.add(subscriberName);
    }

    public void removeSubscriber(String subscriberName){
        subscriberList.remove(subscriberName);
    }

    public ArrayList<String> getSubscriberList(){
        return subscriberList;
    }
}
