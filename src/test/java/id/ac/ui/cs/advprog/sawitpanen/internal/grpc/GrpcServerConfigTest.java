package id.ac.ui.cs.advprog.sawitpanen.internal.grpc;

import id.ac.ui.cs.advprog.sawitpanen.repository.PanenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class GrpcServerConfigTest {

    @Test
    void panenGrpcServer_StartAndStop_ShouldManageLifecycleState() {
        GrpcServerConfig config = new GrpcServerConfig();
        SmartLifecycle lifecycle = config.panenGrpcServer(createGrpcService(), 0);

        assertFalse(lifecycle.isRunning());

        lifecycle.start();
        assertTrue(lifecycle.isRunning());

        lifecycle.stop();
        assertFalse(lifecycle.isRunning());
    }

    @Test
    void panenGrpcServer_StopBeforeStart_ShouldRemainNotRunning() {
        GrpcServerConfig config = new GrpcServerConfig();
        SmartLifecycle lifecycle = config.panenGrpcServer(createGrpcService(), 0);

        lifecycle.stop();

        assertFalse(lifecycle.isRunning());
    }

    @Test
    void panenGrpcServer_WhenPortAlreadyInUse_ShouldThrowIllegalStateException() throws IOException {
        int port = findAvailablePort();
        GrpcServerConfig config = new GrpcServerConfig();
        SmartLifecycle firstLifecycle = config.panenGrpcServer(createGrpcService(), port);
        SmartLifecycle secondLifecycle = config.panenGrpcServer(createGrpcService(), port);

        firstLifecycle.start();
        try {
            assertThrows(IllegalStateException.class, secondLifecycle::start);
            assertFalse(secondLifecycle.isRunning());
        } finally {
            firstLifecycle.stop();
        }
    }

    private PanenInternalGrpcService createGrpcService() {
        return new PanenInternalGrpcService(mock(PanenRepository.class));
    }

    private int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
