import Exceptions.DataLossException;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.Thread.sleep;

public class SubscriberClientImpl extends UnicastRemoteObject implements IClient, Serializable {
    String name;
    IServer stub;
    int port;


    /**
     * when the server sends a message to subscribers about the updated fruit, it should send a response
     * @param message - subscription message
     * @throws RemoteException
     */
    @Override
    public void notify(Message message) throws RemoteException {
        System.out.println("sender: " + message.senderName + "\ncontent: " + message.content);

        Message response; // respond server that I ve received
        if(message.type.equals("subscribe")){
            response = new Message("response", "apple" + " received", this.name);
            response.setMessageChannelId(message.messageChannelId);

            // try to deliver at most 3 times. If connected then only once, if not then 3.
            int limit = 1;
            while(limit <= 3){
                try{
                    stub.addMessage(response);
                    break;
                } catch (DataLossException e) {
                    System.out.println("data loss");
                    e.getMessage();
                    limit++;
                    try{
                        sleep(1000000);
                    } catch (InterruptedException j) {
                        j.printStackTrace();
                    }
                    continue;
                }
            }
        }
    }

    public SubscriberClientImpl() throws RemoteException{
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPort(int port){
        this.port = port;
    }

    /**
     * to subscribe
     * @param fruitName
     */
    public void subscribe(String fruitName){
        try{
            stub.subscribe(fruitName, name);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * to unsubscribe
     * @param fruitName
     */
    public void unsubscribe(String fruitName){
        try{
            stub.unsubscribe(fruitName, name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
