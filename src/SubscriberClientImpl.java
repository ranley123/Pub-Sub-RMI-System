import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SubscriberClientImpl extends UnicastRemoteObject implements IClient, Serializable {
    String name;
    IServer stub;
    int port;


    @Override
    public void notify(Message message) throws RemoteException {
        System.out.println("sender: " + message.senderName + " content: " + message.content);
        Message response;
        if(message.type.equals("subscribe")){
//            int index = message.content.indexOf(":");
            response = new Message("response", "apple" + " received", 0, this.name);
            response.setMessageChannelId(message.messageChannelId);
            stub.addMessage(response);
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

    public void subscribe(String fruitName){
        try{
            stub.subscribe(fruitName, name);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(String fruitName){
        try{
            stub.unsubscribe(fruitName, name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
