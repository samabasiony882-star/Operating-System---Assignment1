import java.util.Random;

public class Doctor implements Runnable {
	private final int id;
	private final WaitingRoom waitingRoom;
	private final TreatmentRooms treatmentRooms;
	private final Semaphore treated;
	private final Random random = new Random();

	public Doctor(int id, WaitingRoom waitingRoom, TreatmentRooms treatmentRooms,
			Semaphore treated) {
		this.id = id;
		this.waitingRoom = waitingRoom;
		this.treatmentRooms = treatmentRooms;
		this.treated = treated;
	}

	private String name() {
		return "Doctor " + id;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Patient patient = waitingRoom.getPatientt();
			if (patient == null) {
				return;
			}

			System.out.println(name() + " is assigned to " + patient.getName() + ".");
			boolean roomReserved = false;
			try {
				System.out.println(name() + " is trying to acquire a treatment room.");
				roomReserved = treatmentRooms.tryAcquire();
				if (!roomReserved) {
					System.out.println(name() + " could not acquire a treatment room.");
					System.out.println(name() + " is sending " + patient.getName() + " back to the waiting room.");
					waitingRoom.returnPatient(patient);
					System.out.println(name() + " is now available.");
					continue;
				}
				System.out.println(name() + " acquired a treatment room.");
				System.out.println(name() + " begins treating " + patient.getName() + ".");

				Thread.sleep(1000 + random.nextInt(2000));
				System.out.println(name() + " finished treating " + patient.getName() + ".");

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			} finally {
				if (roomReserved) {
					treatmentRooms.release();
					System.out.println(name() + " released the treatment room.");
				}
			}
			System.out.println(name() + " is now available.");
			patient.leave();
			treated.release();
		}
	}
}
