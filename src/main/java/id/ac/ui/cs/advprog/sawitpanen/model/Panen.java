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

    @Column(name = "buruh_id", nullable = false)
    private UUID buruhId;

    @Column(nullable = false)
    private int kuantitas;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "bukti_foto")
    private List<String> buktiFoto;

    @Column(name = "tanggal_mulai")
    private LocalDate tanggalMulai;

    @Column(name = "tanggal_akhir")
    private LocalDate tanggalAkhir;

    @Column(name = "tanggal_panen")
    private LocalDate tanggalPanen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPanen status;
}
