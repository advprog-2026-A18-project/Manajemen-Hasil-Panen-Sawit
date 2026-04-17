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

    // Perhatikan: Kita menyuntikkan mock ke class Implementasinya, bukan interface-nya
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

        // Setup request untuk createLaporan
        createRequest = new CreatePanenRequest(
                dummyBuruhId,
                100,
                "Panen lancar",
                List.of("http://foto.com/1.jpg"));

        // Setup entity dummy untuk di-return oleh repository
        dummyPanen = new Panen();
        dummyPanen.setId(dummyPanenId);
        dummyPanen.setBuruhId(dummyBuruhId);
        dummyPanen.setKuantitasBerat(100);
        dummyPanen.setBerita("Panen lancar");
        dummyPanen.setBuktiFoto(List.of("http://foto.com/1.jpg"));
        dummyPanen.setTanggalPanen(LocalDate.now());
        dummyPanen.setStatus(StatusPanen.REPORTED);
    }

    // ==========================================
    // TEST: createLaporan
    // ==========================================

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

    // ==========================================
    // TEST: getPanenByFilter
    // ==========================================

    @Test
    void getPanenByFilter_Sukses_MengembalikanPage() {
        // Setup data dan pageable
        Pageable pageable = PageRequest.of(0, 10);
        Page<Panen> pagedResponse = new PageImpl<>(List.of(dummyPanen));

        // Karena menggunakan Specification, kita pakai "any(Specification.class)"
        when(panenRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(pagedResponse);

        Page<PanenResponse> result = panenService.getPanenByFilter(
                dummyBuruhId, null, null, null, StatusPanen.REPORTED, pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(dummyBuruhId, result.getContent().get(0).getBuruhId());
    }

    // ==========================================
    // TEST: processApproval
    // ==========================================

    @Test
    void processApproval_Sukses_SaatDisetujui() {
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
        approvalReq.setPesanPenolakan(""); // Alasan kosong!

        when(panenRepository.findById(dummyPanenId)).thenReturn(Optional.of(dummyPanen));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            panenService.processApproval(dummyPanenId, mandorId, approvalReq);
        });

        assertTrue(exception.getMessage().contains("Alasan penolakan wajib diisi"));
        // Pastikan tidak ada data yang tersimpan ke database kalau validasi gagal
        verify(panenRepository, never()).save(any(Panen.class));
    }
}