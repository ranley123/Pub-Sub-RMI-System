import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Channel{
    private ArrayList<SubscriberClient> subscriberList;
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

    public void addSubscriber(SubscriberClient subscriber){
        subscriberList.add(subscriber);
    }

    public void removeSubscriber(SubscriberClient subscriber){
        subscriberList.remove(subscriber);
    }

    public ArrayList<SubscriberClient> getSubscriberList(){
        return subscriberList;
    }
}
