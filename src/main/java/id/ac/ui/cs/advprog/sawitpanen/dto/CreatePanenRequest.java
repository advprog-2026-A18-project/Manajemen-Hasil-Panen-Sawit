package id.ac.ui.cs.advprog.sawitpanen.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreatePanenRequest {

    @NotNull()
    private UUID buruhId;

    @Min(value = 1, message = "Kuantitas panen minimal 1 kg")
    private int kuantitasBerat;

    @NotBlank(message = "Berita panen tidak boleh kosong")
    private String berita;

    @NotNull()
    @Size(min = 1, message = "Minimal harus ada 1 bukti foto")
    private List<String> buktiFoto;

}
