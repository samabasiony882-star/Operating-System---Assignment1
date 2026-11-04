import java.util.Objects;
import java.util.function.Consumer;

public class Doctor implements Runnable {
	private final WaitingRoom waitingRoom;
	private final TreatmentRooms treatmentRooms;
	private final Consumer<Patient> treatment;

	public Doctor(WaitingRoom waitingRoom, TreatmentRooms treatmentRooms,
			Consumer<Patient> treatment) {
		this.waitingRoom = Objects.requireNonNull(waitingRoom);
		this.treatmentRooms = Objects.requireNonNull(treatmentRooms);
		this.treatment = Objects.requireNonNull(treatment);
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Patient patient = waitingRoom.getPatientt();
			if (patient == null) {
				return;
			}

			boolean roomReserved = false;
			try {
				roomReserved = treatmentRooms.tryAcquire();
				if (!roomReserved) {
					waitingRoom.returnPatient(patient);
					continue;
				}

				treatment.accept(patient);
			} catch (InterruptedException e) {
				waitingRoom.returnPatient(patient);
				Thread.currentThread().interrupt();
				return;
			} finally {
				if (roomReserved) {
					treatmentRooms.release();
				}
			}
		}
	}
}
