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
            try {
                if (count == capacity) {
                    System.out.println("The waiting room is full!\n");
                    System.out.println(p.getName() + " is waiting outside for an available seat.\n");
                }
            } finally {
                mutex.release();
            }

            emptyspaces.acquire();

            mutex.acquire();
            try {
                totalpatients[inptr] = p;
                inptr = (inptr + 1) % capacity;
                count++;
                System.out.println(p.getName() + " entered the waiting room.\n");
            } finally {
                mutex.release();
            }

            patients.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Patient getPatient() {
        try {
            patients.acquire();

            Patient p;
            mutex.acquire();
            try {
                p = totalpatients[outptr];
                outptr = (outptr + 1) % capacity;
                count--;
            } finally {
                mutex.release();
            }

            emptyspaces.release();
            return p;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public void returnPatient(Patient p) {
        try {
            mutex.acquire();
            try {
                if (count == capacity) {
                    System.out.println("The waiting room is full!\n");
                    System.out.println(p.getName() + " is waiting outside for an available seat.\n");
                }
            } finally {
                mutex.release();
            }

            emptyspaces.acquire();

            mutex.acquire();
            try {
                totalpatients[inptr] = p;
                inptr = (inptr + 1) % capacity;
                count++;
                System.out.println(p.getName() + " entered the waiting room.\n");
            } finally {
                mutex.release();
            }

            patients.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}