package id.ac.ui.cs.advprog.sawitpanen.service;

import id.ac.ui.cs.advprog.sawitpanen.dto.CreatePanenRequest;
import id.ac.ui.cs.advprog.sawitpanen.dto.PanenResponse;
import id.ac.ui.cs.advprog.sawitpanen.model.Panen;

import java.util.List;
import java.util.UUID;

public interface PanenService {
    public Panen createLaporan(CreatePanenRequest request);
    public List<PanenResponse> getAllPanen();
}
