package id.ac.ui.cs.advprog.sawitpanen.repository;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.model.StatusPanen;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PanenSpecification {
    private PanenSpecification() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Panen> buildFilter(
            UUID buruhId,
            LocalDate tanggalMulai,
            LocalDate tanggalAkhir,
            LocalDate tanggalPanen,
            StatusPanen status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (buruhId != null) {
                predicates.add(criteriaBuilder.equal(root.get("buruhId"), buruhId));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (tanggalPanen != null) {
                predicates.add(criteriaBuilder.equal(root.get("tanggalPanen"), tanggalPanen));
            } else {
                if (tanggalMulai != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("tanggalPanen"), tanggalMulai));
                }
                if (tanggalAkhir != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("tanggalPanen"), tanggalAkhir));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
