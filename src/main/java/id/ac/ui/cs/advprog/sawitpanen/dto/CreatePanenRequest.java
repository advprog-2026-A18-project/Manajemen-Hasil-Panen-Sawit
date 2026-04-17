package id.ac.ui.cs.advprog.sawitpanen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreatePanenRequest {
    private UUID buruhId;
    private int kuantitasBerat;
    private String berita;
    private List<String> buktiFoto;
}
