import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import static sun.nio.ch.IOStatus.EOF;

public class Main {
    public static final String EOF="EOF";
    public static void main(String[] args) {
        List<String>buffer=new ArrayList<>();
        ReentrantLock bufferLock=new ReentrantLock();
        MyProducer producer=new MyProducer(buffer,Thread_Color.ANSI_YELLOW,bufferLock);
        MyConsumer consumer1 = new MyConsumer(buffer,Thread_Color.ANSI_PURPLE,bufferLock);
        MyConsumer consumer2 = new MyConsumer(buffer,Thread_Color.ANSI_CYAN,bufferLock);
        new Thread(producer).start();
        new Thread(consumer1).start();
        new Thread(consumer2).start();
        //System.out.println("Hello world!");
    }
}
class MyProducer implements Runnable{
    private List<String>buffer;
    private String color;
    private ReentrantLock bufferLock;
    MyProducer(List<String>buffer,String color,ReentrantLock bufferLock){
        this.buffer=buffer;
        this.color=color;
        this.bufferLock=bufferLock;
    }
    @Override
    public void run() {
        Random rnd=new Random();
        String[]str={"1","2","3","4","5","6","7"};
        for(String nums:str){
            try{
                System.out.println(color + "Adding.." + nums);
                bufferLock.lock();
                buffer.add(color);
                bufferLock.unlock();

                Thread.sleep(rnd.nextInt(1000));
            }catch(InterruptedException e){
                System.out.println("Producer was Interrupted");
            }
        }
        System.out.println(color + "Adding EOF and exiting");
        bufferLock.lock();
        buffer.add("EOF");
        bufferLock.unlock();
    }
}
class MyConsumer implements Runnable{
    private List<String>buffer;
    private String color;
    private ReentrantLock bufferLock;
    MyConsumer(List<String>buffer,String color,ReentrantLock bufferLock){
        this.buffer=buffer;
        this.color=color;
        this.bufferLock=bufferLock;
    }
    @Override
    public void run() {
        while(true){
            bufferLock.lock();
            if(buffer.isEmpty()){
                bufferLock.unlock();
                continue;
            }
            if(buffer.get(0).equals(EOF)){
                System.out.println(color + "Exiting");
                bufferLock.unlock();
                break;
            }
            else{
                System.out.println(color + "Color is Removed"+buffer.remove(0));
            }
            bufferLock.unlock();
        }
    }
}