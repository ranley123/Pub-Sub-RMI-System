import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MessageChannel {
    private int LIMIT;
    private String name;
    MessageThread messageThread;
    private ServerImpl server;
    ArrayList<String> subscriberList; // a list of subscribers who should send response messages
    ArrayList<Queue<Message>> queues;

    // related message for subscribers (such as subscribing message about fruits)
    // if there is a crashing subscriber, relatedMessage can be used to re-send to it
    Message relatedMessage; // for handling crashing customers

    public MessageChannel(ServerImpl server, Message relatedMessage, ArrayList<String> subscriberList, String name){
        this.server = server;
        this.name = name;
        this.subscriberList = subscriberList;
        this.relatedMessage = relatedMessage;

        queues = new ArrayList<>();
        LIMIT = subscriberList.size() + 1;
    }

    /**
     * adds message into the queue
     * @param message
     * @return
     */
    public synchronized int produce(Message message){
        if(queues.size() == 0){
            Queue<Message> newQueue = new LinkedList<>();
            queues.add(newQueue);
        }
        Queue<Message> curQueue = queues.get(queues.size() - 1);
        curQueue.add(message);
        return 0;
    }

    /**
     * gets the first message
     * @return
     */
    public synchronized Message consume(){
        if(queues.size() == 0){
            return null;
        }
        Queue<Message> curQueue = queues.get(0);

        if(curQueue.peek() == null){
            queues.remove(curQueue);
            return null;
        }
        else
            return curQueue.poll();
    }

    /**
     * starts the consuming thread to process messages
     */
    public void startReceivingMessage(){
        messageThread = new MessageThread(server, this);
        messageThread.start();
    }
}
