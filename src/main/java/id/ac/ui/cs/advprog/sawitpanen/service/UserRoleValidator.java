package id.ac.ui.cs.advprog.sawitpanen.service;

import java.util.UUID;

public interface UserRoleValidator {
    boolean isValidRole(UUID userId, String expectedRole);
}
