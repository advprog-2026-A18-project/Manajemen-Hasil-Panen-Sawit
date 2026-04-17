package id.ac.ui.cs.advprog.sawitpanen.exception;

import id.ac.ui.cs.advprog.sawitpanen.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleConflictException() {
        // 1. Arrange: Siapkan exception bohongan
        String errorMessage = "Laporan untuk hari ini sudah ada.";
        ConflictException ex = new ConflictException(errorMessage);

        // 2. Act: Panggil fungsinya secara langsung
        ResponseEntity<ErrorResponse<String>> response = exceptionHandler.handleConflictException(ex);

        // 3. Assert: Pastikan isinya sesuai dengan yang kamu tulis di class asli
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void testHandleBadRequestException() {
        // 1. Arrange
        String errorMessage = "Alasan penolakan wajib diisi";
        BadRequestException ex = new BadRequestException(errorMessage);

        // 2. Act
        ResponseEntity<ErrorResponse<String>> response = exceptionHandler.handleBadRequestException(ex);

        // 3. Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void testHandleGeneralException() {
        // 1. Arrange: Bikin Exception bawaan Java
        Exception ex = new Exception("Database meledak atau error aneh lainnya");

        // 2. Act
        ResponseEntity<ErrorResponse<String>> response = exceptionHandler.handleGeneralException(ex);

        // 3. Assert: Pastikan pesannya aman dan tidak membocorkan error asli ke user
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Terjadi kesalahan pada server. Silakan hubungi administrator.", response.getBody().getMessage());
    }
}