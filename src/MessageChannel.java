import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class MessageChannel {
    private Queue<Message> messageQueue;
    private int size = 0;
    private String name;
    MessageThread messageThread;
    private ServerImpl server;
    ArrayList<String> subscriberList;
    Message relatedMessage;

    public MessageChannel(ServerImpl server, Message relatedMessage, ArrayList<String> subscriberList, String name){
        this.server = server;
        this.name = name;
        messageQueue = new LinkedList<>();
        this.subscriberList = subscriberList;
        this.relatedMessage = relatedMessage;
    }

    public synchronized int produce(Message message){
        if(size == 5){
            return 1;
        }
        else{
            messageQueue.add(message);
            size++;
            return 0;
        }
    }

    public synchronized Message consume(){
        if(messageQueue.peek() == null)
            return null;
        else
            return messageQueue.poll();
    }

    public void startReceivingMessage(){
        messageThread = new MessageThread(server, this);
        messageThread.start();
    }



}
