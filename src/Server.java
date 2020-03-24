import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.enumerate;

public class Server extends UnicastRemoteObject implements IServer{
    HashMap<String, Channel> channelMap; // map fruitName to channel
    ArrayList<FruitItem> fruitItemList;
    ConsumerThread consumer;

    public Server() throws RemoteException {
        channelMap = new HashMap<>();
        fruitItemList = new ArrayList<>();
    }

    @Override
    public void addEvent(Event event) throws RemoteException{
        String fruitName = event.fruitItem.fruitName;
        System.out.println("adding event to channel");


        if(!channelMap.containsKey(fruitName)){
            Channel newChannel = new Channel(fruitName);
            channelMap.put(fruitName, newChannel);
            newChannel.produce(event);

            consumer = new ConsumerThread(newChannel);
            consumer.start();
        }
        else{
            Channel channel = channelMap.get(fruitName);
            channel.produce(event);
        }

    }

    @Override
    public String ping() throws RemoteException{
        return "connected";
    }

    @Override
    public void subscribe(String fruitName, SubscriberClient subscriber) throws RemoteException {
        if(channelMap.containsKey(fruitName)){
            Channel channel = channelMap.get(fruitName);
            channel.addSubscriber(subscriber);
            System.out.println("successfully subscribe to " + fruitName);
        }
        else{
            Channel channel = new Channel(fruitName);
            channel.addSubscriber(subscriber);

            channelMap.put(fruitName, channel);

            consumer = new ConsumerThread(channel);

            consumer.start();
//            channel.start();
        }
    }

    @Override
    public void unsubscribe(String fruitName, SubscriberClient subscriber) throws RemoteException {
        if(channelMap.containsKey(fruitName)){
            Channel channel = channelMap.get(fruitName);
            channel.removeSubscriber(subscriber);
            System.out.println("successfully unsubscribe to " + fruitName);
        }
        else{
            System.out.println("Fruit not exists");
            return;
        }
    }

    @Override
    public void publish(Event event){
        System.out.println("starts publishing");

        for(int i = 0; i < fruitItemList.size(); i++){
            if(fruitItemList.get(i).equals(event.fruitItem)){
                System.out.println("fruit already exists");
                return;
            }
        }

        fruitItemList.add(event.fruitItem);

        for(SubscriberClient subscriber: channelMap.get(event.fruitItem.fruitName).getSubscriberList()){
//            if(subscriber.equals(event.publisher)){
//                continue;
//            }

            subscriber.notify(event.toString());
        }
    }

    @Override
    public void update(Event event){
//        String fruitName = event.fruitItem.fruitName;
//
//        if(!channelMap.containsKey(fruitName)){
//            System.out.println("fruit not exists");
//            return;
//        }
//        else{
//            channelMap.get(fruitName).push(fruitItemList, event);
//        }
    }
//
//    @Override
//    public void delete(Event event) {
//
//    }
}
