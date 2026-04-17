package id.ac.ui.cs.advprog.sawitpanen.service;

import id.ac.ui.cs.advprog.sawitpanen.dto.ApprovalRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.CreatePanenRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.PanenResponse;
import id.ac.ui.cs.advprog.sawitpanen.exception.BadRequestException;
import id.ac.ui.cs.advprog.sawitpanen.exception.ConflictException;
import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import id.ac.ui.cs.advprog.sawitpanen.repository.PanenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PanenServiceImplTest {

    @Mock
    private PanenRepository panenRepository;

    @InjectMocks
    private PanenServiceImpl panenService;

    private CreatePanenRequest createRequest;
    private Panen dummyPanen;
    private UUID dummyBuruhId;
    private UUID dummyPanenId;

    @BeforeEach
    void setUp() {
        dummyBuruhId = UUID.randomUUID();
        dummyPanenId = UUID.randomUUID();

        createRequest = new CreatePanenRequest(
                dummyBuruhId,
                100,
                "Panen lancar",
                List.of("http://foto.com/1.jpg"));

        dummyPanen = new Panen();
        dummyPanen.setId(dummyPanenId);
        dummyPanen.setBuruhId(dummyBuruhId);
        dummyPanen.setKuantitasBerat(100);
        dummyPanen.setBerita("Panen lancar");
        dummyPanen.setBuktiFoto(List.of("http://foto.com/1.jpg"));
        dummyPanen.setTanggalPanen(LocalDate.now());
        dummyPanen.setStatus(StatusPanen.REPORTED);
    }

    @Test
    void createLaporan_Sukses_JikaBelumLaporHariIni() {
        when(panenRepository.existsByBuruhIdAndTanggalPanen(eq(dummyBuruhId), any(LocalDate.class)))
                .thenReturn(false);
        when(panenRepository.save(any(Panen.class))).thenReturn(dummyPanen);

        PanenResponse response = panenService.createLaporan(createRequest);

        assertNotNull(response.getId());
        assertEquals(StatusPanen.REPORTED, response.getStatus());
        verify(panenRepository, times(1)).save(any(Panen.class));
    }

    @Test
    void createLaporan_Gagal_BilaLaporDuaKali() {
        when(panenRepository.existsByBuruhIdAndTanggalPanen(eq(dummyBuruhId), any(LocalDate.class)))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            panenService.createLaporan(createRequest);
        });

        verify(panenRepository, never()).save(any(Panen.class));
    }

    @Test
    void getPanenByFilter_Sukses_MengembalikanPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Panen> pagedResponse = new PageImpl<>(List.of(dummyPanen));

        when(panenRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(pagedResponse);

        Page<PanenResponse> result = panenService.getPanenByFilter(
                dummyBuruhId, null, null, null, StatusPanen.REPORTED, pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(dummyBuruhId, result.getContent().get(0).getBuruhId());
    }

    @Test
    void processApproval_Sukses_SaatDisetujui1() {
        UUID mandorId = UUID.randomUUID();
        ApprovalRequest approvalReq = new ApprovalRequest();
        approvalReq.setStatus(StatusPanen.APPROVED);

        when(panenRepository.findById(dummyPanenId)).thenReturn(Optional.of(dummyPanen));
        when(panenRepository.save(any(Panen.class))).thenAnswer(inv -> inv.getArgument(0));

        PanenResponse response = panenService.processApproval(dummyPanenId, mandorId, approvalReq);

        assertEquals(StatusPanen.APPROVED, response.getStatus());
        assertEquals(mandorId, response.getMandorId());
        verify(panenRepository, times(1)).save(any(Panen.class));
    }

    @Test
    void processApproval_Gagal_JikaDitolakTanpaAlasan() {
        UUID mandorId = UUID.randomUUID();
        ApprovalRequest approvalReq = new ApprovalRequest();
        approvalReq.setStatus(StatusPanen.REJECTED);
        approvalReq.setPesanPenolakan("");

        when(panenRepository.findById(dummyPanenId)).thenReturn(Optional.of(dummyPanen));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            panenService.processApproval(dummyPanenId, mandorId, approvalReq);
        });

        assertTrue(exception.getMessage().contains("Alasan penolakan wajib diisi"));
        verify(panenRepository, never()).save(any(Panen.class));
    }

    @Test
    void getAllPanen_Sukses_MengembalikanList() {
        when(panenRepository.findAll()).thenReturn(List.of(dummyPanen));

        List<PanenResponse> responses = panenService.getAllPanen();

        assertEquals(1, responses.size());
        assertEquals(dummyPanenId, responses.get(0).getId());
    }

    @Test
    void getPanenById_Sukses_MengembalikanData() {
        when(panenRepository.findById(dummyPanenId)).thenReturn(Optional.of(dummyPanen));

        PanenResponse response = panenService.getPanenById(dummyPanenId);

        assertNotNull(response);
        assertEquals(dummyPanenId, response.getId());
    }

    @Test
    void getPanenById_Gagal_DataTidakDitemukan() {
        when(panenRepository.findById(dummyPanenId)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            panenService.getPanenById(dummyPanenId);
        });

        assertEquals("Data panen tidak ditemukan", ex.getMessage());
    }

    @Test
    void processApproval_Gagal_DataTidakDitemukan() {
        UUID mandorId = UUID.randomUUID();
        ApprovalRequest req = new ApprovalRequest();

        when(panenRepository.findById(dummyPanenId)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            panenService.processApproval(dummyPanenId, mandorId, req);
        });

        assertEquals("Data panen tidak ditemukan", ex.getMessage());
    }

    @Test
    void processApproval_Gagal_StatusBukanReported() {
        UUID mandorId = UUID.randomUUID();
        ApprovalRequest req = new ApprovalRequest();

        dummyPanen.setStatus(StatusPanen.APPROVED);
        when(panenRepository.findById(dummyPanenId)).thenReturn(Optional.of(dummyPanen));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            panenService.processApproval(dummyPanenId, mandorId, req);
        });

        assertEquals("Status panen sudah diproses sebelumnya.", ex.getMessage());
    }

    @Test
    void processApproval_Gagal_TolakTapiAlasanMurniNull() {
        UUID mandorId = UUID.randomUUID();
        ApprovalRequest req = new ApprovalRequest();
        req.setStatus(StatusPanen.REJECTED);

        req.setPesanPenolakan(null);

        dummyPanen.setStatus(StatusPanen.REPORTED);
        when(panenRepository.findById(dummyPanenId)).thenReturn(Optional.of(dummyPanen));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            panenService.processApproval(dummyPanenId, mandorId, req);
        });

        assertEquals("Alasan penolakan wajib diisi.", ex.getMessage());
    }

    @Test
    void processApproval_Gagal_TolakTapiAlasanHanyaSpasi() {
        UUID mandorId = UUID.randomUUID();
        ApprovalRequest req = new ApprovalRequest();
        req.setStatus(StatusPanen.REJECTED);
        req.setPesanPenolakan("    ");

        dummyPanen.setStatus(StatusPanen.REPORTED);
        when(panenRepository.findById(dummyPanenId)).thenReturn(Optional.of(dummyPanen));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            panenService.processApproval(dummyPanenId, mandorId, req);
        });

        assertEquals("Alasan penolakan wajib diisi.", ex.getMessage());
    }

    @Test
    void processApproval_Sukses_SaatDisetujui() {
        UUID mandorId = UUID.randomUUID();
        ApprovalRequest req = new ApprovalRequest();
        req.setStatus(StatusPanen.APPROVED);
        req.setPesanPenolakan(null);

        dummyPanen.setStatus(StatusPanen.REPORTED);

        when(panenRepository.findById(dummyPanenId)).thenReturn(Optional.of(dummyPanen));
        when(panenRepository.save(any(Panen.class))).thenAnswer(inv -> inv.getArgument(0));

        PanenResponse response = panenService.processApproval(dummyPanenId, mandorId, req);

        assertEquals(StatusPanen.APPROVED, response.getStatus());
        assertEquals(mandorId, response.getMandorId());
    }
}