import Exceptions.DuplicateException;
import Exceptions.QueueIsFullException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class EventChannel {
    private ArrayList<String> subscriberList; // a list of subscribers
    private Queue<Event> eventQueue; // dynamic queue
    private int limit = 5; // queue limit size
    private String name;
    private ServerImpl server;
    ConsumerThread consumerThread; // holds a thread to everlasting consume events


    public EventChannel(ServerImpl server, String name){
        this.server = server;
        subscriberList = new ArrayList<>();
        eventQueue = new LinkedList<>();
        this.name = name; // channel name "apple"

        // start the internal thread to consume events
        consumerThread = new ConsumerThread(this);
        consumerThread.start();
    }

    /*
    adds an event to the queue if not full
     */
    public synchronized void produce(Event event) throws QueueIsFullException, DuplicateException {
        if(eventQueue.size() == limit){
            limit++;
            throw new QueueIsFullException("queue is full");
        }
        else{
            eventQueue.add(event);
            server.addUUID(event.uuid);
        }
    }

    /*
    extracts an event from the queue to be consumed by the thread
     */
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
