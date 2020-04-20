import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MessageChannel {
    private Queue<Message> messageQueue;
    private int size = 0; // the current size of queue
    private String name;
    MessageThread messageThread;
    private ServerImpl server;
    ArrayList<String> subscriberList; // a list of subscribers who should send response messages

    // related message for subscribers (such as subscribing message about fruits)
    // if there is a crashing subscriber, relatedMessage can be used to re-send to it
    Message relatedMessage; // for handling crashing customers

    public MessageChannel(ServerImpl server, Message relatedMessage, ArrayList<String> subscriberList, String name){
        this.server = server;
        this.name = name;
        messageQueue = new LinkedList<>();
        this.subscriberList = subscriberList;
        this.relatedMessage = relatedMessage;
    }

    /**
     * adds message into the queue
     * @param message
     * @return
     */
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

    /**
     * gets the first message
     * @return
     */
    public synchronized Message consume(){
        if(messageQueue.peek() == null)
            return null;
        else
            return messageQueue.poll();
    }

    /**
     * starts the consuming thread to process messages
     */
    public void startReceivingMessage(){
        messageThread = new MessageThread(server, this);
        messageThread.start();
    }
}
