package id.ac.ui.cs.advprog.sawitpanen.internal.grpc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthGrpcPropertiesTest {

    private AuthGrpcProperties properties;

    @BeforeEach
    void setUp() {
        properties = new AuthGrpcProperties();
    }

    @Test
    void defaultValues() {
        assertTrue(properties.isEnabled());
        assertEquals("localhost", properties.getHost());
        assertEquals(9091, properties.getPort());
    }

    @Test
    void setEnabled() {
        properties.setEnabled(false);
        assertFalse(properties.isEnabled());
    }

    @Test
    void setHost() {
        properties.setHost("192.168.1.1");
        assertEquals("192.168.1.1", properties.getHost());
    }

    @Test
    void setPort() {
        properties.setPort(8080);
        assertEquals(8080, properties.getPort());
    }
}