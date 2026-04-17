package id.ac.ui.cs.advprog.sawitpanen;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SawitPanenApplicationTest {

    // Test bawaan untuk memastikan aplikasi bisa menyala
    @Test
    void contextLoads() {
    }

    // Tambahkan test ini untuk menghijaukan Baris 10-11
    @Test
    void testMainMethod() {
        // Kita panggil fungsi main-nya secara langsung dengan argumen array kosong
        SawitPanenApplication.main(new String[] {});
    }
}