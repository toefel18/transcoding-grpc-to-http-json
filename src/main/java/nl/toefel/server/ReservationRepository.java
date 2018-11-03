package nl.toefel.server;

import nl.toefel.reservations.Reservation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ReservationRepository {

    private Map<String, Reservation> reservationById = new HashMap<>();

    public Optional<Reservation> findReservation(String id) {
        return Optional.ofNullable(reservationById.get(id));
    }

    /**
     * @return a reservation instance with ID
     */
    public Reservation createReservation(Reservation reservation) {
        String id = UUID.randomUUID().toString();
        Reservation reservationWithId = reservation.toBuilder()
                .setId(id)
                .build();

        reservationById.put(id, reservationWithId);

        return reservationWithId;
    }

    public void updateReservation(Reservation reservation) {
        reservationById.put(reservation.getId(), reservation);
    }

    public void deleteReservation(String id) {
        reservationById.remove(id);
    }
}
