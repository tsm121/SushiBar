import java.util.Random;

/**
 * This class implements the Door component of the sushi bar assignment
 * The Door corresponds to the Producer in the producer/consumer problem
 */
public class Door implements Runnable {

    private WaitingArea waitingArea;

    /**
     * Creates a new Door. Adding waiting area to the door
     * @param waitingArea   The customer queue waiting for a seat
     */
    public Door(WaitingArea waitingArea) {
        // TODO Implement required functionality
        this.waitingArea = waitingArea;
    }

    /**
     * Producer function for the door
     * Produce a new customer at a random interval and add them to the SushiBar queue. This runs when the door thread is started
     */
    @Override
    public void run() {
        //As long as SushiBar is open
        // and the waiting area is not full generate a new customer
        while (SushiBar.isOpen) {
            synchronized (waitingArea) {
                try {
                    if (!waitingArea.checkIfFull()){
                        //Generate a new customer
                        Customer newCustomer = new Customer();
                        //Add customer to waiting area
                        waitingArea.enter(newCustomer);

                        //Show that customer has entered the waiting area
                        SushiBar.write(newCustomer + " entered the waiting area!");
                        //Wait before generating a new customer
                        Thread.sleep( new Random().nextInt(SushiBar.doorWait) + SushiBar.doorWait);

                    }
                } catch (InterruptedException | IllegalStateException ignored) {}

            }
        }
        //Close the SushiBar
        SushiBar.write("***** NO MORE CUSTOMERS - THE SHOP IS NOW CLOSED. *****");
    }

    // Add more methods as you see fit
}
