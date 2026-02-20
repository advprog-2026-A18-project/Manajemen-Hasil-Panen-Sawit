package id.ac.ui.cs.advprog.sawitpanen.service;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.repository.PanenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class PanenServiceImpl implements PanenService {
    @Autowired
    private PanenRepository panenRepository;

    @Override
    public Panen create(Panen panen) {
        panenRepository.createPanen(panen);
        return panen;
    }

    @Override
    public List<Panen> findAll() {
        Iterator<Panen> panenIterator = panenRepository.findAll();
        List<Panen> allPanen = new ArrayList<>();
        panenIterator.forEachRemaining(allPanen::add);
        return allPanen;
    }
}
