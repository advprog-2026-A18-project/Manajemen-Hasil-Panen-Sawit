package id.ac.ui.cs.advprog.sawitpanen.dto;

import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class ApprovalRequest {
    @NotNull
    private StatusPanen status;
    private String pesanPenolakan;
}
