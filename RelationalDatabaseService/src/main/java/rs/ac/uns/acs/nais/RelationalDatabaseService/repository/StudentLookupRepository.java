package rs.ac.uns.acs.nais.RelationalDatabaseService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.uns.acs.nais.RelationalDatabaseService.model.StudentLookup;

import java.util.Optional;

public interface StudentLookupRepository extends JpaRepository<StudentLookup, Long> {
    Optional<StudentLookup> findByStudentId(Long studentId);
}
