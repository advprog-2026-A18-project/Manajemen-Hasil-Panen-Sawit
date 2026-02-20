package id.ac.ui.cs.advprog.sawitpanen.repository;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PanenRepositoryTest {
    @InjectMocks
    PanenRepository panenRepository;

    @BeforeEach
    void setup() {}

    @Test
    void testCreateAndFind() {
        Panen panen = new Panen();
        List<String> fotos = new ArrayList<>();
        fotos.add("foto_depan.jpg");
        fotos.add("foto_samping.png");
        panen.setId(1);
        panen.setBuruhId(105);
        panen.setKuantitas(50);
        panen.setBuktiFoto(fotos);
        panen.setTanggalMulai(1704067200); // Unix timestamp format
        panen.setTanggalAkhir(1704153600);
        panen.setTanggalPanen(1704240000);
        panen.setStatus(StatusPanen.APPROVED);
        panenRepository.createPanen(panen);

        Iterator<Panen> panenIterator = panenRepository.findAll();
        assertTrue(panenIterator.hasNext());

        Panen savedPanen = panenIterator.next();
        assertEquals(panen.getId(), savedPanen.getId());
        assertEquals(panen.getBuruhId(), savedPanen.getBuruhId());
        assertEquals(panen.getKuantitas(), savedPanen.getKuantitas());
    }

    @Test
    void testFindAllIfEmpty() {
        Iterator<Panen> panenIterator = panenRepository.findAll();
        assertFalse(panenIterator.hasNext());
    }

    @Test
    void testFindAllIfMoreThanOne() {
        Panen panen1 = new Panen();
        panen1.setId(9000);
        panen1.setBuruhId(1);
        panen1.setKuantitas(1080);
        panen1.setBuktiFoto(Arrays.asList(
                "selfie_with_giant_tomato.jpg",
                "harvest_visible_from_space.png"
        ));
        panen1.setTanggalMulai(1770000000);
        panen1.setTanggalAkhir(1770086400);
        panen1.setTanggalPanen(1770172800);
        panen1.setStatus(StatusPanen.APPROVED);
        panenRepository.createPanen(panen1);

        Panen panen2 = new Panen();
        panen2.setId(404);
        panen2.setBuruhId(13);
        panen2.setKuantitas(5);
        panen2.setBuktiFoto(Arrays.asList(
                "blurry_thumb.jpg",
                "just_a_picture_of_dirt.png",
                "crying_emoji_screenshot.jpeg"
        ));
        panen2.setTanggalMulai(1770172800);
        panen2.setTanggalPanen(1770000000);
        panen2.setStatus(StatusPanen.REJECTED);
        panenRepository.createPanen(panen2);

        Iterator<Panen> panenIterator = panenRepository.findAll();
        assertTrue(panenIterator.hasNext());

        Panen savedPanen = panenIterator.next();
        assertEquals(panen1.getId(), savedPanen.getId());

        assertTrue(panenIterator.hasNext());
        savedPanen = panenIterator.next();
        assertEquals(panen2.getId(), savedPanen.getId());

        assertFalse(panenIterator.hasNext());
    }
}
