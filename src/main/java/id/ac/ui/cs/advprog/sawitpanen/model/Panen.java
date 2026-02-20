package id.ac.ui.cs.advprog.sawitpanen.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Panen {
    private int id;

    private int buruhId;
    private int kuantitas;
    private List<String> buktiFoto;

    private String tanggalMulai;
    private String tanggalAkhir;
    private String tanggalPanen;

    private StatusPanen status;
}
