class WaitingRoom {
    private Patient[] totalpatients;
    private int capacity;
    private int inptr = 0;
    private int outptr = 0;
    private int count = 0;

    private Semaphore emptyspaces;
    private Semaphore patients;
    private Semaphore mutex;

    public WaitingRoom(int capacity) {
        this.capacity = capacity;
        this.totalpatients = new Patient[capacity];

        this.emptyspaces = new Semaphore(capacity);
        this.patients = new Semaphore(0);
        this.mutex = new Semaphore(1);
    }

    public void addPatient(Patient p) {
        try {
            mutex.acquire();
            if (count == capacity) {
                System.out.println("The waiting room is full!");
                System.out.println(p.getname() + " is waiting outside for an available seat.");
            }
            mutex.release();

            emptyspaces.acquire();

            mutex.acquire();
            totalpatients[inptr] = p;
            inptr = (inptr + 1) % capacity;
            count++;
            System.out.println(p.getname() + " entered the waiting room");
            mutex.release();

            patients.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("An error occured while " + p.getname() + " was trying to enter the waiting room");
        }
    }

    public Patient getPatientt() {
        try {
            patients.acquire();

            mutex.acquire();
            Patient p = totalpatients[outptr];
            outptr = (outptr + 1) % capacity;
            count--;
            System.out.println("The doctor is getting " + p.getname());
            mutex.release();

            emptyspaces.release();
            return p;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("An error occured while a doctor was trying to get a patient");
            return null;
        }
    }

    public void returnPatient(Patient p) {
        try {
            emptyspaces.acquire();

            mutex.acquire();
            outptr = (outptr - 1 + capacity) % capacity;
            totalpatients[outptr] = p;
            count++;
            System.out.println("The doctor is returning " + p.getname());
            mutex.release();

            patients.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("An error occured while a doctor was trying to return a patient");
        }
    }
}