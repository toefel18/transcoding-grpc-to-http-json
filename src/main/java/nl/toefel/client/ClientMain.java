package nl.toefel.client;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Durations;
import com.google.protobuf.util.Timestamps;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nl.toefel.reservations.*;

import java.time.ZonedDateTime;

import static nl.toefel.reservations.ReservationServiceGrpc.ReservationServiceBlockingStub;

public class ClientMain {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 53000)
                .usePlaintext() // disable TLS which is enabled by default and requires certificates
                .build();

        System.out.println("--- Getting reservation 123 which does not exist");
        ReservationServiceBlockingStub reservationClient = ReservationServiceGrpc.newBlockingStub(channel);
        Reservation nonExistingReservation = reservationClient.getReservation(GetReservationRequest.newBuilder()
                .setId("123")
                .build());
        System.out.println("response:" + nonExistingReservation);



        System.out.println("--- Creating reservation ");
        Reservation newReservation = Reservation.newBuilder()
                .setTitle("Lunchmeeting")
                .setAttendees(2)
                .setVenue("JDriven Coltbaan 3")
                .setTimestamp("2018-10-10T11:12:13")
                .build();

        CreateReservationResponse createdReservationResponse = reservationClient.createReservation(newReservation);
        String createdReservationId = createdReservationResponse.getReservation().getId();
        System.out.println("response: " + createdReservationResponse);




        System.out.println("--- Getting reservation with id " + createdReservationId);
        Reservation existingReservation = reservationClient.getReservation(GetReservationRequest.newBuilder()
                .setId(createdReservationId)
                .build());
        System.out.println("response: " + existingReservation);




        System.out.println("--- Deleting reservation with id " + createdReservationId);
        DeleteReservationResponse deleteReservationResponse = reservationClient.deleteReservation(DeleteReservationRequest.newBuilder()
                .setId(createdReservationId)
                .build());
        System.out.println("response: " + deleteReservationResponse);



        System.out.println("--- Getting reservation with id " + createdReservationId);
        Reservation existingReservation2 = reservationClient.getReservation(GetReservationRequest.newBuilder()
                .setId(createdReservationId)
                .build());
        System.out.println("response: " + existingReservation2);

    }
}
