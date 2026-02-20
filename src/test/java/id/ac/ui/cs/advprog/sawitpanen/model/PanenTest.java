package id.ac.ui.cs.advprog.sawitpanen.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PanenTest {
    Panen panen;

    @BeforeEach
    void setUp() {
        this.panen = new Panen();
        this.panen.setId(69);
        this.panen.setBuruhId(67);
        this.panen.setKuantitas(420);

        List<String> buktiFoto = new ArrayList<>();
        buktiFoto.add("screenshot.png");
        this.panen.setBuktiFoto(buktiFoto);

        this.panen.setTanggalMulai(100);
        this.panen.setTanggalAkhir(404);
        this.panen.setTanggalPanen(360);

        this.panen.setStatus(StatusPanen.Approved);
    }

    @Test
    void testGetPanenId() {
        assertEquals(69, this.panen.getId());
    }

    @Test
    void testGetPanenBuruhId() {
        assertEquals(67, this.panen.getBuruhId());
    }

    @Test
    void testGetPanenKuantitas() {
        assertEquals(420, this.panen.getKuantitas());
    }
}
