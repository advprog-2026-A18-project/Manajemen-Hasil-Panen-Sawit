package id.ac.ui.cs.advprog.sawitpanen.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "internal.grpc.auth", name = "enabled", havingValue = "false")
public class NoopUserRoleValidator implements UserRoleValidator {
    @Override
    public boolean isValidRole(UUID userId, String expectedRole) {
        return userId != null && expectedRole != null && !expectedRole.isBlank();
    }
}
