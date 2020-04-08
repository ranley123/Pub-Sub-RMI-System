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
        System.out.println("message thread starts");
        int flag = 0;
        int limit = 0;
        

        try{
            while(true){
                UUID messageChannelId = null;
                while(true){
                    message = channel.consume();
                    if(message == null){
                        if(flag == 1)
                            break;
                        sleep(5000);
                        flag = 1;
                    }
                    else{
                        flag = 0;
                        messageChannelId = message.messageChannelId;
                        channel.subscriberList.remove(message.senderName);
                        stub.notify(message);

                    }
                }

                if(channel.subscriberList.size() != 0){
                    if(limit == 4){
                        System.out.println("Debug: subscriber not available");
                        break;
                    }
                    limit++;
                    stub.notifyClient(channel.subscriberList, channel.relatedMessage);
                    System.out.println("Debug: subscriber not available");
                    break;
                }
                else{
                    System.out.println("Debug: all subscribers responsed");
                    stub.messageChannelMap.remove(messageChannelId);
                    break;
                }
            }
        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        }
    }



}
