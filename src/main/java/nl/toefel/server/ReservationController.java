package nl.toefel.server;

import io.grpc.stub.StreamObserver;
import nl.toefel.reservations.*;

import java.util.Optional;

class ReservationController extends ReservationServiceGrpc.ReservationServiceImplBase {

    private ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void createReservation(Reservation request, StreamObserver<CreateReservationResponse> responseObserver) {
        Reservation createdReservation = reservationRepository.createReservation(request);

        CreateReservationResponse createdResponse = CreateReservationResponse.newBuilder()
                .setReservation(createdReservation)
                .build();

        responseObserver.onNext(createdResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getReservation(GetReservationRequest request, StreamObserver<Reservation> responseObserver) {
        Optional<Reservation> optionalReservation = reservationRepository.findReservation(request.getId());
        System.out.println(request + " -> " + optionalReservation);
        responseObserver.onNext(optionalReservation.orElse(Reservation.newBuilder().build()));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteReservation(DeleteReservationRequest request, StreamObserver<DeleteReservationResponse> responseObserver) {
        reservationRepository.deleteReservation(request.getId());
        responseObserver.onNext(DeleteReservationResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
