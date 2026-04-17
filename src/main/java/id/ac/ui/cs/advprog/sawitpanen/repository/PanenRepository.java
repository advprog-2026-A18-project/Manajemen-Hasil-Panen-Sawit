package id.ac.ui.cs.advprog.sawitpanen.repository;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PanenRepository extends JpaRepository<Panen, UUID>, JpaSpecificationExecutor<Panen> {
    List<Panen> findByBuruhId(UUID buruhId);

    boolean existsByBuruhIdAndTanggalPanen(UUID buruhId, LocalDate tanggalPanen);
}
