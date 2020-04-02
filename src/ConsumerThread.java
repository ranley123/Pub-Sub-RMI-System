import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ConsumerThread extends Thread {
    private EventChannel channel;
    private IServer stub;

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

    public void run(){
        Event event;
        System.out.println("consumer starts");
        try{
            while(true){
                event = channel.consume();
//                if(event == null)
//                    System.out.println("event: null");
//                else
//                    System.out.println("event: " + event.toString());
                if(event == null)
                    sleep(500);
                else
                    stub.publish(event);
            }
        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
