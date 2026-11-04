import java.util.Scanner;

public class Hospital {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        int waitingRoomCapacity;
        int numberOfDoctors;
        int numberOfPatients;

        System.out.print("Waiting room capacity: ");
        waitingRoomCapacity = input.nextInt();

        System.out.print("Number of doctors: ");
        numberOfDoctors = input.nextInt();

        System.out.print("Number of patients: ");
        numberOfPatients = input.nextInt();

        if (waitingRoomCapacity <= 0 || numberOfDoctors <= 0 || numberOfPatients <= 0) {
            System.out.println("All values must be greater than zero.");
            input.close();
            return;
        }

        WaitingRoom waitingRoom = new WaitingRoom(waitingRoomCapacity);
        TreatmentRooms treatmentRooms = new TreatmentRooms();
        Semaphore treatedPatients = new Semaphore(0);

        Thread[] doctorThreads = new Thread[numberOfDoctors];

        for (int i = 0; i < numberOfDoctors; i++) {
            doctorThreads[i] = new Thread(
                    new Doctor(i + 1, waitingRoom, treatmentRooms, treatedPatients)
            );
            doctorThreads[i].start();
        }

        Patient[] patients = new Patient[numberOfPatients];

        System.out.println("\nPatients arriving:");

        for (int i = 0; i < numberOfPatients; i++) {
            patients[i] = new Patient(i + 1, waitingRoom);
            System.out.print(patients[i].getName() + " ");
        }

        System.out.println("\n");

        for (int i = 0; i < numberOfPatients; i++) {
            patients[i].start();
        }

        for (int i = 0; i < numberOfPatients; i++) {
            try {
                patients[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                input.close();
                return;
            }
        }

        for (int i = 0; i < numberOfPatients; i++) {
            try {
                treatedPatients.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                input.close();
                return;
            }
        }

        for (int i = 0; i < numberOfDoctors; i++) {
            doctorThreads[i].interrupt();
        }

        for (int i = 0; i < numberOfDoctors; i++) {
            try {
                doctorThreads[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                input.close();
                return;
            }
        }

        System.out.println("\nAll patients have been treated.");
        System.out.println("Simulation completed.");

        input.close();
    }
}
