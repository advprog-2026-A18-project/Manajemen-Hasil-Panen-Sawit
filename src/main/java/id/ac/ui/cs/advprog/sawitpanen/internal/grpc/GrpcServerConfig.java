package id.ac.ui.cs.advprog.sawitpanen.internal.grpc;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GrpcServerConfig {

    @Bean
    @ConditionalOnProperty(prefix = "grpc.server", name = "enabled", havingValue = "true", matchIfMissing = true)
    SmartLifecycle panenGrpcServer(
            PanenInternalGrpcService panenInternalGrpcService,
            @Value("${grpc.server.port:9093}") int grpcPort
    ) {
        return new PanenGrpcServerLifecycle(panenInternalGrpcService, grpcPort);
    }

    private static class PanenGrpcServerLifecycle implements SmartLifecycle {
        private final PanenInternalGrpcService panenInternalGrpcService;
        private final int grpcPort;
        private Server server;
        private boolean running;

        private PanenGrpcServerLifecycle(PanenInternalGrpcService panenInternalGrpcService, int grpcPort) {
            this.panenInternalGrpcService = panenInternalGrpcService;
            this.grpcPort = grpcPort;
        }

        @Override
        public void start() {
            try {
                server = NettyServerBuilder.forPort(grpcPort)
                        .addService(panenInternalGrpcService)
                        .build()
                        .start();
                running = true;
            } catch (IOException e) {
                throw new IllegalStateException("Failed to start Panen gRPC server on port " + grpcPort, e);
            }
        }

        @Override
        public void stop() {
            if (server != null) {
                server.shutdown();
            }
            running = false;
        }

        @Override
        public boolean isRunning() {
            return running;
        }
    }
}
