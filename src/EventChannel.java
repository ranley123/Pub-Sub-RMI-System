import Exceptions.DuplicateException;
import Exceptions.QueueIsFullException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class EventChannel {
    private ArrayList<String> subscriberList; // a list of subscribers
//    private Queue<Event> eventQueue; // dynamic queue
    private int limit = 5; // queue limit size
    private int maxQueues = 5;
    private String name;
    private ServerImpl server;
    ConsumerThread consumerThread; // holds a thread to everlasting consume events
    ArrayList<Queue<Event>> queues;

    public EventChannel(ServerImpl server, String name){
        this.server = server;
        subscriberList = new ArrayList<>();
//        eventQueue = new LinkedList<>();
        this.name = name; // channel name "apple"

        queues = new ArrayList<>();

        // start the internal thread to consume events
        consumerThread = new ConsumerThread(this);
        consumerThread.start();
    }

    /**
     * adds event to queue
     * @param event
     * @throws QueueIsFullException
     * @throws DuplicateException
     */
    public synchronized void produce(Event event) throws QueueIsFullException, DuplicateException {
        if(queues.size() == 0){
            Queue<Event> newQueue = new LinkedList<>();
            queues.add(newQueue);
        }
        Queue<Event> curQueue = queues.get(queues.size() - 1);

        // if current queue is full
        if(curQueue.size() == limit){
            // the upper bound of queues we can have is reached
            if(queues.size() == maxQueues){
                throw new QueueIsFullException("queue is full");
            }
            // if we are still allowed to add new queue
            else{
                Queue<Event> newQueue = new LinkedList<>();
                queues.add(newQueue);
                newQueue.add(event);
                server.addUUID(event.uuid);
            }
        }
        else{
            curQueue.add(event);
            server.addUUID(event.uuid);
        }
    }

    /**
     * extracts an event from the queue to be consumed by the thread
     * @return - the next event to be consumed
     */
    public synchronized Event consume(){
        if(queues.size() == 0){
            return null;
        }
        // get the first event in the first queue
        Queue<Event> curQueue = queues.get(0);
        if(curQueue.peek() == null){
            queues.remove(curQueue);
            return null;
        }
        else
            return curQueue.poll();
    }

    /**
     * adds a subscriber to the channel
     * @param subscriberName
     */
    public void addSubscriber(String subscriberName){
        subscriberList.add(subscriberName);
    }

    /**
     * removes a subscriber from the channel
     * @param subscriberName
     */
    public void removeSubscriber(String subscriberName){
        subscriberList.remove(subscriberName);
    }

    public ArrayList<String> getSubscriberList(){
        return subscriberList;
    }
}
