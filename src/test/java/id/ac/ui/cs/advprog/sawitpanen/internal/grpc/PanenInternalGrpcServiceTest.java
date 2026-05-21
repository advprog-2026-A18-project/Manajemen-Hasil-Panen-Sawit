package id.ac.ui.cs.advprog.sawitpanen.internal.grpc;

import id.ac.ui.cs.advprog.mysawit.grpc.panen.GetPanenByBuruhIdResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.GetPanenByIdRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.GetPanenByIdsRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.GetPanenByIdsResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.PanenResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.PanenStatus;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.UserPanenRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.ValidatePanenApprovedRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.panen.ValidatePanenApprovedResponse;
import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import id.ac.ui.cs.advprog.sawitpanen.repository.PanenRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PanenInternalGrpcServiceTest {

    @Mock
    private PanenRepository panenRepository;

    @Mock
    private StreamObserver<PanenResponse> panenResponseObserver;

    @Mock
    private StreamObserver<GetPanenByIdsResponse> panenByIdsResponseObserver;

    @Mock
    private StreamObserver<GetPanenByBuruhIdResponse> panenByBuruhIdResponseObserver;

    @Mock
    private StreamObserver<ValidatePanenApprovedResponse> validationResponseObserver;

    private PanenInternalGrpcService grpcService;
    private UUID panenId;
    private UUID buruhId;
    private UUID mandorId;
    private Panen panen;

    @BeforeEach
    void setUp() {
        grpcService = new PanenInternalGrpcService(panenRepository);
        panenId = UUID.randomUUID();
        buruhId = UUID.randomUUID();
        mandorId = UUID.randomUUID();
        panen = createPanen(panenId, buruhId, mandorId, StatusPanen.APPROVED);
    }

    @Test
    void getPanenById_WhenFound_ReturnsPanen() {
        when(panenRepository.findById(panenId)).thenReturn(Optional.of(panen));

        grpcService.getPanenById(
                GetPanenByIdRequest.newBuilder().setPanenId(panenId.toString()).build(),
                panenResponseObserver
        );

        PanenResponse response = capturePanenResponse();
        assertTrue(response.getFound());
        assertEquals(panenId.toString(), response.getId());
        assertEquals(buruhId.toString(), response.getBuruhId());
        assertEquals(mandorId.toString(), response.getMandorId());
        assertEquals(120, response.getKuantitasBerat());
        assertEquals("Panen pagi", response.getBerita());
        assertEquals("2026-05-21", response.getTanggalPanen());
        assertEquals(PanenStatus.PANEN_STATUS_APPROVED, response.getStatus());
        assertEquals(2, response.getBuktiFotoCount());
    }

    @Test
    void getPanenById_WhenNotFound_ReturnsFoundFalse() {
        when(panenRepository.findById(panenId)).thenReturn(Optional.empty());

        grpcService.getPanenById(
                GetPanenByIdRequest.newBuilder().setPanenId(panenId.toString()).build(),
                panenResponseObserver
        );

        PanenResponse response = capturePanenResponse();
        assertFalse(response.getFound());
        assertEquals(panenId.toString(), response.getId());
    }

    @Test
    void getPanenById_WhenInvalidUuid_ReturnsInvalidArgument() {
        grpcService.getPanenById(
                GetPanenByIdRequest.newBuilder().setPanenId("not-a-uuid").build(),
                panenResponseObserver
        );

        assertInvalidArgument(panenResponseObserver);
    }

    @Test
    void getPanenByIds_WhenFound_ReturnsMatchingPanen() {
        UUID otherPanenId = UUID.randomUUID();
        Panen otherPanen = createPanen(otherPanenId, buruhId, null, StatusPanen.REPORTED);
        when(panenRepository.findAllById(List.of(panenId, otherPanenId))).thenReturn(List.of(panen, otherPanen));

        grpcService.getPanenByIds(
                GetPanenByIdsRequest.newBuilder()
                        .addPanenIds(panenId.toString())
                        .addPanenIds(otherPanenId.toString())
                        .build(),
                panenByIdsResponseObserver
        );

        ArgumentCaptor<GetPanenByIdsResponse> captor = ArgumentCaptor.forClass(GetPanenByIdsResponse.class);
        verify(panenByIdsResponseObserver).onNext(captor.capture());
        verify(panenByIdsResponseObserver).onCompleted();

        GetPanenByIdsResponse response = captor.getValue();
        assertEquals(2, response.getPanenCount());
        assertEquals(PanenStatus.PANEN_STATUS_APPROVED, response.getPanen(0).getStatus());
        assertEquals(PanenStatus.PANEN_STATUS_REPORTED, response.getPanen(1).getStatus());
    }

    @Test
    void getPanenByIds_WhenContainsInvalidUuid_ReturnsInvalidArgument() {
        grpcService.getPanenByIds(
                GetPanenByIdsRequest.newBuilder().addPanenIds("not-a-uuid").build(),
                panenByIdsResponseObserver
        );

        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(panenByIdsResponseObserver).onError(captor.capture());
        verify(panenByIdsResponseObserver, never()).onNext(any());
        verify(panenByIdsResponseObserver, never()).onCompleted();

        StatusRuntimeException error = assertInstanceOf(StatusRuntimeException.class, captor.getValue());
        assertEquals(Status.INVALID_ARGUMENT.getCode(), error.getStatus().getCode());
    }

    @Test
    void getPanenByBuruhId_WhenFound_ReturnsBuruhPanen() {
        when(panenRepository.findByBuruhId(buruhId)).thenReturn(List.of(panen));

        grpcService.getPanenByBuruhId(
                UserPanenRequest.newBuilder().setUserId(buruhId.toString()).build(),
                panenByBuruhIdResponseObserver
        );

        ArgumentCaptor<GetPanenByBuruhIdResponse> captor = ArgumentCaptor.forClass(GetPanenByBuruhIdResponse.class);
        verify(panenByBuruhIdResponseObserver).onNext(captor.capture());
        verify(panenByBuruhIdResponseObserver).onCompleted();

        GetPanenByBuruhIdResponse response = captor.getValue();
        assertEquals(1, response.getPanenCount());
        assertEquals(buruhId.toString(), response.getPanen(0).getBuruhId());
    }

    @Test
    void getPanenByBuruhId_WhenInvalidUuid_ReturnsInvalidArgument() {
        grpcService.getPanenByBuruhId(
                UserPanenRequest.newBuilder().setUserId("not-a-uuid").build(),
                panenByBuruhIdResponseObserver
        );

        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(panenByBuruhIdResponseObserver).onError(captor.capture());
        verify(panenByBuruhIdResponseObserver, never()).onNext(any());
        verify(panenByBuruhIdResponseObserver, never()).onCompleted();

        StatusRuntimeException error = assertInstanceOf(StatusRuntimeException.class, captor.getValue());
        assertEquals(Status.INVALID_ARGUMENT.getCode(), error.getStatus().getCode());
    }

    @Test
    void validatePanenApproved_WhenApproved_ReturnsValid() {
        when(panenRepository.findById(panenId)).thenReturn(Optional.of(panen));

        grpcService.validatePanenApproved(
                ValidatePanenApprovedRequest.newBuilder().setPanenId(panenId.toString()).build(),
                validationResponseObserver
        );

        ValidatePanenApprovedResponse response = captureValidationResponse();
        assertTrue(response.getValid());
        assertEquals(panenId.toString(), response.getPanenId());
        assertEquals("Panen sudah disetujui", response.getMessage());
        assertEquals(PanenStatus.PANEN_STATUS_APPROVED, response.getStatus());
    }

    @Test
    void validatePanenApproved_WhenReported_ReturnsInvalid() {
        Panen reportedPanen = createPanen(panenId, buruhId, null, StatusPanen.REPORTED);
        when(panenRepository.findById(panenId)).thenReturn(Optional.of(reportedPanen));

        grpcService.validatePanenApproved(
                ValidatePanenApprovedRequest.newBuilder().setPanenId(panenId.toString()).build(),
                validationResponseObserver
        );

        ValidatePanenApprovedResponse response = captureValidationResponse();
        assertFalse(response.getValid());
        assertEquals("Panen belum disetujui", response.getMessage());
        assertEquals(PanenStatus.PANEN_STATUS_REPORTED, response.getStatus());
    }

    @Test
    void validatePanenApproved_WhenNotFound_ReturnsInvalid() {
        when(panenRepository.findById(panenId)).thenReturn(Optional.empty());

        grpcService.validatePanenApproved(
                ValidatePanenApprovedRequest.newBuilder().setPanenId(panenId.toString()).build(),
                validationResponseObserver
        );

        ValidatePanenApprovedResponse response = captureValidationResponse();
        assertFalse(response.getValid());
        assertEquals("Data panen tidak ditemukan", response.getMessage());
    }

    @Test
    void validatePanenApproved_WhenInvalidUuid_ReturnsInvalidArgument() {
        grpcService.validatePanenApproved(
                ValidatePanenApprovedRequest.newBuilder().setPanenId("not-a-uuid").build(),
                validationResponseObserver
        );

        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(validationResponseObserver).onError(captor.capture());
        verify(validationResponseObserver, never()).onNext(any());
        verify(validationResponseObserver, never()).onCompleted();

        StatusRuntimeException error = assertInstanceOf(StatusRuntimeException.class, captor.getValue());
        assertEquals(Status.INVALID_ARGUMENT.getCode(), error.getStatus().getCode());
    }

    private PanenResponse capturePanenResponse() {
        ArgumentCaptor<PanenResponse> captor = ArgumentCaptor.forClass(PanenResponse.class);
        verify(panenResponseObserver).onNext(captor.capture());
        verify(panenResponseObserver).onCompleted();
        return captor.getValue();
    }

    private ValidatePanenApprovedResponse captureValidationResponse() {
        ArgumentCaptor<ValidatePanenApprovedResponse> captor =
                ArgumentCaptor.forClass(ValidatePanenApprovedResponse.class);
        verify(validationResponseObserver).onNext(captor.capture());
        verify(validationResponseObserver).onCompleted();
        return captor.getValue();
    }

    private void assertInvalidArgument(StreamObserver<PanenResponse> observer) {
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(observer).onError(captor.capture());
        verify(observer, never()).onNext(any());
        verify(observer, never()).onCompleted();

        StatusRuntimeException error = assertInstanceOf(StatusRuntimeException.class, captor.getValue());
        assertEquals(Status.INVALID_ARGUMENT.getCode(), error.getStatus().getCode());
    }

    private Panen createPanen(UUID id, UUID buruhId, UUID mandorId, StatusPanen status) {
        Panen item = new Panen();
        item.setId(id);
        item.setBuruhId(buruhId);
        item.setMandorId(mandorId);
        item.setKuantitasBerat(120);
        item.setBerita("Panen pagi");
        item.setBuktiFoto(List.of("foto-1.jpg", "foto-2.jpg"));
        item.setTanggalPanen(LocalDate.of(2026, 5, 21));
        item.setStatus(status);
        return item;
    }
}
