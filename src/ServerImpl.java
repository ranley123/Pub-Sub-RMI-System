import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends Server {
    public ServerImpl() throws RemoteException {
    }

    public static void main(String[] args){
        try{
            Server server = new Server();
            IServer stub = (IServer) server;
            Registry registry = LocateRegistry.createRegistry(1888);
            registry.rebind("server", stub);
            System.out.println("server ready");



        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
