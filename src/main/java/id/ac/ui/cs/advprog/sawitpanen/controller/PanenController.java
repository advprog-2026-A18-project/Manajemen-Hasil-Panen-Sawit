package id.ac.ui.cs.advprog.sawitpanen.controller;

import id.ac.ui.cs.advprog.sawitpanen.dto.ApprovalRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.CreatePanenRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.PanenResponse;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import id.ac.ui.cs.advprog.sawitpanen.service.PanenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/panen")
public class PanenController {
    @Autowired
    private PanenService panenService;

    @PostMapping
    public ResponseEntity<PanenResponse> createLaporanPanen(
            @Valid @RequestBody CreatePanenRequest request) {
        PanenResponse laporanBaru = panenService.createLaporan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(laporanBaru);
    }

    @GetMapping
    public ResponseEntity<Page<PanenResponse>> getPanen(
            @RequestParam(name = "buruh_id", required = false) UUID buruhId,
            @RequestParam(name = "tanggal_mulai", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalMulai,
            @RequestParam(name = "tanggal_akhir", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalAkhir,
            @RequestParam(name = "tanggal_panen", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalPanen,
            @RequestParam(name = "status", required = false) StatusPanen status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("tanggalPanen").descending());
        Page<PanenResponse> hasilFilter = panenService.getPanenByFilter(
                buruhId, tanggalMulai, tanggalAkhir, tanggalPanen, status, pageable
        );

        return ResponseEntity.ok(hasilFilter);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PanenResponse> getPanenDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(panenService.getPanenById(id));
    }

    @PatchMapping("/{id}/approval")
    public ResponseEntity<PanenResponse> processApproval(
            @RequestHeader("X-User-Id") UUID mandorId,
            @PathVariable UUID id,
            @Valid @RequestBody ApprovalRequest request) {
        PanenResponse response = panenService.processApproval(id, mandorId, request);
        return ResponseEntity.ok(response);
    }
}
