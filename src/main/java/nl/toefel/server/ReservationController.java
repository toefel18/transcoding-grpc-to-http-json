package nl.toefel.server;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import nl.toefel.reservations.v1.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ReservationController extends ReservationServiceGrpc.ReservationServiceImplBase {

    private ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void createReservation(CreateReservationRequest request, StreamObserver<Reservation> responseObserver) {
        System.out.println("createReservation() called");
        Reservation createdReservation = reservationRepository.createReservation(request.getReservation());
        responseObserver.onNext(createdReservation);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteReservation(DeleteReservationRequest request, StreamObserver<Empty> responseObserver) {
        System.out.println("deleteReservation() called");
        reservationRepository.deleteReservation(request.getId());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getReservation(GetReservationRequest request, StreamObserver<Reservation> responseObserver) {
        System.out.println("getReservation() called");
        Optional<Reservation> optionalReservation = reservationRepository.findReservation(request.getId());
        if (optionalReservation.isPresent()) {
            responseObserver.onNext(optionalReservation.orElse(Reservation.newBuilder().build()));
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("no reservation with id " + request.getId())
                    .asRuntimeException());
        }
    }

    @Override
    public void listReservations(ListReservationsRequest request, StreamObserver<Reservation> responseObserver) {
        Stream<Reservation> result = reservationRepository.listReservations().stream();

        if (!request.getVenue().isBlank()) {
            result = result.filter(it -> request.getVenue().equals(it.getVenue()));
        }
        if (!request.getTimestamp().isBlank()) {
            result = result.filter(it -> request.getTimestamp().equals(it.getTimestamp()));
        }

        result.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }
}
