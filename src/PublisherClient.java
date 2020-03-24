import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;

public class PublisherClient extends UnicastRemoteObject implements IClient{
    private IServer stub;
    int id;

    protected PublisherClient(int id) throws RemoteException {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private void generatePublishEvent(FruitItem fruitItem) throws RemoteException {
        Date date= new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        Event publishEvent = new Event("publish", ts, this.id, fruitItem);
        stub.addEvent(publishEvent);
    }

    private void update(FruitItem fruitItem){
        Date date= new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        Event updateEvent = new Event("update", ts, this.id, fruitItem);
//        stub.update(updateEvent);
    }

    private void displayMenu(){
        System.out.println("Menu: ");
        System.out.println("(0) Publish a fruit");
        System.out.println("(1) Update a fruit");
    }

    @Override
    public void notify(String message) throws RemoteException {
        System.out.println("message: " + message);
    }

    public static void main(String[] args) {
        try {
            PublisherClient publisher = new PublisherClient(0);
            Registry registry = LocateRegistry.getRegistry(null, 1888);
            publisher.stub = (IServer) registry.lookup("server");
            System.out.println("Registered");
            Scanner scanner = new Scanner(System.in);

            boolean exit = false;

            do{
                publisher.displayMenu();
                System.out.println("Please enter a number to act");

                int operation = scanner.nextInt();
                FruitItem fruitItem;
                switch (operation) {
                    case 0:
//                    System.out.println("Enter Fruit Name: ");
//                    String fruitName = scanner.nextLine();
//                    System.out.println("Enter Fruit Price: ");
//                    double price = scanner.nextDouble();

                        fruitItem = new FruitItem("apple", 0.99);
                        publisher.generatePublishEvent(fruitItem);
                        System.out.println("published");

                        break;
                    case 1:
                        fruitItem = new FruitItem("apple", 1.99);
                        publisher.update(fruitItem);
                        System.out.println("updated");
                        break;
                    default:
                        System.out.println("Exit");
                }

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
}
