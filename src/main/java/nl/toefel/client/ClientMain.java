package nl.toefel.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import nl.toefel.reservations.v1.*;

import java.util.concurrent.atomic.AtomicInteger;

public class ClientMain {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 53000)
                .usePlaintext() // disable TLS which is enabled by default and requires certificates
                .build();

        System.out.println("--- Getting reservation 123 which does not exist");
        ReservationServiceGrpc.ReservationServiceBlockingStub reservationClient = ReservationServiceGrpc.newBlockingStub(channel);

        try {
            Reservation nonExistingReservation = reservationClient.getReservation(GetReservationRequest.newBuilder()
                    .setId("123")
                    .build());
            System.out.println("response:" + nonExistingReservation);
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus().getDescription() + " " + e.getMessage());
            System.out.println(e);
        }

        String createdReservationId = createReservation(reservationClient, "JDriven Coltbaan 3", "2018-10-10T11:12:13", "meeting-room");

        System.out.println("--- Getting reservation with id " + createdReservationId);
        Reservation existingReservation = reservationClient.getReservation(GetReservationRequest.newBuilder()
                .setId(createdReservationId)
                .build());
        System.out.println("response: " + existingReservation);


//        deleteReservation(reservationClient, createdReservationId);


        System.out.println("--- Getting reservation with id " + createdReservationId);
        Reservation existingReservation2 = reservationClient.getReservation(GetReservationRequest.newBuilder()
                .setId(createdReservationId)
                .build());
        System.out.println("response: " + existingReservation2);


        createReservation(reservationClient, "JDriven Coltbaan 3", "2018-11-10T12:12:13", "meeting-room");
        createReservation(reservationClient, "JDriven Coltbaan 3", "2019-11-12T11:30:13", "meeting-room");
        createReservation(reservationClient, "Vandervalk Hotel", "2019-11-12T11:30:13", "meeting-room");

        AtomicInteger counterWithoutFilters = new AtomicInteger();
        reservationClient.listReservations(ListReservationsRequest.newBuilder().build()).forEachRemaining(it -> counterWithoutFilters.getAndIncrement());
        System.out.println("Received " +counterWithoutFilters.get() + " reservations without filter params");

        AtomicInteger counterWithVenueFilter = new AtomicInteger();
        reservationClient.listReservations(ListReservationsRequest.newBuilder().setVenue("JDriven Coltbaan 3").build()).forEachRemaining(it -> counterWithVenueFilter.getAndIncrement());
        System.out.println("Received " +counterWithVenueFilter.get() + " reservations with venue filter params");

        AtomicInteger counterWithTimestampFilter = new AtomicInteger();
        reservationClient.listReservations(ListReservationsRequest.newBuilder().setTimestamp("2019-11-12T11:30:13").build()).forEachRemaining(it -> counterWithTimestampFilter.getAndIncrement());
        System.out.println("Received " +counterWithTimestampFilter.get() + " reservations with timestamp filter params");
    }

    private static void deleteReservation(ReservationServiceGrpc.ReservationServiceBlockingStub reservationClient, String createdReservationId) {
        System.out.println("--- Deleting reservation with id " + createdReservationId);
        reservationClient.deleteReservation(DeleteReservationRequest.newBuilder()
                .setId(createdReservationId)
                .build());
        System.out.println("response: empty" );
    }

    private static String createReservation(ReservationServiceGrpc.ReservationServiceBlockingStub reservationClient, String venue, String timestamp, String room) {
        System.out.println("--- Creating reservation ");
        Reservation newReservation = Reservation.newBuilder()
                .setTitle("Lunchmeeting")
                .addAttendees(Person.newBuilder()
                        .setSsn("1234567890")
                        .setFirstName("Jimmy")
                        .setLastName("Jones").build())
                .addAttendees(Person.newBuilder()
                        .setSsn("9999999999")
                        .setFirstName("Dennis")
                        .setLastName("Richie").build())
                .setVenue(venue)
                .setRoom(room)
                .setTimestamp(timestamp)
                .build();

        Reservation createdReservationResponse = reservationClient.createReservation(
                CreateReservationRequest.newBuilder()
                        .setReservation(newReservation)
                        .build());

        String createdReservationId = createdReservationResponse.getId();
        System.out.println("response: " + createdReservationResponse);
        return createdReservationId;
    }
}
