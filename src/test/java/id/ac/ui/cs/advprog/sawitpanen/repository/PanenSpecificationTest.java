package id.ac.ui.cs.advprog.sawitpanen.repository;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PanenSpecificationTest {

    @Autowired
    private PanenRepository panenRepository;

    private UUID buruh1;
    private UUID buruh2;
    private LocalDate hariIni;
    private LocalDate kemarin;

    @BeforeEach
    void setUp() {
        buruh1 = UUID.randomUUID();
        buruh2 = UUID.randomUUID();
        hariIni = LocalDate.now();
        kemarin = hariIni.minusDays(1);

        // Buat data sampel 1
        Panen p1 = new Panen();
        p1.setBuruhId(buruh1);
        p1.setTanggalPanen(hariIni);
        p1.setStatus(StatusPanen.REPORTED);
        p1.setBerita("Panen di daerah Sumatra");
        panenRepository.save(p1);

        // Buat data sampel 2
        Panen p2 = new Panen();
        p2.setBuruhId(buruh2);
        p2.setTanggalPanen(kemarin);
        p2.setStatus(StatusPanen.APPROVED);
        p2.setBerita("Panen melimpah ruah");
        panenRepository.save(p2);
    }

    @Test
    void illegalConstructiom() throws NoSuchMethodException {
        // 1. Ambil konstruktor private-nya
        Constructor<PanenSpecification> constructor = PanenSpecification.class.getDeclaredConstructor();

        // 2. Paksa agar bisa diakses (menembus aturan private)
        constructor.setAccessible(true);

        // 3. Eksekusi dan pastikan dia melempar exception seperti yang kita harapkan
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);

        // 4. Pastikan penyebab aslinya adalah IllegalStateException
        assertInstanceOf(IllegalStateException.class, exception.getCause());
        assertEquals("Utility class", exception.getCause().getMessage());
    }

    @Test
    void testFilter_ByBuruhId() {
        // Menguji if (buruhId != null)
        Specification<Panen> spec = PanenSpecification.buildFilter(buruh1, null, null, null, null);
        List<Panen> result = panenRepository.findAll(spec);

        assertEquals(1, result.size());
        assertEquals(buruh1, result.getFirst().getBuruhId());
    }

    @Test
    void testFilter_ByStatus() {
        // Menguji if (status != null)
        Specification<Panen> spec = PanenSpecification.buildFilter(null, null, null, null, StatusPanen.APPROVED);
        List<Panen> result = panenRepository.findAll(spec);

        assertEquals(1, result.size());
        assertEquals(StatusPanen.APPROVED, result.get(0).getStatus());
    }

    @Test
    void testFilter_ByTanggalPanenSpesifik() {
        // Menguji if (tanggalPanen != null)
        Specification<Panen> spec = PanenSpecification.buildFilter(null, null, null, hariIni, null);
        List<Panen> result = panenRepository.findAll(spec);

        assertEquals(1, result.size());
        assertEquals(hariIni, result.get(0).getTanggalPanen());
    }

    @Test
    void testFilter_ByRentangTanggalMulaiDanAkhir() {
        // Menguji blok else -> if (tanggalMulai != null) dan if (tanggalAkhir != null)
        Specification<Panen> spec = PanenSpecification.buildFilter(null, kemarin, kemarin, null, null);
        List<Panen> result = panenRepository.findAll(spec);

        assertEquals(1, result.size());
        assertEquals(kemarin, result.get(0).getTanggalPanen());
    }

    @Test
    void testFilter_SemuaKosong() {
        // Menguji skenario ketika semua parameter null
        Specification<Panen> spec = PanenSpecification.buildFilter(null, null, null, null, null);
        List<Panen> result = panenRepository.findAll(spec);

        // Karena ada 2 data di database dan tidak difilter, hasilnya harus 2
        assertEquals(2, result.size());
    }
}