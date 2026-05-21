package id.ac.ui.cs.advprog.sawitpanen.internal.grpc;

import id.ac.ui.cs.advprog.mysawit.grpc.panen.GetPanenByBuruhIdResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.GetPanenByIdRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.GetPanenByIdsRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.GetPanenByIdsResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.PanenInternalServiceGrpc;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.PanenResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.PanenStatus;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.UserPanenRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.ValidatePanenApprovedRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.ValidatePanenApprovedResponse;
import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import id.ac.ui.cs.advprog.sawitpanen.repository.PanenRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PanenInternalGrpcService extends PanenInternalServiceGrpc.PanenInternalServiceImplBase {
    private final PanenRepository panenRepository;

    public PanenInternalGrpcService(PanenRepository panenRepository) {
        this.panenRepository = panenRepository;
    }

    @Override
    public void getPanenById(GetPanenByIdRequest request, StreamObserver<PanenResponse> responseObserver) {
        UUID panenId = parseUuid(request.getPanenId(), "panen_id", responseObserver);
        if (panenId == null) {
            return;
        }

        PanenResponse response = panenRepository.findById(panenId)
                .map(this::toGrpcResponse)
                .orElseGet(() -> PanenResponse.newBuilder()
                        .setId(request.getPanenId())
                        .setFound(false)
                        .build());

        complete(responseObserver, response);
    }

    @Override
    public void getPanenByIds(GetPanenByIdsRequest request, StreamObserver<GetPanenByIdsResponse> responseObserver) {
        List<UUID> panenIds;
        try {
            panenIds = request.getPanenIdsList().stream()
                    .map(UUID::fromString)
                    .toList();
        } catch (IllegalArgumentException exception) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("panen_ids must contain valid UUID values")
                    .asRuntimeException());
            return;
        }

        GetPanenByIdsResponse response = GetPanenByIdsResponse.newBuilder()
                .addAllPanen(panenRepository.findAllById(panenIds).stream()
                        .map(this::toGrpcResponse)
                        .toList())
                .build();

        complete(responseObserver, response);
    }

    @Override
    public void getPanenByBuruhId(UserPanenRequest request, StreamObserver<GetPanenByBuruhIdResponse> responseObserver) {
        UUID buruhId = parseUuid(request.getUserId(), "user_id", responseObserver);
        if (buruhId == null) {
            return;
        }

        GetPanenByBuruhIdResponse response = GetPanenByBuruhIdResponse.newBuilder()
                .addAllPanen(panenRepository.findByBuruhId(buruhId).stream()
                        .map(this::toGrpcResponse)
                        .toList())
                .build();

        complete(responseObserver, response);
    }

    @Override
    public void validatePanenApproved(
            ValidatePanenApprovedRequest request,
            StreamObserver<ValidatePanenApprovedResponse> responseObserver
    ) {
        UUID panenId = parseUuid(request.getPanenId(), "panen_id", responseObserver);
        if (panenId == null) {
            return;
        }

        ValidatePanenApprovedResponse response = panenRepository.findById(panenId)
                .map(this::toValidationResponse)
                .orElseGet(() -> ValidatePanenApprovedResponse.newBuilder()
                        .setValid(false)
                        .setPanenId(request.getPanenId())
                        .setMessage("Data panen tidak ditemukan")
                        .build());

        complete(responseObserver, response);
    }

    private PanenResponse toGrpcResponse(Panen panen) {
        PanenResponse.Builder builder = PanenResponse.newBuilder()
                .setId(toString(panen.getId()))
                .setBuruhId(toString(panen.getBuruhId()))
                .setMandorId(toString(panen.getMandorId()))
                .setKuantitasBerat(panen.getKuantitasBerat())
                .setBerita(toString(panen.getBerita()))
                .setTanggalPanen(toString(panen.getTanggalPanen()))
                .setStatus(toGrpcStatus(panen.getStatus()))
                .setPesanPenolakan(toString(panen.getPesanPenolakan()))
                .setFound(true);

        if (panen.getBuktiFoto() != null) {
            builder.addAllBuktiFoto(panen.getBuktiFoto());
        }

        return builder.build();
    }

    private ValidatePanenApprovedResponse toValidationResponse(Panen panen) {
        boolean approved = panen.getStatus() == StatusPanen.APPROVED;
        return ValidatePanenApprovedResponse.newBuilder()
                .setValid(approved)
                .setPanenId(toString(panen.getId()))
                .setBuruhId(toString(panen.getBuruhId()))
                .setMandorId(toString(panen.getMandorId()))
                .setKuantitasBerat(panen.getKuantitasBerat())
                .setTanggalPanen(toString(panen.getTanggalPanen()))
                .setStatus(toGrpcStatus(panen.getStatus()))
                .setMessage(approved ? "Panen sudah disetujui" : "Panen belum disetujui")
                .build();
    }

    private PanenStatus toGrpcStatus(StatusPanen status) {
        if (status == StatusPanen.REPORTED) {
            return PanenStatus.PANEN_STATUS_REPORTED;
        }
        if (status == StatusPanen.APPROVED) {
            return PanenStatus.PANEN_STATUS_APPROVED;
        }
        if (status == StatusPanen.REJECTED) {
            return PanenStatus.PANEN_STATUS_REJECTED;
        }
        return PanenStatus.PANEN_STATUS_UNSPECIFIED;
    }

    private <T> UUID parseUuid(String value, String fieldName, StreamObserver<T> responseObserver) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(fieldName + " must be a valid UUID")
                    .asRuntimeException());
            return null;
        }
    }

    private <T> void complete(StreamObserver<T> responseObserver, T response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private String toString(UUID value) {
        return value == null ? "" : value.toString();
    }

    private String toString(LocalDate value) {
        return value == null ? "" : value.toString();
    }

    private String toString(String value) {
        return value == null ? "" : value;
    }
}
