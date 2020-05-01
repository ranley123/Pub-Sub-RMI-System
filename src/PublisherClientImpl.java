import Exceptions.DataLossException;
import Exceptions.DuplicateException;
import Exceptions.QueueIsFullException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import static java.lang.Thread.sleep;

public class PublisherClientImpl extends UnicastRemoteObject implements IClient, Serializable {
    public IServer stub;

    protected PublisherClientImpl() throws RemoteException {
    }

    /**
     * creates event and publish it to the server
     * @param fruitItem
     * @throws RemoteException
     */
    public void generatePublishEvent(FruitItem fruitItem) throws RemoteException {
        UUID uuid = UUID.randomUUID();
        Date date= new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        // creates a new event
        Event publishEvent = new Event(uuid, "publish", ts, fruitItem);
        int LIMIT = 3;
        int trial = 0;

        // send event
        while(trial <= LIMIT){
            try{
                stub.addEvent(publishEvent);
            }
            // if exception is thrown, try to resend
            catch (DataLossException | QueueIsFullException e){
                System.err.println(e.getMessage());
                trial++;
                try{
                    sleep(1000);
                } catch (InterruptedException j) {
                    j.printStackTrace();
                }
                continue;
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
            // if duplicate event, then drop
            catch (DuplicateException e){
                System.err.println("Duplicate event, dropped");
                break;
            }
            break;
        }
    }

    public void displayMenu(){
        System.out.println("Menu: ");
        System.out.println("(0) Publish / Update a fruit");
    }

    @Override
    public void notify(Message message) throws RemoteException {
        System.out.println("sender: " + message.senderName + " content: " + message.content);
    }
}
