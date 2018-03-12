import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


public class SushiBar {

    //SushiBar settings
    private static int waitingAreaCapacity = 10;
    private static int waitressCount = 10;
    private static int duration = 3;
    public static int maxOrder = 8;
    public static int waitressWait = 100; // Used to calculate the time the waitress spends before taking the order
    public static int customerWait = 1000; // Used to calculate the time the customer uses eating
    public static int doorWait = 50; // Used to calculate the interval at which the door tries to create a customer
    public static boolean isOpen = true;

    //Creating log file
    private static File log;
    private static String path = "./";

    //Variables related to statistics
    public static SynchronizedInteger customerCounter;
    public static SynchronizedInteger servedOrders;
    public static SynchronizedInteger takeawayOrders;
    public static SynchronizedInteger totalOrders;

    public static SynchronizedInteger servedCustomers;
    public static SynchronizedInteger currentCustomers;

    /**
     * Initializes the SushiBar and start the different threads
     * Writes statistics when all threads have completed
     * @param args
     */
    public static void main(String[] args) {
        log = new File(path + "log.txt");

        //Initializing shared variables for counting number of orders
        customerCounter = new SynchronizedInteger(1);
        totalOrders = new SynchronizedInteger(0);
        servedOrders = new SynchronizedInteger(0);
        takeawayOrders = new SynchronizedInteger(0);

        servedCustomers = new SynchronizedInteger(0);
        currentCustomers = new SynchronizedInteger( 0);

        //Generate new waiting area
        WaitingArea waitingArea = new WaitingArea(waitingAreaCapacity);
        //Make number of threads as there are waitress
        ExecutorService waitressStaff = Executors.newFixedThreadPool(waitressCount);
        //Assign each thread (waitress) to a waitingArea
        IntStream.range(0, waitressCount).forEach(
                i -> waitressStaff.submit(new Waitress(waitingArea)));

        //Generate a new Door and make it a thread. Start thread
        Door door = new Door(waitingArea);
        Thread doorThread = new Thread(door);
        doorThread.start();

        //Start Clock (SushiBar opening hours).
        new Clock(duration);
        try {
            //Make Door the last thread that dies
            doorThread.join();

        } catch (InterruptedException ignored){ }

        //Shutdown consumers (waitresses)
        waitressStaff.shutdown();

        while (!waitressStaff.isTerminated()){
            //Wait for every thread has stopped executing
        }

        //Write statistics
        write("\n **** Statistics *****\n"
                + (SushiBar.customerCounter.get() - 1) + " number of customers at the SushiBar.\n"
                + (SushiBar.servedCustomers.get() - 1) + " number of customers that got served.\n"
        );

        write(SushiBar.totalOrders.get() + " number of orders completed.\n"
                        + SushiBar.takeawayOrders.get() + " number of takeaway.\n"
                        + SushiBar.servedOrders.get() + " number of orders served."
        );

    }

    /**
     * Writes actions in the log file and console
     * @param str action that should be written
     */

    public static void write(String str) {
        try {
            FileWriter fw = new FileWriter(log.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(Clock.getTime() + ", " + str + "\n");
            bw.close();
            System.out.println(Clock.getTime() + ", " + str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
