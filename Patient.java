public class Patient extends Thread {
    private final WaitingRoom waitingRoom;

    public Patient(int id, WaitingRoom waitingRoom) {
        super("P" + id);
        this.waitingRoom = waitingRoom;
    }

    @Override
    public void run() {
        System.out.println(getName() + " arrived.\n");
        waitingRoom.addPatient(this);
    }

    public void leave() {
        System.out.println(getName() + " left.\n");
    }

    @Override
    public String toString() {
        return getName();
    }
}