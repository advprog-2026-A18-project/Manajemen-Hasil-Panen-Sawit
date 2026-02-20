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
        this.panen = new Panen();
        UUID mockId = UUID.randomUUID();
        UUID mockBuruhId = UUID.randomUUID();
        LocalDate today = LocalDate.now();
        List<String> fotoList = List.of("foto_a.jpg", "foto_b.jpg");

        // Act
        this.panen.setId(mockId);
        this.panen.setBuruhId(mockBuruhId);
        this.panen.setKuantitas(420);
        this.panen.setBuktiFoto(fotoList);
        this.panen.setTanggalMulai(today);
        this.panen.setTanggalAkhir(today.plusDays(3));
        this.panen.setTanggalPanen(today.plusDays(4));
        this.panen.setStatus(StatusPanen.REPORTED);
    }

    @Test
    void testGetPanenKuantitas() {
        assertEquals(420, this.panen.getKuantitas());
    }
}
