import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.Scanner;

public class SubscriberClient extends SubscriberClientImpl{

    public SubscriberClient() throws RemoteException {
    }

    public static void main(String[] args) { // args: port, name
        if(args.length != 2){
            System.out.println("Usage: java SubscriberClient [port] [name]");
            return;
        }
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
                // check user input
                String input = scanner.next();
                int operation = 0;
                try{
                    operation = Integer.parseInt(input);
                }
                catch(NumberFormatException e){
                    System.err.println("Please enter a number");
                    continue;
                }

                // act based on user input
                switch (operation){
                    case 0: // subscribe
                        System.out.println("Enter the fruit name you want to subscribe: ");
                        input = scanner.next();
                        subscriber.subscribe(input);
                        System.out.println("Successfully subscribed");
                        break;
                    case 1: // unsubscribe
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
        }
        catch(ExportException e){
            System.err.println("Port number " + args[0] + " is used or not valid");
            System.err.println("Usage: java SubscriberClient [port] [name]");
        }
        catch (AccessException e) {
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
