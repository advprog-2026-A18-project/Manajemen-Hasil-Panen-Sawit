package id.ac.ui.cs.advprog.sawitpanen.dto;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class PanenResponse {
    private UUID id;
    private UUID buruhId;
    private UUID mandorId;
    private int kuantitasBerat;
    private String berita;
    private List<String> buktiFoto;
    private LocalDate tanggalPanen;
    private StatusPanen status;
    private String pesanPenolakan;

    public PanenResponse(Panen entity) {
        this.id = entity.getId();
        this.buruhId = entity.getBuruhId();
        this.mandorId = entity.getMandorId();
        this.kuantitasBerat = entity.getKuantitasBerat();
        this.berita = entity.getBerita();
        this.buktiFoto = entity.getBuktiFoto();
        this.tanggalPanen = entity.getTanggalPanen();
        this.status = entity.getStatus();
        this.pesanPenolakan = entity.getPesanPenolakan();
    }
}