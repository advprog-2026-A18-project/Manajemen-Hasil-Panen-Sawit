package id.ac.ui.cs.advprog.sawitpanen.service;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;

import java.util.List;
import java.util.UUID;

public interface PanenService {
    public Panen lapor(Panen panen);
    public List<Panen> findAll();
    public List<Panen> findByBuruhId(UUID buruhId);
}
