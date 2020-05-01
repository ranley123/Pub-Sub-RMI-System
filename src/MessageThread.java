import java.rmi.RemoteException;
import java.util.UUID;

public class MessageThread extends Thread{
    private MessageChannel channel;
    private ServerImpl stub;

    public MessageThread(ServerImpl server, MessageChannel channel){
        this.channel = channel;
        this.stub = server;
    }

    public void run(){
        Message message;
//        System.out.println("message thread starts");
        int flag = 0;
        int LIMIT = 3;
        int trial = 0;

        try{
            while(true){
                UUID messageChannelId = null;
                while(true){
                    message = channel.consume();
                    // if no message available, then waiting for a while
                    if(message == null){
                        // if this time no message, and the last time no message, then maybe customer crashes
                        if(flag == 1)
                            break;
                        sleep(5000);
                        flag = 1;
                    }
                    else{
                        // the subscriber responded, so removes it from the list
                        flag = 0;
                        messageChannelId = message.messageChannelId;
                        channel.subscriberList.remove(message.senderName);
                        stub.notify(message);
                        if(channel.subscriberList.size() == 0)
                            break;
                    }
                }

                if(channel.subscriberList.size() != 0){
                    // if we tried 3 times but there is still subscribers who unresponded
                    if(trial == LIMIT){
                        System.out.println("Debug: subscriber not available: " + channel.subscriberList.toString());
                        break;
                    }
                    trial++;
                    stub.notifyClient(channel.subscriberList, channel.relatedMessage);
                }
                else{
                    System.out.println("Debug: all subscribers responded");
                    stub.messageChannelMap.remove(messageChannelId);
                    // because all subscribers related to this event had responded, the event does not need to be kept
                    // release its UUID for new use
                    stub.UUIDStore.remove(messageChannelId);
                    break;
                }
            }
        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        }
    }



}
