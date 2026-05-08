package id.ac.ui.cs.advprog.sawitpanen.dto;

import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalRequest {
    @NotNull
    private StatusPanen status;
    private String pesanPenolakan;
}
