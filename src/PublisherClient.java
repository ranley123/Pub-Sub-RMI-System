import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class PublisherClient extends PublisherClientImpl{
    static int port = 1888;
    protected PublisherClient() throws RemoteException {
    }

    public static void main(String[] args) throws InterruptedException {
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

                    FruitItem fruitItem;
                    String input = scanner.next();
                    int operation = 0;

                    // get user input
                    try{
                        operation = Integer.parseInt(input);
                    }
                    catch(NumberFormatException e){
                        System.err.println("Please enter a number");
                        continue;
                    }

                    switch (operation) {
                        case 0: // put or update a new fruit item
                            System.out.println("Enter Fruit Name: ");
                            String fruitName = scanner.next();

                            System.out.println("Enter Fruit Price: ");
                            double price = 0;
                            input = scanner.next();

                            // check input type
                            try{
                                price = Double.parseDouble(input);
                            }
                            catch(NumberFormatException e){
                                System.err.println("Please enter a number");
                                continue;
                            }

                            // creates an event and publishs it
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

    /**
     * handle connection interruptions and unexpected exceptions
     * @param publisher
     * @return
     * @throws InterruptedException
     */
    public static int connection_handler(PublisherClientImpl publisher) throws InterruptedException {
        int limit = 1;
        // the max trial times: 3
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
            // if reconnecting fails, waits for server recovery and tries again later
            catch(RemoteException | NotBoundException e){
                limit++;
                sleep(5000); // wait for recovery
                continue;
            }
        }
        return 1;
    }
}
