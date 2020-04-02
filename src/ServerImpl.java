import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerImpl extends UnicastRemoteObject implements IServer{
    HashMap<String, EventChannel> channelMap; // map fruitName to channel
    ArrayList<FruitItem> fruitItemList;
//    ConsumerThread consumer;
    MessageChannel messageChannel;
    String response;
    HashMap<String, IClient> stubMap; // name, stub
    HashMap<Integer, ResponseTable> responseTableMap; // integer: id
    int id; // for responseTableMap


    public ServerImpl() throws RemoteException {
        channelMap = new HashMap<>();
        fruitItemList = new ArrayList<>();
        stubMap = new HashMap<>();
        messageChannel = new MessageChannel(this, "message");
        responseTableMap = new HashMap<>();
    }

    @Override
    public int addEvent(Event event) throws RemoteException{
        String fruitName = event.fruitItem.fruitName;
        System.out.println("adding event to channel");
        int errcode;

        if(!channelMap.containsKey(fruitName)){
            EventChannel newChannel = new EventChannel(fruitName);
            channelMap.put(fruitName, newChannel);
            errcode = newChannel.produce(event);

//            consumer = new ConsumerThread(newChannel);
//            consumer.start();
        }
        else{
            EventChannel channel = channelMap.get(fruitName);
            errcode = channel.produce(event);
        }
        return errcode;
    }

    @Override
    public void subscribe(String fruitName, String subscriberName) throws RemoteException {
        if(channelMap.containsKey(fruitName)){
            EventChannel channel = channelMap.get(fruitName);
            channel.addSubscriber(subscriberName);
            System.out.println("successfully subscribe to " + fruitName);
        }
        else{
            EventChannel channel = new EventChannel(fruitName);
            channel.addSubscriber(subscriberName);

            channelMap.put(fruitName, channel);
//            consumer = new ConsumerThread(channel);
//            consumer.start();
        }
    }

    @Override
    public void unsubscribe(String fruitName, String subscriberName) throws RemoteException {
        if(channelMap.containsKey(fruitName)){
            EventChannel channel = channelMap.get(fruitName);
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
//        System.out.println(subscriberList);


        try{
            for(String subscriberName: subscriberList){

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
            stub.notify("Subscriber Registered");
            stubMap.put(name, stub);
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notify(Message message) throws RemoteException {
        System.out.println("sender: " + message.senderName + " content: " + message.content);
        if(message.type == "response"){
            String fruitName = message.content.split(" ")[0];

        }
    }

    @Override
    public void receiveMessage(Message message) throws RemoteException {
        this.messageChannel.produce(message);
    }
}
