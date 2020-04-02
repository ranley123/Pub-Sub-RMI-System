import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class EventChannel {
    private ArrayList<String> subscriberList;
    private Queue<Event> eventQueue; // maximum size = 1
    private int size = 0;
    private String name;
    ConsumerThread consumerThread;


    public EventChannel(String name){
        subscriberList = new ArrayList<>();
        eventQueue = new LinkedList<Event>();
        this.name = name;
        consumerThread = new ConsumerThread(this);
        consumerThread.start();
    }

    public synchronized int produce(Event event){
        if(size == 1){
            return 1;
        }
        else{
            eventQueue.add(event);
            size++;
            return 0;
        }
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
