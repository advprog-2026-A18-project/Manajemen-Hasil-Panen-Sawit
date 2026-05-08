package id.ac.ui.cs.advprog.sawitpanen;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class SawitPanenApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
    }

    @Test
    void testMainMethod() {
        assertDoesNotThrow(() -> SawitPanenApplication.main(new String[] {}));
        assertNotNull(applicationContext);
    }
}