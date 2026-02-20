package id.ac.ui.cs.advprog.sawitpanen.repository;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PanenRepository {
    private List<Panen> panenData = new ArrayList<>();

    public Panen createPanen(Panen panen) {
        panenData.add(panen);
        return panen;
    }

    public Iterator<Panen> findAll() {
        return panenData.iterator();
    }

    public Iterator<Panen> findByBuruh(int buruhId) {
        List<Panen> filteredPanen = panenData.stream()
                .filter(p -> p.getBuruhId() == buruhId)
                .toList();

        return filteredPanen.iterator();
    }

    public Iterator<Panen> findByTanggalPanen(int tanggalPanen) {
        List<Panen> filteredPanen = panenData.stream()
                .filter(p -> p.getTanggalPanen() == tanggalPanen)
                .toList();

        return filteredPanen.iterator();
    }
}
