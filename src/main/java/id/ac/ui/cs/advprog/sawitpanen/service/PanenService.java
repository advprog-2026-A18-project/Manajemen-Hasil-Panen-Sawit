package id.ac.ui.cs.advprog.sawitpanen.service;

import id.ac.ui.cs.advprog.sawitpanen.dto.ApprovalRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.CreatePanenRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.PanenResponse;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PanenService {
    PanenResponse createLaporan(CreatePanenRequest request);
    List<PanenResponse> getAllPanen();
    Page<PanenResponse> getPanenByFilter(
            UUID buruhId,
            LocalDate tanggalMulai,
            LocalDate tanggalAkhir,
            LocalDate tanggalPanen,
            StatusPanen status,
            Pageable pageable);
    PanenResponse getPanenById(UUID panenId);
    PanenResponse processApproval(UUID panenId, UUID mandorId, ApprovalRequest request);
}
