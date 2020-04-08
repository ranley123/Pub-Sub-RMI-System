import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class EventChannel {
    private ArrayList<String> subscriberList;
    private Queue<Event> eventQueue; // maximum size = 1
    private int limit = 0;
    private String name;
    ConsumerThread consumerThread;


    public EventChannel(String name){
        subscriberList = new ArrayList<>();
        eventQueue = new LinkedList<Event>();
        this.name = name;
        consumerThread = new ConsumerThread(this);
        consumerThread.start();
    }

    public synchronized void produce(Event event) throws QueueIsFullException {
        if(eventQueue.size() == limit){
            limit++;
            throw new QueueIsFullException("queue is full");
        }
        else{
            eventQueue.add(event);
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
