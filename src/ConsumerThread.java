import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ConsumerThread extends Thread {
    private EventChannel channel;
    private IServer stub; //  the stub of the server

    public ConsumerThread(EventChannel channel){
        this.channel = channel;
        try{
            Registry registry = LocateRegistry.getRegistry(null, 1888);
            stub = (IServer) registry.lookup("server");
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * if there is no event in the channel, sleep for a while to wait
     * otherwise, processes the event
     */
    public void run(){
        Event event;
        System.out.println("consumer starts");
        try{
            while(true){
                event = channel.consume();
                if(event == null) // sleep to wait for further incoming events
                    sleep(500);
                else // consumes the current event
                    stub.publish(event);
            }
        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
