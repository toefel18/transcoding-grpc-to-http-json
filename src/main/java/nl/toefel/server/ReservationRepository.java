package nl.toefel.server;

import nl.toefel.reservations.v1.Reservation;

import java.util.*;

public class ReservationRepository {

    private Map<String, Reservation> reservationById = new HashMap<>();

    public Optional<Reservation> findReservation(String id) {
        return Optional.ofNullable(reservationById.get(id));
    }

    public Reservation createReservation(Reservation reservation) {
        String id = UUID.randomUUID().toString();
        Reservation reservationWithId = reservation.toBuilder()
                .setId(id)
                .build();

        reservationById.put(id, reservationWithId);

        return reservationWithId;
    }

    public List<Reservation> listReservations() {
        return List.copyOf(reservationById.values());
    }

    public void deleteReservation(String id) {
        reservationById.remove(id);
    }
}
