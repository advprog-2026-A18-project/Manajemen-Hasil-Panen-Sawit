package id.ac.ui.cs.advprog.sawitpanen.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponse<T> {
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private T message;
}