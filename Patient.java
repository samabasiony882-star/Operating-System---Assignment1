public class Patient extends Thread {
    private final int id;
    private final WaitingRoom waitingRoom;

    public Patient(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
    }

    public String getname() {
        return "P" + id;
    }

    @Override
    public void run() {
        System.out.println(getname() + " arrived.");
        waitingRoom.addPatient(this);
    }

    public void leave() {
        System.out.println(getname() + " left.");
    }

    @Override
    public String toString() {
        return getname();
    }
}