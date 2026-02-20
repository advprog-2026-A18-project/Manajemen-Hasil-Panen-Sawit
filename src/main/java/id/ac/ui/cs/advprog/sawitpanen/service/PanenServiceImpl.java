package id.ac.ui.cs.advprog.sawitpanen.service;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.repository.PanenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class PanenServiceImpl implements PanenService {
    @Autowired
    private PanenRepository panenRepository;

    @Override
    public Panen lapor(Panen panen) {
        panenRepository.save(panen);
        return panen;
    }

    @Override
    public List<Panen> findAll() {
        return panenRepository.findAll();
    }

    public List<Panen> findByBuruhId(UUID buruhId) {
        return panenRepository.findByBuruhId(buruhId);
    }
}
