package id.ac.ui.cs.advprog.sawitpanen.service;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;

import java.util.List;

public interface PanenService {
    public Panen create(Panen panen);
    public List<Panen> findAll();
}
