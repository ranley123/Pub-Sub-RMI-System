import java.rmi.RemoteException;
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
        try{
            while(true){
                message = channel.consume();
                if(message == null)
                    sleep(500);
                else
                    stub.notify(message);
            }
        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        }
    }

}
