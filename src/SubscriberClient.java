import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class SubscriberClient extends SubscriberClientImpl{

    public SubscriberClient() throws RemoteException {
    }

    public static void main(String[] args) { // args: port, name
        try{
            Scanner scanner = new Scanner(System.in);
            SubscriberClientImpl subscriber = new SubscriberClientImpl();
            subscriber.setPort(Integer.parseInt(args[0]));
            subscriber.setName(args[1]);

            // look for server stub
            Registry registry = LocateRegistry.getRegistry(null, 1888);
            subscriber.stub = (IServer) registry.lookup("server");

            boolean exit = false;

            // build own client stub
            IClient ownStub = (IClient) subscriber;
            Registry clientRegistry = LocateRegistry.createRegistry(subscriber.port);
            clientRegistry.rebind(subscriber.name, ownStub);
            subscriber.stub.register(subscriber.port, subscriber.name);

            do{
                displayMenu();
                System.out.println("Please enter a number to act");
                int operation = scanner.nextInt();
                switch (operation){
                    case 0:
                        System.out.println("Enter the fruit name you want to subscribe: ");
                        String input = scanner.next();
                        subscriber.subscribe(input);
                        System.out.println("Successfully subscribed");
                        break;
                    case 1:
                        System.out.println("Enter the fruit name you want to unsubscribe: ");
                        input = scanner.next();
                        subscriber.unsubscribe(input);
                        break;
                    default:
                        System.out.println("Invalid operation");
                        break;
                }
                System.out.println();
            }while(exit == false);

            scanner.close();
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }


    }


    private static void displayMenu(){
        System.out.println("Menu");
        System.out.println("(0) Subscribe");
        System.out.println("(1) UnSubscribe");
    }


}
