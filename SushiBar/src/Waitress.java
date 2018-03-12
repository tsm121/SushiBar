/**
 * This class implements the consumer part of the producer/consumer problem.
 * One waitress instance corresponds to one consumer.
 */
public class Waitress implements Runnable {

    private WaitingArea waitingArea;

    /**
     * @param waitingArea The waiting area for customers
     */
    Waitress(WaitingArea waitingArea) {
        this.waitingArea = waitingArea;
    }

    /**
     * Consumer function for the waitress
     * Fetches customer from the waiting area and takes the order
     */
    @Override
    public void run() {
        // Serve customers as long as SushiBar is open
        // or there are customers in the waiting area.
        while (SushiBar.isOpen || SushiBar.currentCustomers.get() > 0) {
            Customer currentCustomer;

            synchronized (waitingArea) {
                //Get next customer
                currentCustomer = waitingArea.next();

                //Get customer from waiting area
                // if there are customers in the waiting area
                if (currentCustomer != null) {

                    //Check if SushiBar is closed and the waiting area is empty
                    if (!SushiBar.isOpen && !waitingArea.checkIfFull()){
                        //Notify the rest of the waitresses so that those who wait can terminate
                        waitingArea.notifyAll();
                        SushiBar.write("Last customer for the day.");
                    }
                    String waitressName = this.toString().substring(0).split("@")[0];
                    SushiBar.write(waitressName + " fetched customer "
                            + currentCustomer.getCustomerID());
                }
                // Wait if there are no customers in waiting area
                else {
                    try {
                        waitingArea.wait();
                    } catch (InterruptedException ignored) { }
                }
            }

            //Waitress waits before taking the customers order
            if (currentCustomer != null) {
                try {
                    Thread.sleep(SushiBar.waitressWait);
                } catch (InterruptedException ignored) { }

                //Take customer order
                currentCustomer.order();


            }
        }
    }
}
