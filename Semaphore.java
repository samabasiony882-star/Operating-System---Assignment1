public class Semaphore {
    private int permits;

    public Semaphore(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Initial count cannot be negative");
        }
        this.permits = count;
    }
    

    public synchronized void release() {
        this.permits++;
        notifyAll();
    }

    public synchronized void acquire() throws InterruptedException {
        while (this.permits == 0) {
            wait();
        }
        this.permits--;
    }

    public synchronized boolean tryAcquire(long maxWaitTime) throws InterruptedException {
        if (this.permits > 0) {
            this.permits--;
            return true;
        }

        long start = System.currentTimeMillis();
        long remaining = maxWaitTime;

        while (this.permits == 0) {
            if (remaining <= 0) {
                return false;
            }
            
            wait(remaining);
            
            long elapsed = System.currentTimeMillis() - start;
            remaining = maxWaitTime - elapsed;
        }

        this.permits--;
        return true;
    }
}