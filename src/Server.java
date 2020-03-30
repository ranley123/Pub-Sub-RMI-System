import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.enumerate;

public class Server extends UnicastRemoteObject implements IServer{
    HashMap<String, Channel> channelMap; // map fruitName to channel
    ArrayList<FruitItem> fruitItemList;
    ConsumerThread consumer;
    String response;
    HashMap<String, IClient> stubMap; // name, stub

    public Server() throws RemoteException {
        channelMap = new HashMap<>();
        fruitItemList = new ArrayList<>();
        stubMap = new HashMap<>();
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
    public void subscribe(String fruitName, String subscriberName) throws RemoteException {
        if(channelMap.containsKey(fruitName)){
            Channel channel = channelMap.get(fruitName);
            channel.addSubscriber(subscriberName);
            System.out.println("successfully subscribe to " + fruitName);
        }
        else{
            Channel channel = new Channel(fruitName);
            channel.addSubscriber(subscriberName);

            channelMap.put(fruitName, channel);

            consumer = new ConsumerThread(channel);

            consumer.start();
        }
    }

    @Override
    public void unsubscribe(String fruitName, String subscriberName) throws RemoteException {
        if(channelMap.containsKey(fruitName)){
            Channel channel = channelMap.get(fruitName);
            channel.removeSubscriber(subscriberName);
            System.out.println("successfully unsubscribe to " + fruitName);
        }
        else{
            System.out.println("Fruit not exists");
            return;
        }
    }

    @Override
    public void publish(Event event) throws RemoteException {
        System.out.println("starts publishing");

        for(int i = 0; i < fruitItemList.size(); i++){
            if(fruitItemList.get(i).fruitName.equals(event.fruitItem.fruitName)){
                System.out.println("fruit already exists");
                return;
            }
        }

        fruitItemList.add(event.fruitItem);
        ArrayList<String> subscriberList = channelMap.get(event.fruitItem.fruitName).getSubscriberList();
        System.out.println(subscriberList);
        try{
            for(String subscriberName: channelMap.get(event.fruitItem.fruitName).getSubscriberList()){

                IClient stub = stubMap.get(subscriberName);
//                System.out.println("server: client stub registered");

                stub.notify("fruit name: " + event.fruitItem.fruitName + " fruit price: " + event.fruitItem.price);
                System.out.println(subscriberName);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void register(int port, String name) throws RemoteException {

        try{
            Registry registry = LocateRegistry.getRegistry(null, port);
            IClient stub = (IClient) registry.lookup(name);
            stub.notify("hddddddddd");
            stubMap.put(name, stub);
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notify(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public String notifyClient() throws RemoteException {
        return response;
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
