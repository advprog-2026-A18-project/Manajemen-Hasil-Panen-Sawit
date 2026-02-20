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
public class PanenRepositoryTest {
    @Autowired
    PanenRepository panenRepository;

    @BeforeEach
    void setup() {}

    @Test
    void testCreateAndFind() {
        Panen panen = new Panen();
        panen.setBuruhId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        panen.setKuantitas(150);
        panen.setBuktiFoto(List.of("foto_kebun_pagi.jpg"));
        panen.setTanggalMulai(LocalDate.of(2026, 2, 18));
        panen.setTanggalAkhir(null); // Still ongoing, hasn't finished yet
        panen.setTanggalPanen(null);
        panen.setStatus(StatusPanen.REPORTED);
        panenRepository.save(panen);

        Iterator<Panen> panenIterator = panenRepository.findAll().iterator();
        assertTrue(panenIterator.hasNext());

        Panen savedPanen = panenIterator.next();
        assertEquals(panen.getId(), savedPanen.getId());
        assertEquals(panen.getBuruhId(), savedPanen.getBuruhId());
        assertEquals(panen.getKuantitas(), savedPanen.getKuantitas());
    }

    @Test
    void testFindAllIfEmpty() {
        Iterator<Panen> panenIterator = panenRepository.findAll().iterator();
        assertFalse(panenIterator.hasNext());
    }

    @Test
    void testFindAllIfMoreThanOne() {
        Panen panen1 = new Panen();
        panen1.setBuruhId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        panen1.setKuantitas(500);
        panen1.setBuktiFoto(List.of("bukti_1.png", "bukti_2.png"));
        panen1.setTanggalMulai(LocalDate.of(2026, 2, 15));
        panen1.setTanggalAkhir(LocalDate.of(2026, 2, 17));
        panen1.setTanggalPanen(LocalDate.of(2026, 2, 17));
        panen1.setStatus(StatusPanen.APPROVED);
        panenRepository.save(panen1);

        Panen panen2 = new Panen();
        panen2.setBuruhId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        panen2.setKuantitas(0);
        panen2.setBuktiFoto(List.of("blur_pic.jpg"));
        panen2.setTanggalMulai(LocalDate.of(2026, 2, 19));
        panen2.setTanggalAkhir(LocalDate.of(2026, 2, 19));
        panen2.setTanggalPanen(null); // Never harvested because it was rejected
        panen2.setStatus(StatusPanen.REJECTED);
        panenRepository.save(panen2);

        Iterator<Panen> panenIterator = panenRepository.findAll().iterator();
        assertTrue(panenIterator.hasNext());

        Panen savedPanen = panenIterator.next();
        assertEquals(panen1.getId(), savedPanen.getId());

        assertTrue(panenIterator.hasNext());
        savedPanen = panenIterator.next();
        assertEquals(panen2.getId(), savedPanen.getId());

        assertFalse(panenIterator.hasNext());
    }
}
