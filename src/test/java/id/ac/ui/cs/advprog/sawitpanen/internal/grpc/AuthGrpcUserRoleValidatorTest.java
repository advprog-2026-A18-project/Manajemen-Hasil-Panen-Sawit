package id.ac.ui.cs.advprog.sawitpanen.internal.grpc;

import id.ac.ui.cs.advprog.mysawit.grpc.auth.AuthInternalServiceGrpc;
import id.ac.ui.cs.advprog.mysawit.grpc.auth.ValidateUserRoleRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.auth.ValidateUserRoleResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthGrpcUserRoleValidatorTest {

    @Mock
    private ManagedChannel channel;

    @SuppressWarnings("rawtypes")
    @Mock
    private ManagedChannelBuilder channelBuilder;

    @Mock
    private AuthInternalServiceGrpc.AuthInternalServiceBlockingStub authStub;

    private MockedStatic<ManagedChannelBuilder> mockedChannelBuilder;
    private MockedStatic<AuthInternalServiceGrpc> mockedAuthGrpc;

    private AuthGrpcUserRoleValidator validator;

    @BeforeEach
    void setUp() {
        mockedChannelBuilder = mockStatic(ManagedChannelBuilder.class);
        mockedAuthGrpc = mockStatic(AuthInternalServiceGrpc.class);

        mockedChannelBuilder.when(() -> ManagedChannelBuilder.forAddress(anyString(), anyInt()))
                .thenReturn(channelBuilder);
        when(channelBuilder.usePlaintext()).thenReturn(channelBuilder);
        when(channelBuilder.build()).thenReturn(channel);

        mockedAuthGrpc.when(() -> AuthInternalServiceGrpc.newBlockingStub(channel))
                .thenReturn(authStub);

        AuthGrpcProperties properties = new AuthGrpcProperties();
        validator = new AuthGrpcUserRoleValidator(properties);
    }

    @AfterEach
    void tearDown() {
        mockedChannelBuilder.close();
        mockedAuthGrpc.close();
    }

    @Test
    void isValidRole_nullUserId_returnsFalse() {
        assertFalse(validator.isValidRole(null, "ADMIN"));
    }

    @Test
    void isValidRole_nullExpectedRole_returnsFalse() {
        assertFalse(validator.isValidRole(UUID.randomUUID(), null));
    }

    @Test
    void isValidRole_blankExpectedRole_returnsFalse() {
        assertFalse(validator.isValidRole(UUID.randomUUID(), "   "));
    }

    @Test
    void isValidRole_validInputReturnsTrue() {
        ValidateUserRoleResponse response = ValidateUserRoleResponse.newBuilder()
                .setValid(true)
                .build();
        when(authStub.validateUserRole(any(ValidateUserRoleRequest.class))).thenReturn(response);

        assertTrue(validator.isValidRole(UUID.randomUUID(), "ADMIN"));
    }

    @Test
    void isValidRole_validInputReturnsFalse() {
        ValidateUserRoleResponse response = ValidateUserRoleResponse.newBuilder()
                .setValid(false)
                .build();
        when(authStub.validateUserRole(any(ValidateUserRoleRequest.class))).thenReturn(response);

        assertFalse(validator.isValidRole(UUID.randomUUID(), "ADMIN"));
    }

    @Test
    void shutdown_channelTerminatesInTime() throws Exception {
        when(channel.awaitTermination(3, TimeUnit.SECONDS)).thenReturn(true);

        validator.shutdown();

        verify(channel).shutdown();
        verify(channel).awaitTermination(3, TimeUnit.SECONDS);
        verify(channel, never()).shutdownNow();
    }

    @Test
    void shutdown_channelDoesNotTerminateInTime() throws Exception {
        when(channel.awaitTermination(3, TimeUnit.SECONDS)).thenReturn(false);

        validator.shutdown();

        verify(channel).shutdown();
        verify(channel).awaitTermination(3, TimeUnit.SECONDS);
        verify(channel).shutdownNow();
    }
}