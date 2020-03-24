import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class SubscriberClient implements IClient, Serializable {
    String name;
    IServer stub;

    public static void main(String[] args) {
        try{
            Scanner scanner = new Scanner(System.in);
            SubscriberClient subscriber = new SubscriberClient();
            Registry registry = LocateRegistry.getRegistry(null, 1888);
            subscriber.stub = (IServer) registry.lookup("server");
            System.out.println("Subscriber Registered");
            boolean exit = false;

            do{
                subscriber.displayMenu();
                System.out.println("Please enter a number to act");
                int operation = scanner.nextInt();
                switch (operation){
                    case 0:
                        System.out.println("Enter the fruit name you want to subscribe: ");
                        String input = "apple";
                        subscriber.subscribe(input);
                        System.out.println("Successfully subscribed");
                        break;
                    case 1:
                        System.out.println("Enter the fruit name you want to unsubscribe: ");
                        input = scanner.nextLine();
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

    @Override
    public void notify(String message){
        System.err.println("message: " + message);
    }

    public int getId() {
        return 0;
    }

    public void subscribe(String fruitName){
        try{
            stub.subscribe(fruitName, this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unsubscribe(String fruitName){
        try{
            stub.unsubscribe(fruitName, this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getName(){
        return name;
    }

    private void displayMenu(){
        System.out.println("Menu");
        System.out.println("(0) Subscribe");
        System.out.println("(1) UnSubscribe");
    }
}
