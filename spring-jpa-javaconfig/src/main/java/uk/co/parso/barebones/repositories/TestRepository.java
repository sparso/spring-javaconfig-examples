package uk.co.parso.barebones.repositories;

import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import uk.co.parso.barebones.entities.Test;

public interface TestRepository extends PagingAndSortingRepository<Test, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Test t WHERE id = :id")
    public Test selectForUpdate(@Param("id") Long id);
}
