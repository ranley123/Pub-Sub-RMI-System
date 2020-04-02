import java.util.LinkedList;
import java.util.Queue;

public class MessageChannel {
    private Queue<Message> messageQueue;
    private int size = 0;
    private String name;
    MessageThread messageThread;
    private ServerImpl server;

    public MessageChannel(ServerImpl server, String name){
        this.server = server;
        this.name = name;
        messageQueue = new LinkedList<>();
        messageThread = new MessageThread(server, this);
        messageThread.start();
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


}
