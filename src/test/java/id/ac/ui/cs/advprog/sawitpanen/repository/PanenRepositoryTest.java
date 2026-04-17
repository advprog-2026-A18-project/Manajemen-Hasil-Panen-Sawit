package id.ac.ui.cs.advprog.sawitpanen.repository;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PanenRepositoryTest {

    @Autowired
    private PanenRepository panenRepository;

    @Test
    void testExistsByBuruhIdAndTanggalPanen() {
        UUID buruhId = UUID.randomUUID();
        LocalDate hariIni = LocalDate.now();

        Panen panen = new Panen();
        panen.setBuruhId(buruhId);
        panen.setKuantitasBerat(100);
        panen.setBerita("Tes DB");
        panen.setTanggalPanen(hariIni);
        panen.setStatus(StatusPanen.REPORTED);
        panenRepository.save(panen);

        boolean ada = panenRepository.existsByBuruhIdAndTanggalPanen(buruhId, hariIni);
        boolean tidakAda = panenRepository.existsByBuruhIdAndTanggalPanen(buruhId, hariIni.minusDays(1));

        assertTrue(ada, "Harusnya bernilai true karena data baru di-insert");
        assertFalse(tidakAda, "Harusnya false karena kemarin tidak ada laporan");
    }
}