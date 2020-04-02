import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server extends ServerImpl {
    public Server() throws RemoteException {
    }

    public static void main(String[] args){
        try{
            ServerImpl server = new ServerImpl();
            IServer stub = (IServer) server;
            Registry registry = LocateRegistry.createRegistry(1888);
            registry.rebind("server", stub);
            System.out.println("server ready");

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}