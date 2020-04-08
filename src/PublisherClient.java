import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class PublisherClient extends PublisherClientImpl{
    static int port = 1888;
    protected PublisherClient() throws RemoteException {
    }

    public static void main(String[] args) {
        PublisherClientImpl publisher = null;
        boolean exit = false;


        while(exit == false){
            try {
                publisher = new PublisherClientImpl();
                Registry registry = LocateRegistry.getRegistry(null, port);
                publisher.stub = (IServer) registry.lookup("server");

                Scanner scanner = new Scanner(System.in);

                do{
                    publisher.displayMenu();
                    System.out.println("Please enter a number");

                    int operation = scanner.nextInt();
                    FruitItem fruitItem;
                    switch (operation) {
                        case 0:
                            System.out.println("Enter Fruit Name: ");
                            String fruitName = scanner.next();
                            System.out.println("Enter Fruit Price: ");
                            double price = scanner.nextDouble();

                            fruitItem = new FruitItem(fruitName, price);
                            publisher.generatePublishEvent(fruitItem);

                            break;
                        default:
                            System.out.println("Exit");
                    }

                }while(exit == false);
                scanner.close();
            } catch (RemoteException | NotBoundException e) {
//            e.printStackTrace();
                int status = connection_handler(publisher);
                if(status == 0){
                    continue;
                }
                else{
                    System.err.println("Cannot reconnect to the server");
                    break;
                }
            }
        }
    }

    public static int connection_handler(PublisherClientImpl publisher){
        int limit = 1;
        while(limit <= 3){
            System.out.println("reconnecting...");
//            System.out.println("limit: " + limit);
            try{
                publisher = new PublisherClientImpl();
                port = 1888;
                Registry registry = LocateRegistry.getRegistry(null, port);
                publisher.stub = (IServer) registry.lookup("server");
                return 0;
            }
            catch(RemoteException | NotBoundException e){
                limit++;
                continue;
            }
        }
        return 1;
    }
}
