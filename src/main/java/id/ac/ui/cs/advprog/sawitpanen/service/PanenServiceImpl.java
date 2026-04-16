package id.ac.ui.cs.advprog.sawitpanen.service;

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
    public Panen createLaporan(CreatePanenRequest request) {
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

        return panenRepository.save(panen);
    }

    @Override
    public List<PanenResponse> getAllPanen() {
        return panenRepository.findAll().stream()
                .map(PanenResponse::new)
                .collect(Collectors.toList());
    }
}
