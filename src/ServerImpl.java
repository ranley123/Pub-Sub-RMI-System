import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ServerImpl extends UnicastRemoteObject implements IServer{
    HashMap<String, EventChannel> eventChannelMap; // map fruitName to channel
    HashMap<UUID, MessageChannel> messageChannelMap;
    ArrayList<FruitItem> fruitItemList;
    HashMap<String, IClient> stubMap; // name, stub


    public ServerImpl() throws RemoteException {
        eventChannelMap = new HashMap<>();
        messageChannelMap = new HashMap<>();
        fruitItemList = new ArrayList<>();
        stubMap = new HashMap<>();
    }

    @Override
    public int addEvent(Event event) throws RemoteException{
        String fruitName = event.fruitItem.fruitName;
//        System.out.println("adding event to channel");
        int errcode;

        if(!eventChannelMap.containsKey(fruitName)){
            EventChannel newChannel = new EventChannel(fruitName);
            eventChannelMap.put(fruitName, newChannel);
            errcode = newChannel.produce(event);

//            consumer = new ConsumerThread(newChannel);
//            consumer.start();
        }
        else{
            EventChannel channel = eventChannelMap.get(fruitName);
            errcode = channel.produce(event);
        }
        return errcode;
    }

    @Override
    public void subscribe(String fruitName, String subscriberName) throws RemoteException {
        if(eventChannelMap.containsKey(fruitName)){
            EventChannel channel = eventChannelMap.get(fruitName);
            channel.addSubscriber(subscriberName);
            System.out.println("successfully subscribe to " + fruitName);
        }
        else{
            EventChannel channel = new EventChannel(fruitName);
            channel.addSubscriber(subscriberName);

            eventChannelMap.put(fruitName, channel);
//            consumer = new ConsumerThread(channel);
//            consumer.start();
        }
    }

    @Override
    public void unsubscribe(String fruitName, String subscriberName) throws RemoteException {
        if(eventChannelMap.containsKey(fruitName)){
            EventChannel channel = eventChannelMap.get(fruitName);
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
//        System.out.println("starts publishing");

        for(int i = 0; i < fruitItemList.size(); i++){
            if(fruitItemList.get(i).fruitName.equals(event.fruitItem.fruitName)){
                System.out.println("fruit already exists");
                return;
            }
        }

        fruitItemList.add(event.fruitItem);
        ArrayList<String> subscriberList = eventChannelMap.get(event.fruitItem.fruitName).getSubscriberList();
//        System.out.println(subscriberList);
        Message subscribedMessage = new Message("subscribe", "fruit name: " + event.fruitItem.fruitName + " fruit price: " + event.fruitItem.price, 0, "server");
        subscribedMessage.setMessageChannelId(event.uuid);

        MessageChannel messageChannel = new MessageChannel(this, subscribedMessage, subscriberList, "message");
        messageChannelMap.put(event.uuid, messageChannel);

        notifyClient(subscriberList, subscribedMessage);

        messageChannel.startReceivingMessage();
    }

    @Override
    public void register(int port, String name) throws RemoteException {

        try{
            Registry registry = LocateRegistry.getRegistry(null, port);
            IClient stub = (IClient) registry.lookup(name);
            Message message = new Message("response", "Subscriber Registered", 0, "server");
            stub.notify(message);
            stubMap.put(name, stub);
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }

    // called by client, information client -> server
    @Override
    public void notify(Message message) throws RemoteException {
        System.out.println("sender: " + message.senderName + " content: " + message.content);
    }

    @Override
    public void addMessage(Message message) throws RemoteException{
        messageChannelMap.get(message.messageChannelId).produce(message);
    }

    // information server -> client
    public void notifyClient(ArrayList<String> subscriberList, Message message){
        try{
            for(String subscriberName: subscriberList){

                IClient stub = stubMap.get(subscriberName);
//                System.out.println("server: client stub registered");

                stub.notify(message);
//                System.out.println(subscriberName);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
