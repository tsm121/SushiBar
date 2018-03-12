import java.util.Random;

/**
 * This class implements a customer, which is used for holding data and update the statistics
 *
 */
public class Customer {

    private int ID;

    /**
     *  Creates a new Customer.
     *  Each customer is given a unique ID
     */
    public Customer() {
        // TODO Implement required functionality
        this.ID = SushiBar.customerCounter.getAndInc();
    }

    /**
     * Customer takes an order from the waitress
     * and leaves the SushiBar with leftovers (if exists) when done
     */
    public synchronized void order(){
        // TODO Implement required functionality
        //New customer in SushiBar. Increment number of customers in SushiBar
        SushiBar.servedCustomers.increment();

        //Customer order
        int numOrders = new Random().nextInt(SushiBar.maxOrder) + 1;
        int numServed = new Random().nextInt(numOrders);

        //If customer eats at SushiBar
        if (numServed > 0) {
            doAction("is eating.");
            try {
                //Set delay on customer while eating
                Thread.sleep(SushiBar.customerWait);
            } catch (InterruptedException ignored) { }
        }

        //Register orders
        SushiBar.totalOrders.add(numOrders);
        SushiBar.servedOrders.add(numServed);
        SushiBar.takeawayOrders.add(numOrders - numServed);

        //Show customer order and that he/she left the SushiBar
        //and how many leftovers he/she has
        doAction("did order " + numOrders + " meal(s).");
        doAction("left the SushiBar with " + (numOrders - numServed) + " leftover(s).");

        //Decrement number of customers in SushiBar
        SushiBar.currentCustomers.decrement();
    }

    //

    /***
     * Action for the customer with a given type
     * @param type: what kind of action the customer do
     */
    private void doAction(String type) {
        SushiBar.write(this + " " + type);
    }

    /**
     * @return return the customerID
     */
    public int getCustomerID() {
        // TODO Implement required functionality
        return this.ID;
    }

    /**
     * toString with the customer ID
     * @return String with the customer ID
     */
    @Override
    public String toString() {
        //Format thread name for printing
        String threadName = Thread.currentThread().getName();
        threadName = threadName.substring(threadName.lastIndexOf("-") + 1);
        return "Customer " + this.getCustomerID();
    }
}
