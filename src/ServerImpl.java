import Exceptions.DataLossException;
import Exceptions.DuplicateException;
import Exceptions.QueueIsFullException;

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
    ArrayList<UUID> UUIDStore; //  database for all event id to check duplicate

    /**
     * initialise data structures
     * @throws RemoteException
     */
    public ServerImpl() throws RemoteException {
        eventChannelMap = new HashMap<>();
        messageChannelMap = new HashMap<>();
        stubMap = new HashMap<>();
        fruitItemList = new ArrayList<>();
        UUIDStore = new ArrayList<>();
    }

    /**
     * adds event to a corresponding event channel
     * @param event - published event from publisher
     * @throws RemoteException
     * @throws DataLossException
     * @throws QueueIsFullException
     */
    @Override
    public void addEvent(Event event) throws RemoteException, DataLossException, QueueIsFullException, DuplicateException {
        if(checkEventDataLoss(event)){
            throw new DataLossException("data loss");
        }

        String fruitName = event.fruitItem.fruitName;

        // if the topic not exists
        if(!eventChannelMap.containsKey(fruitName)){
            EventChannel newChannel = new EventChannel(this, fruitName);
            eventChannelMap.put(fruitName, newChannel);
            newChannel.produce(event);
        }
        else{
            EventChannel channel = eventChannelMap.get(fruitName);
            channel.produce(event);
        }
    }

    /**
     * subscribers call this to subscribe to a topic
     * @param fruitName - the topic
     * @param subscriberName - the subscriber name to be added to that channel
     * @throws RemoteException
     */
    @Override
    public void subscribe(String fruitName, String subscriberName) throws RemoteException {
        // if the channel already exists
        if(eventChannelMap.containsKey(fruitName)){
            EventChannel channel = eventChannelMap.get(fruitName);
            channel.addSubscriber(subscriberName);
        }
        // if the channel not exists
        else{
            EventChannel channel = new EventChannel(this, fruitName);
            channel.addSubscriber(subscriberName);

            eventChannelMap.put(fruitName, channel);
        }
    }

    /**
     * subscribers call this to unsubscribe to a topic
     * @param fruitName - the topic they want to unsubscribe
     * @param subscriberName - the subscriber name
     * @throws RemoteException
     */
    @Override
    public void unsubscribe(String fruitName, String subscriberName) throws RemoteException {
        if(eventChannelMap.containsKey(fruitName)){
            EventChannel channel = eventChannelMap.get(fruitName);
            channel.removeSubscriber(subscriberName);
        }
        else{
            return;
        }
    }

    /**
     * server consumes events and updates its database, notifies corresponding subscribers
     * @param event
     * @throws RemoteException
     */
    @Override
    public void publish(Event event) throws RemoteException {
        boolean exists = false; // true for an existing fruit

        // if the fruit exists, updates its price
        for(int i = 0; i < fruitItemList.size(); i++){
            if(fruitItemList.get(i).fruitName.equals(event.fruitItem.fruitName)){
                fruitItemList.set(i, event.fruitItem);
                exists = true;
                break;
            }
        }
        // if not exists, add the new fruit
        if(!exists){
            fruitItemList.add(event.fruitItem);
        }

        // starts to push non-permanent messages to subscribers
        // get subscribers of the channel
        ArrayList<String> subscriberList = eventChannelMap.get(event.fruitItem.fruitName).getSubscriberList();

        // creates a new message with type "subscribe" for subscribed messages
        Message subscribedMessage = new Message("subscribe", "fruit name: " + event.fruitItem.fruitName + " fruit price: " + event.fruitItem.price, "server");
        // set the message channel id as the related event id. Since the message relates to the event
        subscribedMessage.setMessageChannelId(event.uuid);

        // creates a new message channel to hold "response" type acknowledgement messages from subscribers
        MessageChannel messageChannel = new MessageChannel(this, subscribedMessage, new ArrayList<>(subscriberList), "message");
        // because the channel is for the specific event's response
        messageChannelMap.put(event.uuid, messageChannel);

        // sends the message to subscribers, if no subscribers then the message is dropped
        notifyClient(subscriberList, subscribedMessage);

        // message channel waiting for responses from subscribers to solve crashing customers
        messageChannel.startReceivingMessage();
    }

    /**
     * subscribers register themselves for future call-back
     * @param port
     * @param name
     * @throws RemoteException
     */
    @Override
    public void register(int port, String name) throws RemoteException {
        try{
            Registry registry = LocateRegistry.getRegistry(null, port);
            IClient stub = (IClient) registry.lookup(name);

            // acknowledgement message to subscribers
            Message message = new Message("response", "Subscriber Registered", "server");
            stub.notify(message);

            // save stub of registered subscriber
            stubMap.put(name, stub);
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }

    // called by client, message client -> server
    @Override
    public void notify(Message message) throws RemoteException {
        System.out.println("sender: " + message.senderName + " content: " + message.content);
    }

    /**
     * called by subscribers to add acknowledgement response messages to an event
     * @param message
     * @throws RemoteException
     * @throws DataLossException
     */
    @Override
    public void addMessage(Message message) throws RemoteException, DataLossException {
        if (checkMessageDataLoss(message)){
            throw new DataLossException("data loss");
        }
        // push message
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

    private boolean checkEventDataLoss(Event event){
        try{
            return (event.type == null || event.type.length() == 0 || event.timestamp == null
            || event.fruitItem == null || event.uuid == null);
        }
        catch (Exception e){
            return true;
        }
    }

    private boolean checkMessageDataLoss(Message message){
        try{
            return (message.type == null || message.type.length() == 0 || message.content == null
                    || message.content.length() == 0 || message.senderName == null
                    || message.senderName.length() == 0 || message.messageChannelId == null);
        }
        catch (Exception e){
            return true;
        }
    }

    public void addUUID(UUID uuid) throws DuplicateException {
        if(UUIDStore.contains(uuid)){
            throw new DuplicateException("duplicate event");
        }
        else{
            UUIDStore.add(uuid);
        }
    }

}
