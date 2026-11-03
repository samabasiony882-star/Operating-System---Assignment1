public class TreatmentRooms {
    private final Semaphore rooms = new Semaphore(2);   
    private static final long TIMEOUT_MS = 3000;        

    public boolean tryAcquire() throws InterruptedException {
        return rooms.tryAcquire(TIMEOUT_MS);
    }

    public void release() {
        rooms.release();
    }
}