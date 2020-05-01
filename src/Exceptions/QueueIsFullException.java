package Exceptions;

public class QueueIsFullException extends Exception{
    public QueueIsFullException(String msg){
        super(msg);
    }
}
