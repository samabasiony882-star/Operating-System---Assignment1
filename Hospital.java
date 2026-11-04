import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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


        if (waitingRoomCapacity <= 0 ||
            numberOfDoctors <= 0 ||
            numberOfPatients <= 0) {

            System.out.println("All values must be greater than zero.");
            input.close();
            return;
        }


        WaitingRoom waitingRoom =
                new WaitingRoom(waitingRoomCapacity);


        TreatmentRooms treatmentRooms =
                new TreatmentRooms();


        AtomicInteger treatedPatients =
                new AtomicInteger(0);

        Object completionMonitor = new Object();

        Patient[] patients =
                new Patient[numberOfPatients];

        Thread[] doctorThreads =
                new Thread[numberOfDoctors];

        Thread[] patientThreads =
                new Thread[numberOfPatients];


        Random random = new Random();

        System.out.println("\nPatients arriving:");

        for (int i = 0; i < numberOfPatients; i++) {
            patients[i] = new Patient(i + 1, waitingRoom);
            System.out.print(patients[i].getname() + " ");
        }

        System.out.println("\n");

        for (int i = 0; i < numberOfDoctors; i++) {

            final int doctorId = i + 1;

            Doctor doctor = new Doctor(
                    waitingRoom,
                    treatmentRooms,
                    patient -> {

                        System.out.println(
                                "Doctor " + doctorId +
                                " begins treating " +
                                patient.getname() + "."
                        );

                        int treatmentTime =
                                random.nextInt(3000) + 1000;

                        try {
                            Thread.sleep(treatmentTime);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        System.out.println(
                                "Doctor " + doctorId +
                                " finished treating " +
                                patient.getname() + "."
                        );

                        patient.leave();


                        treatedPatients.incrementAndGet();


                        synchronized (completionMonitor) {
                            completionMonitor.notifyAll();
                        }
                    }
            );

            doctorThreads[i] = new Thread(
                    doctor,
                    "Doctor-" + doctorId
            );

            doctorThreads[i].start();
        }

        for (int i = 0; i < numberOfPatients; i++) {

            patientThreads[i] = patients[i];

            patientThreads[i].start();
        }

        synchronized (completionMonitor) {

            while (treatedPatients.get() < numberOfPatients) {

                try {
                    completionMonitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    input.close();
                    return;
                }
            }
        }

        for (int i = 0; i < numberOfPatients; i++) {

            try {
                patientThreads[i].join();
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
