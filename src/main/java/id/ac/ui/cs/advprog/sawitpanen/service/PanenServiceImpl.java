package id.ac.ui.cs.advprog.sawitpanen.service;

import id.ac.ui.cs.advprog.sawitpanen.dto.ApprovalRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.CreatePanenRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.PanenResponse;
import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import id.ac.ui.cs.advprog.sawitpanen.repository.PanenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PanenServiceImpl implements PanenService {
    @Autowired
    private PanenRepository panenRepository;

    @Override
    @Transactional
    public PanenResponse createLaporan(CreatePanenRequest request) {
        LocalDate hariIni = LocalDate.now();
        
        boolean sudahLaporan = panenRepository.existsByBuruhIdAndTanggalPanen(request.getBuruhId(), hariIni);
        if (sudahLaporan)
            throw new RuntimeException("Buruh sudah melaporkan hasil panen hari ini");

        Panen panen = new Panen();
        panen.setBuruhId(request.getBuruhId());
        panen.setKuantitasBerat(request.getKuantitasBerat());
        panen.setBerita(request.getBerita());
        panen.setBuktiFoto(request.getBuktiFoto());
        panen.setTanggalPanen(hariIni);
        panen.setStatus(StatusPanen.REPORTED);

        Panen savedEntity = panenRepository.save(panen);
        return new PanenResponse(savedEntity);
    }

    @Override
    public List<PanenResponse> getAllPanen() {
        return panenRepository.findAll().stream()
                .map(PanenResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public PanenResponse getPanenById(UUID panenId) {
        Panen panen = panenRepository.findById(panenId)
                .orElseThrow(() -> new RuntimeException("Data panen tidak ditemukan"));
        return new PanenResponse(panen);
    }

    @Override
    @Transactional
    public PanenResponse processApproval(UUID panenId, UUID mandorId, ApprovalRequest request) {
        Panen panen = panenRepository.findById(panenId)
                .orElseThrow(() -> new RuntimeException("Data panen tidak ditemukan"));

        if (panen.getStatus() != StatusPanen.REPORTED) {
            throw new RuntimeException("Status panen sudah diproses sebelumnya.");
        }

        if (request.getStatus() == StatusPanen.REJECTED &&
                (request.getPesanPenolakan() == null || request.getPesanPenolakan().isBlank())) {
            throw new RuntimeException("Alasan penolakan wajib diisi.");
        }

        panen.setStatus(request.getStatus());
        panen.setMandorId(mandorId);
        panen.setPesanPenolakan(request.getPesanPenolakan());

        Panen updatedEntity = panenRepository.save(panen);
        return new PanenResponse(updatedEntity);
    }
}
