package id.ac.ui.cs.advprog.sawitpanen.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "panen")
@Getter @Setter
public class Panen {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // --- Relasi Model ---
    @Column(name = "buruh_id", nullable = false)
    private UUID buruhId;

    @Column(name = "mandor_id")
    private UUID mandorId;

    // --- Properti Panen ---
    @Column(name = "kuantitas_berat", nullable = false)
    private int kuantitasBerat;

    @Column(nullable = false)
    private String berita;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "bukti_foto")
    private List<String> buktiFoto;

    @Column(name = "tanggal_panen")
    private LocalDate tanggalPanen;

    // -- Status Approval --
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPanen status;

    @Column(name = "pesan_penolakan")
    private String pesanPenolakan;
}
