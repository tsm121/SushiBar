import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class implements a waiting area used as the bounded buffer, in the producer/consumer problem.
 */
public class WaitingArea {

    private Queue<Customer> customers;
    private int roomSize;

    /**
     * Creates a new waiting area.
     * areaSize decides how many people can be waiting at the same time (how large the shared buffer is)
     * @param size The maximum number of Customers that can be waiting.
     */
    public WaitingArea(int size) {
        //Set room size and generate a queue for customers at that size
        this.roomSize = size;
        this.customers = new LinkedBlockingQueue<Customer>(size);
    }

    /**
     * Check if SushiBar is full or not
     * @return status of room
     */
    public boolean checkIfFull() {
        if (this.customers.size() == this.roomSize){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Puts the customer into the waitingArea
     * @param customer A customer created by Door, trying to enter the waiting area
     */
    public synchronized void enter(Customer customer) {
        this.customers.add(customer);
        SushiBar.currentCustomers.increment();
        this.notify();
    }

    /**
     * @return The customer that is first in line.
     */
    public synchronized Customer next() {
        //Add a new customer to the waiting area if there are customers waiting
        Customer nextCustomer = this.customers.poll();
        if (nextCustomer != null) {
            SushiBar.currentCustomers.decrement();
        }
        //Notify all threads
        this.notifyAll();
        return nextCustomer;
    }
}
