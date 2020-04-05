import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class PublisherClientImpl extends UnicastRemoteObject implements IClient, Serializable {
    public IServer stub;
    int id;

    protected PublisherClientImpl() throws RemoteException {
    }

    public int getId() {
        return id;
    }

    public void generatePublishEvent(FruitItem fruitItem) throws RemoteException {
        UUID uuid = UUID.randomUUID();
        Date date= new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);

        Event publishEvent = new Event(uuid, "publish", ts, fruitItem);
        int limit = 1;

        while(limit <= 3){
            int errcode = stub.addEvent(publishEvent);
            if(errcode == 0){
                break;
            }
            if(errcode == 1){ // queue full
                errorPrinter(errcode);
                limit++;
                try{
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
        }
        System.err.println("queue is full");
    }

    public void update(FruitItem fruitItem){
        Date date= new Date();
        long time = date.getTime();
        UUID uuid = UUID.randomUUID();
        Timestamp ts = new Timestamp(time);
        Event updateEvent = new Event(uuid, "update", ts, fruitItem);
//        stub.update(updateEvent);
    }

    public void displayMenu(){
        System.out.println("Menu: ");
        System.out.println("(0) Publish a fruit");
        System.out.println("(1) Update a fruit");
    }

    @Override
    public void notify(Message message) throws RemoteException {
        System.out.println("sender: " + message.senderName + " content: " + message.content);
    }



    private void errorPrinter(int errcode){
        switch (errcode){
            case 1:
                System.out.println("too many queries");
                break;
            case 2:
                System.out.println("connection failures");
                break;
            case 3:
                System.out.println("crashing customers");
                break;
            case 4:
                System.out.println("dropped messages");
                break;
        }
    }
}
