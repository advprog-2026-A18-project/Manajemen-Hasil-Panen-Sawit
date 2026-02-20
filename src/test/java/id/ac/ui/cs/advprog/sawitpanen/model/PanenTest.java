package id.ac.ui.cs.advprog.sawitpanen.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PanenTest {
    Panen panen;

    @BeforeEach
    void setUp() {
        Panen panen = new Panen();
        UUID mockId = UUID.randomUUID();
        UUID mockBuruhId = UUID.randomUUID();
        LocalDate today = LocalDate.now();
        List<String> fotoList = List.of("foto_a.jpg", "foto_b.jpg");

        // Act
        panen.setId(mockId);
        panen.setBuruhId(mockBuruhId);
        panen.setKuantitas(420);
        panen.setBuktiFoto(fotoList);
        panen.setTanggalMulai(today);
        panen.setTanggalAkhir(today.plusDays(3));
        panen.setTanggalPanen(today.plusDays(4));
        panen.setStatus(StatusPanen.REPORTED);
    }

    @Test
    void testGetPanenKuantitas() {
        assertEquals(420, this.panen.getKuantitas());
    }
}
